package fr.triplea.badasscouncil.web.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.RefreshTokenTransfer;
import fr.triplea.badasscouncil.dto.UserCredentials;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.UserStatus;
import fr.triplea.badasscouncil.model.RefreshToken;
import fr.triplea.badasscouncil.model.Role;
import fr.triplea.badasscouncil.security.MyUserDetailsService;
import fr.triplea.badasscouncil.security.jwt.JwtTokenUtil;
import fr.triplea.badasscouncil.security.jwt.RefreshTokenException;
import fr.triplea.badasscouncil.security.jwt.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sign")
public class AuthController 
{
   
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  public PasswordEncoder passwordEncoder;
  
  @Autowired
  public MyUserDetailsService myUserDetailsService;
  
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  RefreshTokenService refreshTokenService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;
  
  
  @PostMapping(value = "/in")
  public ResponseEntity<UserCredentials> signIn(@RequestBody UserCredentials uc, HttpServletRequest request)
  {
    Locale locale = localeResolver.resolveLocale(request);

    String usrn = uc.getLoginName(); if (usrn == null) { usrn = ""; } else { usrn = usrn.trim(); }
    String pass = uc.getPassword(); if (pass == null) { pass = ""; } else { pass = pass.trim(); }
    
    if (usrn.isEmpty() || pass.isEmpty()) { return ResponseEntity.notFound().build(); }
    
    User found = userRepository.findByLoginName(usrn);
        
    if (found != null)
    { 
      UserDetails userDetails = myUserDetailsService.loadUserByUsername(usrn);
    
      Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) ; 
      
      if (passwordEncoder.matches(pass, userDetails.getPassword()))
      {
        if (found.getStatus().equals(UserStatus.BANNED))
        {
          uc = new UserCredentials();
          
          uc.setUserId(0);
          uc.setLoginName("");
          uc.setPassword("");
          uc.setNickName("");
          uc.setGroupName("");
          uc.setSessionTimeout(15);
          uc.setAccessToken("");
          uc.setRefreshToken("");
          uc.setRole("");
          uc.setError(messageSource.getMessage("auth.user.banned", null, locale));
         
          return ResponseEntity.ok(uc);
        }
        else if (found.getStatus().equals(UserStatus.LOCKED))
        {
          uc = new UserCredentials();
          
          uc.setUserId(0);
          uc.setLoginName("");
          uc.setPassword("");
          uc.setNickName("");
          uc.setGroupName("");
          uc.setSessionTimeout(15);
          uc.setAccessToken("");
          uc.setRefreshToken("");
          uc.setRole("");
          uc.setError(messageSource.getMessage("auth.user.locked", null, locale));
         
          return ResponseEntity.ok(uc);
        }
        else
        {
          SecurityContextHolder.getContext().setAuthentication(authentication);
          
          String token = jwtTokenUtil.generateJwtToken(authentication);
          
          refreshTokenService.deleteByNumeroParticipant(found.getId());
          
          RefreshToken refreshToken = refreshTokenService.createRefreshToken(found.getId());
                  
          uc = new UserCredentials();
          
          uc.setUserId(found.getId());
          uc.setLoginName(usrn);
          uc.setPassword("<success@auth>");
          uc.setNickName(found.getNickName());
          uc.setGroupName(found.getGroupName());
          uc.setSessionTimeout(found.getSessionTimeout());
          uc.setAccessToken(token);
          uc.setRefreshToken(refreshToken.getToken());
          uc.setError("");

          List<Role> roles = found.getRoles();
           
          if (!(uc.hasRole())) { for (Role role : roles) { if (role.isRole("ADMIN")) { uc.setRole("ADMIN"); } } }
          if (!(uc.hasRole())) { for (Role role : roles) { if (role.isRole("REGUL")) { uc.setRole("REGUL"); } } }
          if (!(uc.hasRole())) { uc.setRole("USER"); }
          
          return ResponseEntity.ok(uc);
        }
      }
      else
      {
        uc = new UserCredentials();
        
        uc.setUserId(0);
        uc.setLoginName("");
        uc.setPassword("");
        uc.setNickName("");
        uc.setGroupName("");
        uc.setSessionTimeout(15);
        uc.setAccessToken("");
        uc.setRefreshToken("");
        uc.setRole("");
        uc.setError(messageSource.getMessage("auth.password.mismatches", null, locale));
       
        return ResponseEntity.ok(uc);
      }
    }
    
    uc = new UserCredentials();
    
    uc.setUserId(0);
    uc.setLoginName("");
    uc.setPassword("");
    uc.setNickName("");
    uc.setGroupName("");
    uc.setSessionTimeout(15);
    uc.setAccessToken("");
    uc.setRefreshToken("");
    uc.setRole("");
    uc.setError(messageSource.getMessage("auth.user.notfound", null, locale));
   
    return ResponseEntity.ok(uc);
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshTokenTransfer rtt, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    String refreshTokenActif = rtt.getRefreshToken();

    RefreshToken found = refreshTokenService.findByToken(refreshTokenActif);
    
    if (found == null) { throw new RefreshTokenException(refreshTokenActif, messageSource.getMessage("refreshtoken.notfound", null, locale)); }

    found = refreshTokenService.verifyExpiration(found);
    
    if (found == null) { throw new RefreshTokenException(refreshTokenActif, messageSource.getMessage("refreshtoken.expired", null, locale)); }

    User participant = found.getUser();
        
    rtt.setAccessToken(jwtTokenUtil.generateTokenFromPseudonyme(participant.getLoginName()));
    
    return ResponseEntity.ok(rtt);
  }
  
  @PostMapping("/out")
  public ResponseEntity<UserCredentials> signOut(final Authentication authentication)
  {
    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if (found != null) { refreshTokenService.deleteByNumeroParticipant(found.getId()); }
    }

    SecurityContextHolder.clearContext();
        
    UserCredentials uc = new UserCredentials();
    
    uc.setUserId(0);
    uc.setLoginName("");
    uc.setPassword("");
    uc.setNickName("");
    uc.setGroupName("");
    uc.setSessionTimeout(15);
    uc.setAccessToken("");
    uc.setRefreshToken("");
    uc.setRole("");

    return ResponseEntity.ok(uc);
  }
  
}
