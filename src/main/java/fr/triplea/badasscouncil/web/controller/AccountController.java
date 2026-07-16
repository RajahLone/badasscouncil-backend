package fr.triplea.badasscouncil.web.controller;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import fr.triplea.badasscouncil.dao.RoleRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.MessagesTransfer;
import fr.triplea.badasscouncil.dto.PasswordTransfer;
import fr.triplea.badasscouncil.dto.UserTransfer;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.UserStatus;
import fr.triplea.badasscouncil.model.Role;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AccountController 
{

  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LocaleResolver localeResolver;
 
  @Autowired
  private MessageSource messageSource;

  private final DateTimeFormatter dtf_fr = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
  private final DateTimeFormatter dft_en = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
 

  @PostMapping(value = "/subscribe")
  public ResponseEntity<PasswordTransfer> subscribe(@RequestBody(required = true) UserTransfer user, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);
   
    PasswordTransfer pt = new PasswordTransfer();
 
    if (user.getSubscribeMotive().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.motive", null, locale)); return ResponseEntity.ok(pt); }
    if (user.getLoginName().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.login", null, locale)); return ResponseEntity.ok(pt); }
    if (user.getPassword().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.password", null, locale)); return ResponseEntity.ok(pt); }
    if (user.getNickName().isBlank()) { pt.setError(messageSource.getMessage("account.subscribe.missing.nickname", null, locale)); return ResponseEntity.ok(pt); }
 
    Role adminRole = roleRepository.findByLibelle("ROLE_ADMIN");
    Role regulRole = roleRepository.findByLibelle("ROLE_REGUL");
    Role userRole = roleRepository.findByLibelle("ROLE_USER");
    
    User found = new User();
    
    found.setEnabled(true);

    List<Role> roles = Arrays.asList(userRole);
    
    long n = userRepository.count();

    String first = "";

    if (n < 1) 
    { 
      // admin for the first subscriber, without PENDING status.
      
      roles.add(adminRole); 
      roles.add(regulRole); 
      
      found.setStatus(UserStatus.ACTIVE);
      
      first = " " + messageSource.getMessage("account.subscribe.first", null, locale);
    } 
    else 
    { 
      found.setStatus(UserStatus.PENDING); 
    } 
    
    found.setRoles(roles);
    found.setSubscribeMotive(user.getSubscribeMotive());
    found.setLoginName(user.getLoginName().trim());
    
    final String mdp = user.getPassword().trim();

    found.setPasswordHash(passwordEncoder.encode(mdp));

    found.setSessionTimeout(user.getSessionTimeout());
    
    found.setNickName(user.getNickName().trim());
    found.setGroupName(user.getGroupName().trim()); 
    found.setFirstName(user.getFirstName());
    found.setLastName(user.getLastName());
     
    found.setDisplayCoordinates(user.mustDisplayCoordinates());  
    found.setAddress(user.getAddress());
    found.setZipCode(user.getZipCode());
    found.setTown(user.getTown());
    found.setCountry(user.getCountry());
    found.setPhone(user.getPhone());
    found.setEmail(user.getEmail());
    
    n = userRepository.count(user.getLoginName().trim().toUpperCase());
    
    if (n > 0) { pt.setError(messageSource.getMessage("account.subscribe.already.login", null, locale)); return ResponseEntity.ok(pt); }
    
    if (user.getGroupName().isBlank()) 
    {
      n = userRepository.count(user.getNickName().trim().toUpperCase(), "");
      
      if (n > 0) { pt.setError(messageSource.getMessage("account.subscribe.already.nickname", null, locale)); return ResponseEntity.ok(pt); }
    }
    else 
    {
      n = userRepository.count(user.getNickName().trim().toUpperCase(), user.getGroupName().trim().toUpperCase());
      
      if (n > 0) { pt.setError(messageSource.getMessage("account.subscribe.already.nickgroupname", null, locale)); return ResponseEntity.ok(pt); }
    }
    
    userRepository.saveAndFlush(found);

    pt.setError("");
    pt.setSuccess(messageSource.getMessage("account.subscribe.success", null, locale) + first); 
    
    return ResponseEntity.ok(pt);
  }

  
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
        p.setUserId(found.getId());
        p.setStatus("");
        
        p.setLoginName(found.getLoginName());
        p.setSessionTimeout(15);
        
        p.setNickName(found.getNickName());
        p.setGroupName(found.getGroupName());
        p.setFirstName(found.getFirstName());
        p.setLastName(found.getLastName());
        
        p.setAddress(found.getAddress());
        p.setZipCode(found.getZipCode());
        p.setTown(found.getTown());
        p.setCountry(found.getCountry());
        p.setPhone(found.getPhone());
        p.setEmail(found.getEmail());
                 
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
        
        found.setDisplayCoordinates(user.mustDisplayCoordinates());
        found.setAddress(user.getAddress());
        found.setZipCode(user.getZipCode());
        found.setTown(user.getTown());
        found.setCountry(user.getCountry());
        found.setPhone(user.getPhone());
        found.setEmail(user.getEmail());
         
               
        userRepository.saveAndFlush(found);
       
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("user.updated", null, locale));
        
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
          if (passwordEncoder.matches(mdp_old, found.getPasswordHash()))
          {
            found.setPasswordHash(passwordEncoder.encode(mdp_new.trim()));
            
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
