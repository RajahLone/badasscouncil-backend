package fr.triplea.badasscouncil.web.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.HomeInformationTransfer;
import fr.triplea.badasscouncil.dto.PasswordTransfer;
import fr.triplea.badasscouncil.dto.UserTransfer;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Role;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AccountController 
{

  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private UserRepository userRepository;
  
  @Value("${password.salt}")
  private String salt;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LocaleResolver localeResolver;
 
  @Autowired
  private MessageSource messageSource;

  private final DateTimeFormatter dtf_fr = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
  private final DateTimeFormatter dft_en = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
 
  @GetMapping(value = "/form")
  public ResponseEntity<UserTransfer> getForm(final Authentication authentication, HttpServletRequest request) 
  {         
    Locale locale = localeResolver.resolveLocale(request);

    DateTimeFormatter dtf = this.dtf_fr; if (locale == Locale.ENGLISH) { dtf = this.dft_en; }

    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if (found != null) 
      { 
        UserTransfer p = new UserTransfer();
        
        p.setCreatedOn(found.hasCreatedOn() ? dtf.format(found.getCreatedOn()) : "");
        p.setUpdatedOn(found.hasUpdatedOn() ? dtf.format(found.getUpdatedOn()) : ""); 
        p.setUserId(found.getUserId());
        p.setStatus(found.getStatus().name());
        
        p.setSubscribeMotive(found.getSubscribeMotive());
        
        p.setLoginName(found.getLoginName());
        p.setSessionTimeout(15);
        
        p.setNickName(found.getNickName());
        p.setGroupName(found.getGroupName());
        p.setFirstName(found.getFirstName());
        p.setLastName(found.getLastName());
        
        p.setDisplayContactDetails(found.mustDisplayContactDetails());
        
        p.setAddress(found.getAddress());
        p.setZipCode(found.getZipCode());
        p.setTown(found.getTown());
        p.setCountry(found.getCountry());
        p.setPhone(found.getPhone());
        p.setEmail(found.getEmail());
        
        p.setStorageLimit(found.getStorageLimit());
                 
        p.setLastActivityOn(found.hasLastActivityOn() ? dtf.format(found.getLastActivityOn()) : "");
       
        List<Role> roles = found.getRoles();       
        
        if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("ADMIN")) { p.setRole("ADMIN"); } } }
        if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("REGUL")) { p.setRole("REGUL"); } } }
        if (!(p.hasRole())) { p.setRole("USER"); } 

        return ResponseEntity.ok(p); 
      }
    }
   
    return ResponseEntity.notFound().build();
  }
 
  @PutMapping(value = "/update")
  public ResponseEntity<Object> update(@RequestBody(required = true) UserTransfer user, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if (found != null)
      {
        found.setEnabled(true);

        found.setSubscribeMotive(user.getSubscribeMotive());

        found.setNickName(user.getNickName());
        found.setGroupName(user.getGroupName()); 
        found.setFirstName(user.getFirstName());
        found.setLastName(user.getLastName());
        
        found.setDisplayContactDetails(user.mustDisplayContactDetails());
        found.setAddress(user.getAddress());
        found.setZipCode(user.getZipCode());
        found.setTown(user.getTown());
        found.setCountry(user.getCountry());
        found.setPhone(user.getPhone());
        found.setEmail(user.getEmail());
               
        userRepository.saveAndFlush(found);
       
        HomeInformationTransfer mt = new HomeInformationTransfer();
        mt.setInfo(messageSource.getMessage("user.updated", null, locale));
        
        return ResponseEntity.ok(mt);
      }
    } 
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/newmdp")
  public ResponseEntity<PasswordTransfer> update(@RequestBody(required = true) PasswordTransfer mdpt, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if (found != null)
      {
        if (mdpt.getLoginName().equals(authentication.getName()))
        {    
          final String mdp_old = mdpt.getOldPassword();
          final String mdp_new = mdpt.getNewPassword();

          mdpt.setOldPassword("");
          mdpt.setNewPassword("");

          if (mdp_old == null) { mdpt.setError(messageSource.getMessage("account.password.old.missing", null, locale)); }
          else
          if (mdp_old.isBlank()) { mdpt.setError(messageSource.getMessage("account.password.old.missing", null, locale)); }
          else
          if (mdp_new == null) { mdpt.setError(messageSource.getMessage("account.password.new.missing", null, locale)); }
          else
          if (mdp_new.isBlank()) { mdpt.setError(messageSource.getMessage("account.password.new.missing", null, locale)); }
          else
          if (passwordEncoder.matches(salt + mdp_old, found.getPasswordHash()))
          {
            found.setPasswordHash(passwordEncoder.encode(salt + mdp_new.trim()));
            
            userRepository.saveAndFlush(found);

            mdpt.setOldPassword("<success@old>");
            mdpt.setNewPassword("<success@new>");
            mdpt.setError("");
            mdpt.setSuccess(messageSource.getMessage("account.password.changed", null, locale));
          }
          else { mdpt.setError(messageSource.getMessage("account.password.old.failed", null, locale)); }
          
        }
        else
        {
          mdpt.setError(messageSource.getMessage("account.username.unmatched", null, locale));
        }
       
        return ResponseEntity.ok(mdpt);
      }
    } 
    
    return ResponseEntity.notFound().build();
  }

}
