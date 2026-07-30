package fr.triplea.badasscouncil.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import fr.triplea.badasscouncil.dao.RoleRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.PasswordTransfer;
import fr.triplea.badasscouncil.dto.RefreshTokenTransfer;
import fr.triplea.badasscouncil.dto.UserCredentials;
import fr.triplea.badasscouncil.dto.UserTransfer;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.UserStatus;
import fr.triplea.badasscouncil.model.RefreshToken;
import fr.triplea.badasscouncil.model.Role;
import fr.triplea.badasscouncil.security.MyUserDetailsService;
import fr.triplea.badasscouncil.security.jwt.JwtTokenUtil;
import fr.triplea.badasscouncil.security.jwt.RefreshTokenException;
import fr.triplea.badasscouncil.security.jwt.RefreshTokenService;
import fr.triplea.badasscouncil.web.service.VariableService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sign")
public class AuthController 
{
   
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
  
  @Value("${password.salt}")
  private String salt;

  @Autowired
  public PasswordEncoder passwordEncoder;
  
  @Autowired
  public MyUserDetailsService myUserDetailsService;
  
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  RefreshTokenService refreshTokenService;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VariableService variableService;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;
  
  
  @PostMapping(value = "/in")
  public ResponseEntity<UserCredentials> signIn(@RequestBody UserCredentials uc, HttpServletRequest request)
  {
    Locale locale = localeResolver.resolveLocale(request);

    String captchaQuestion = variableService.getString("CAPTCHA", "LOGIN_QUESTION");
    String captchaResponse = variableService.getString("CAPTCHA", "LOGIN_RESPONSE");
    String captchaAnswer = uc.getAnswer(); 
    
    if (captchaQuestion == null) { captchaQuestion = ""; } else { captchaQuestion = captchaQuestion.trim(); }
    if (captchaResponse == null) { captchaResponse = ""; } else { captchaResponse = captchaResponse.trim(); }
    if (captchaAnswer == null) { captchaAnswer = ""; } else { captchaAnswer = captchaAnswer.trim(); }
     
    if (!captchaQuestion.isEmpty())
    {
      if (captchaAnswer.isEmpty())
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
        uc.setError(messageSource.getMessage("captcha.missing.answer", null, locale));     

        return ResponseEntity.ok(uc);
      }
      else if (!captchaAnswer.equalsIgnoreCase(captchaResponse)) 
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
        uc.setError(messageSource.getMessage("captcha.wrong.answer", null, locale));     

        return ResponseEntity.ok(uc);
      }
    }

    String usrn = uc.getLoginName(); if (usrn == null) { usrn = ""; } else { usrn = usrn.trim(); }
    String pass = uc.getPassword(); if (pass == null) { pass = ""; } else { pass = pass.trim(); }
    
    if (usrn.isEmpty() || pass.isEmpty()) { return ResponseEntity.notFound().build(); }
    
    User found = userRepository.findByLoginName(usrn);
        
    if (found != null)
    { 
      UserDetails userDetails = myUserDetailsService.loadUserByUsername(usrn);
    
      Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) ; 
      
      if (passwordEncoder.matches(salt + pass, userDetails.getPassword()))
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
          
          refreshTokenService.deleteByNumeroParticipant(found.getUserId());
          
          RefreshToken refreshToken = refreshTokenService.createRefreshToken(found.getUserId());
                  
          uc = new UserCredentials();
          
          uc.setUserId(found.getUserId());
          uc.setLoginName(usrn);
          uc.setPassword("<success@auth>");
          uc.setPasswordExpired(found.isPasswordExpired());
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
      
      if (found != null) { refreshTokenService.deleteByNumeroParticipant(found.getUserId()); }
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
  
  @PostMapping(value = "/subscribe")
  public ResponseEntity<PasswordTransfer> subscribe(@RequestBody(required = true) UserTransfer user, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);
   
    PasswordTransfer pt = new PasswordTransfer();
 
    String captchaQuestion = variableService.getString("CAPTCHA", "SUBSCRIBE_QUESTION");
    String captchaResponse = variableService.getString("CAPTCHA", "SUBSCRIBE_RESPONSE");
    String captchaAnswer = user.getAnswer(); 
    
    if (captchaQuestion == null) { captchaQuestion = ""; } else { captchaQuestion = captchaQuestion.trim(); }
    if (captchaResponse == null) { captchaResponse = ""; } else { captchaResponse = captchaResponse.trim(); }
    if (captchaAnswer == null) { captchaAnswer = ""; } else { captchaAnswer = captchaAnswer.trim(); }
     
    if (!captchaQuestion.isEmpty())
    {
      if (captchaAnswer.isEmpty())
      {
        pt.setError(messageSource.getMessage("captcha.missing.answer", null, locale)); return ResponseEntity.ok(pt);         
      }
      else if (!captchaAnswer.equalsIgnoreCase(captchaResponse)) 
      {
        pt.setError(messageSource.getMessage("captcha.wrong.answer", null, locale)); return ResponseEntity.ok(pt);         
      }
    }
     
    if (user.getSubscribeMotive().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.motive", null, locale)); return ResponseEntity.ok(pt); }
    if (user.getLoginName().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.login", null, locale)); return ResponseEntity.ok(pt); }
    if (user.getPassword().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.password", null, locale)); return ResponseEntity.ok(pt); }
    if (user.getNickName().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.nickname", null, locale)); return ResponseEntity.ok(pt); }
 
    Role adminRole = roleRepository.findByLabel("ROLE_ADMIN"); 
    Role regulRole = roleRepository.findByLabel("ROLE_REGUL"); 
    Role userRole = roleRepository.findByLabel("ROLE_USER");
        
    long cur = userRepository.count();
  
    long max = variableService.getLong("Quota", "MEMBERS_COUNT", 42);

    if (max > cur) { pt.setError(messageSource.getMessage("account.subscribe.quota.reached", null, locale)); return ResponseEntity.ok(pt); }
    
    boolean admin = false;

    User found = new User();
    
    found.setEnabled(true);

    List<Role> roles = new ArrayList<Role>();
    
    roles.add(userRole);

    if (cur < 1) 
    { 
      // admin for the first subscriber, without PENDING status.
      
      roles.add(adminRole);
      roles.add(regulRole);
      
      found.setStatus(UserStatus.ACTIVE);
      
      admin = true;
    } 
    else 
    { 
      found.setStatus(UserStatus.PENDING); 
    } 
    
    found.setRoles(roles);
    found.setSubscribeMotive(user.getSubscribeMotive());
    found.setLoginName(user.getLoginName().trim());
    
    final String mdp = user.getPassword().trim();

    found.setPasswordHash(passwordEncoder.encode(salt + mdp));

    found.setSessionTimeout(user.getSessionTimeout());
    
    found.setNickName(user.getNickName().trim());
    found.setGroupName(user.getGroupName().trim()); 
    found.setFirstName(user.getFirstName());
    found.setLastName(user.getLastName());
     
    found.setDisplayContactDetails(user.mustDisplayContactDetails());  
    found.setAddress(user.getAddress());
    found.setZipCode(user.getZipCode());
    found.setTown(user.getTown());
    found.setCountry(user.getCountry());
    found.setPhone(user.getPhone());
    found.setEmail(user.getEmail());
    
    found.setStorageLimit(variableService.getInt("Quota", "STORAGE_DEFAULT", 0));
    
    cur = userRepository.count(user.getLoginName().trim().toUpperCase());
    
    if (cur > 0) { pt.setError(messageSource.getMessage("account.subscribe.already.login", null, locale)); return ResponseEntity.ok(pt); }
    
    if (user.getGroupName().isBlank()) 
    {
      cur = userRepository.countForNickGroup(user.getNickName().trim().toUpperCase(), "");
      
      if (cur > 0) { pt.setError(messageSource.getMessage("account.subscribe.already.nickname", null, locale)); return ResponseEntity.ok(pt); }
    }
    else 
    {
      cur = userRepository.countForNickGroup(user.getNickName().trim().toUpperCase(), user.getGroupName().trim().toUpperCase());
      
      if (cur > 0) { pt.setError(messageSource.getMessage("account.subscribe.already.nickgroupname", null, locale)); return ResponseEntity.ok(pt); }
    }
    
    userRepository.saveAndFlush(found);

    pt.setError("");
    
    if (admin) { pt.setSuccess(messageSource.getMessage("account.subscribe.first", null, locale)); }
          else { pt.setSuccess(messageSource.getMessage("account.subscribe.success", null, locale)); }
    
    return ResponseEntity.ok(pt);
  }

}
