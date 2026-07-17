package fr.triplea.badasscouncil.web.controller;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dao.RoleRepository;
import fr.triplea.badasscouncil.dao.VariableRepository;
import fr.triplea.badasscouncil.dto.MessagesTransfer;
import fr.triplea.badasscouncil.dto.Pagination;
import fr.triplea.badasscouncil.dto.UserList;
import fr.triplea.badasscouncil.dto.UserOptionList;
import fr.triplea.badasscouncil.dto.UserTransfer;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.UserStatus;
import fr.triplea.badasscouncil.model.Role;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController 
{

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private VariableRepository variableRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;
  

  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('ORGA')")
  public List<UserList> getList(
      @RequestParam("name") String nameFilter, 
      @RequestParam("status") int statusFilter, 
      @RequestParam("sort") int sortType, 
      @RequestParam(name="page", defaultValue="0") int pageNumber, 
      @RequestParam(name="size", defaultValue="0") Integer pageSize
      ) 
  { 
    if (nameFilter != null) { if (nameFilter.isBlank()) { nameFilter = null; } else { nameFilter = nameFilter.trim().toUpperCase(); } }
    
    if (pageSize == 0) { try { pageSize = Integer.parseInt(variableRepository.findByFamilyAndCode("Navigation", "LISTING_MAX_USERS")); } catch(Exception e) { pageSize = null; } }
    
    if ((pageSize == null) || (pageSize == 0)) { pageNumber = 0; }
    
    int offset = 0;
    
    if (pageNumber > 0) { offset = ((pageNumber * pageSize) + 1); }
    
    if (sortType == 1) 
    { 
      return userRepository.getPageOrderedByDateInscription(nameFilter, statusFilter, offset, pageSize); 
    }
    else 
    {
      return userRepository.getPageOrderedByNom(nameFilter, statusFilter, offset, pageSize);
    }
  }

  @GetMapping(value = "/pagination")
  @PreAuthorize("hasRole('ORGA')")
  public Pagination getCount(
      @RequestParam("name") String nameFilter, 
      @RequestParam("status") int statusFilter, 
      @RequestParam(name="page", defaultValue="0") int pageNumber
      ) 
  { 
    if (nameFilter != null) { if (nameFilter.isBlank()) { nameFilter = null; } else { nameFilter = nameFilter.trim().toUpperCase(); } }
    
    int nombreParPage = 100;
    
    try { nombreParPage = Integer.parseInt(variableRepository.findByFamilyAndCode("Navigation", "LISTING_MAX_USERS")); } catch(Exception e) { nombreParPage = 100; }

    int nombreElements = userRepository.count(nameFilter, statusFilter);
     
    int nombrePages = 0;
    
    int decompte = nombreElements; while (decompte > 0) { nombrePages++; decompte -= nombreParPage; }
    
    pageNumber = Math.max(0, Math.min(pageNumber, nombrePages - 1));
    
    return new Pagination(nombreElements, nombreParPage, nombrePages, pageNumber);
  }

  
  @GetMapping(value = "/option-list")
  @PreAuthorize("hasRole('ORGA')")
  public List<UserOptionList> getOptionList(final Authentication authentication) 
  { 
    return userRepository.getUserOptionList(); 
  }


  private final DateTimeFormatter dtf_fr = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
  private final DateTimeFormatter dft_en = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
 
  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<UserTransfer> getForm(@PathVariable("id") int userId, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    DateTimeFormatter dtf = this.dtf_fr; if (locale == Locale.ENGLISH) { dtf = this.dft_en; }
    
    User found = userRepository.findById(userId);   
    
    if (found != null)
    {
      UserTransfer p = new UserTransfer();
      
      p.setCreatedOn(found.hasCreatedOn() ? dtf.format(found.getCreatedOn()) : "");
      p.setUpdatedOn(found.hasUpdatedOn() ? dtf.format(found.getUpdatedOn()) : ""); 
      p.setUserId(found.getId());
 
      if (found.getStatus().equals(UserStatus.PENDING)) { p.setStatus("PENDING"); }
      else if (found.getStatus().equals(UserStatus.LOCKED)) { p.setStatus("LOCKED"); }
      else if (found.getStatus().equals(UserStatus.BANNED)) { p.setStatus("BANNED"); }
      else if (found.getStatus().equals(UserStatus.SLEEPING)) { p.setStatus("SLEEPING"); }
      else { p.setStatus("ACTIVE"); }

      p.setSubscribeMotive(found.getSubscribeMotive());
      
      p.setLoginName(found.getLoginName());
      p.setSessionTimeout(found.getSessionTimeout());

      p.setNickName(found.getNickName());
      p.setGroupName(found.getGroupName()); 
      p.setFirstName(found.getFirstName());
      p.setLastName(found.getLastName());

      p.setDisplayCoordinates(found.mustDisplayCoordinates());
      p.setAddress(found.getAddress());
      p.setZipCode(found.getZipCode());
      p.setTown(found.getTown());
      p.setCountry(found.getCountry());
      p.setPhone(found.getPhone());
      p.setEmail(found.getEmail());
      
      List<Role> roles = found.getRoles();       
      
      if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("ADMIN")) { p.setRole("ADMIN"); } } }
      if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("REGUL")) { p.setRole("REGUL"); } } }
      if (!(p.hasRole())) { p.setRole("USER"); } 

      return ResponseEntity.ok(p); 
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Object> create(@RequestBody(required = true) UserTransfer user, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    User found = userRepository.findById(0);
    
    if (found == null) 
    {
      if (!(user.getLoginName().isBlank()))
      {
        if (!(user.getNickName().isBlank()))
        {
          found = new User();
          
          found.setRoles(found.getRoles());
          found.setEnabled(true);
          
          if (user.getStatus().equals("PENDING")) { found.setStatus(UserStatus.PENDING); }
          else if (user.getStatus().equals("LOCKED")) { found.setStatus(UserStatus.LOCKED); }
          else if (user.getStatus().equals("BANNED")) { found.setStatus(UserStatus.BANNED); }
          else if (user.getStatus().equals("SLEEPING")) { found.setStatus(UserStatus.SLEEPING); }
          else { found.setStatus(UserStatus.ACTIVE); }

          found.setSubscribeMotive(user.getSubscribeMotive());
          
          final String mdp = user.getPassword();
          if (mdp != null) { if (!(mdp.isBlank())) { found.setPasswordHash(passwordEncoder.encode(mdp.trim())); } } 

          found.setSessionTimeout(user.getSessionTimeout());
          
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
         
          Role userRole = roleRepository.findByLabel("ROLE_USER");

          if (authentication != null)
          {
            Role adminRole = roleRepository.findByLabel("ROLE_ADMIN");
            Role regulRole = roleRepository.findByLabel("ROLE_REGUL");
           
            if ((adminRole != null) && (regulRole != null) && (userRole != null))
            {
              List<String> granter_roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());
              
              if (user.getRole().equals("ADMIN") && granter_roles.contains("ROLE_ADMIN"))
              {
                found.setRoles(Arrays.asList(adminRole, regulRole, userRole));
              }
              else if (user.getRole().equals("REGUL") && granter_roles.contains("ROLE_REGUL"))
              {
                found.setRoles(Arrays.asList(regulRole, userRole));
              }
              else
              {
                found.setRoles(Arrays.asList(userRole));
              }
            }
          }
          else
          {
            if (userRole != null) { found.setRoles(Arrays.asList(userRole)); }
          }
                    
          userRepository.saveAndFlush(found);
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("user.created", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
       
    return ResponseEntity.notFound().build();
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Object> update(@PathVariable("id") int userId, @RequestBody(required = true) UserTransfer user, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    User found = userRepository.findById(userId);
    
    if (found != null)
    {
      found.setRoles(found.getRoles());
      found.setEnabled(true);
      
      if (user.getStatus().equals("PENDING")) { found.setStatus(UserStatus.PENDING); }
      else if (user.getStatus().equals("LOCKED")) { found.setStatus(UserStatus.LOCKED); }
      else if (user.getStatus().equals("BANNED")) { found.setStatus(UserStatus.BANNED); }
      else if (user.getStatus().equals("SLEEPING")) { found.setStatus(UserStatus.SLEEPING); }
      else { found.setStatus(UserStatus.ACTIVE); }

      found.setSubscribeMotive(user.getSubscribeMotive());
      
      final String mdp = user.getPassword();
      if (mdp != null) { if (!(mdp.isBlank())) { found.setPasswordHash(passwordEncoder.encode(mdp.trim())); } } 

      found.setSessionTimeout(user.getSessionTimeout());
      
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
      
      Role userRole = roleRepository.findByLabel("ROLE_USER");

      if (authentication != null)
      {
        Role adminRole = roleRepository.findByLabel("ROLE_ADMIN");
        Role regulRole = roleRepository.findByLabel("ROLE_REGUL");
       
        if ((adminRole != null) && (regulRole != null) && (userRole != null))
        {
          List<String> granter_roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());
          
          if (user.getRole().equals("ADMIN") && granter_roles.contains("ROLE_ADMIN"))
          {
            found.setRoles(Arrays.asList(adminRole, regulRole, userRole));
          }
          else if (user.getRole().equals("REGUL") && granter_roles.contains("ROLE_REGUL"))
          {
            found.setRoles(Arrays.asList(regulRole, userRole));
          }
          else
          {
            found.setRoles(Arrays.asList(userRole));
          }
        }
      }
      else
      {
        if (userRole != null) { found.setRoles(Arrays.asList(userRole)); }
      }

      userRepository.saveAndFlush(found);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("user.updated", null, locale));
    
      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Map<String, Boolean>> disableUser(@PathVariable("id") int userId, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    User found = userRepository.findById(userId);
    
    if (found != null)
    {
      found.setEnabled(false); 
      found.setLoginName(found.getLoginName() + "_" + UUID.randomUUID().toString());
      
      userRepository.saveAndFlush(found);

      Map<String, Boolean> response = new HashMap<>();
      response.put("deleted", Boolean.TRUE);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setAlerte(messageSource.getMessage("user.deleted", null, locale));

      return ResponseEntity.ok(response); 
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  

}
