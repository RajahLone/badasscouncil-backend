package fr.triplea.badasscouncil.web.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
import fr.triplea.badasscouncil.dao.RefreshTokenRepository;
import fr.triplea.badasscouncil.dao.RoleRepository;
import fr.triplea.badasscouncil.dto.HomeInformationTransfer;
import fr.triplea.badasscouncil.dto.Pagination;
import fr.triplea.badasscouncil.dto.UserList;
import fr.triplea.badasscouncil.dto.UserOptionList;
import fr.triplea.badasscouncil.dto.UserTransfer;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.UserStatus;
import fr.triplea.badasscouncil.web.service.PreferenceService;
import fr.triplea.badasscouncil.web.service.UserService;
import fr.triplea.badasscouncil.web.service.VariableService;
import fr.triplea.badasscouncil.model.Preference;
import fr.triplea.badasscouncil.model.Role;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController 
{

  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private PreferenceService preferenceService;

  @Autowired
  private VariableService variableService;
  
  @Value("${password.salt}")
  private String salt;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;
  

  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<UserList> getList(
      @RequestParam("name") String nameFilter, 
      @RequestParam("status") String statusFilter, 
      @RequestParam(name="sort", defaultValue="0") Integer sortType, 
      @RequestParam(name="page", defaultValue="0") int current, 
      @RequestParam(name="size", defaultValue="0") Integer length, 
      final Authentication authentication
      ) 
  { 
    if (authentication == null) { return new ArrayList<UserList>(); }

    userService.setLastActivityOn(authentication);

    if (nameFilter != null) { if (nameFilter.isBlank()) { nameFilter = null; } else { nameFilter = nameFilter.trim().toUpperCase(); } }
    if (statusFilter != null) { if (statusFilter.isBlank()) { statusFilter = null; } else { statusFilter = statusFilter.trim().toUpperCase(); } }
    
    StringBuffer sb = new StringBuffer();
    
    if (nameFilter != null) { sb.append("&name="); sb.append(nameFilter); }
    if (statusFilter != null) { sb.append("&status="); sb.append(statusFilter); }
    if (sortType.intValue() > -1) { sb.append("&sort="); sb.append(sortType.toString()); }
    
    preferenceService.set(Preference.USERS_FILTERS, sb.toString(), authentication);
    
    length = preferenceService.getInteger(Preference.USERS_PAGE_SIZE, authentication);
    
    if (length == null) 
    {
      length = 50;
      current = 0;
      
      preferenceService.set(Preference.USERS_PAGE_SIZE, "50", authentication);
    }
    
    int offset = 0;
    
    if (current > 0) { offset = ((current * length.intValue()) + 1); }
    
    List<UserList> list = null;
   
    if (sortType != null)
    {
      switch (sortType.intValue()) 
      { 
        case 1: 
          list = userRepository.getPageOrderedBySubscriptionDate(nameFilter, statusFilter, offset, length); 
          break;
        case 3: 
          list = userRepository.getPageOrderedBySubscriptionDateInverted(nameFilter, statusFilter, offset, length); 
          break;
        case 2: 
          list = userRepository.getPageOrderedByNameInverted(nameFilter, statusFilter, offset, length); 
          break;
        default: 
          list = userRepository.getPageOrderedByName(nameFilter, statusFilter, offset, length); 
          break;
      }
    }
    
    return list;
  }

  @GetMapping(value = "/pagination")
  @PreAuthorize("hasRole('USER')")
  public Pagination getCount(
      @RequestParam("name") String nameFilter, 
      @RequestParam("status") String statusFilter, 
      @RequestParam(name="page", defaultValue="0") int current, 
      final Authentication authentication
      ) 
  { 
    if (nameFilter != null) { if (nameFilter.isBlank()) { nameFilter = null; } else { nameFilter = nameFilter.trim().toUpperCase(); } }
    if (statusFilter != null) { if (statusFilter.isBlank()) { statusFilter = null; } else { statusFilter = statusFilter.trim().toUpperCase(); } }
    
    Integer size = preferenceService.getInteger(Preference.USERS_PAGE_SIZE, authentication);
    
    if (size == null) 
    {
      size = 50;
      current = 0;
      
      preferenceService.set(Preference.USERS_PAGE_SIZE, "50", authentication);
    }
    
    int items = userRepository.countForNameStatus(nameFilter, statusFilter);
     
    int pages = 0;
    
    int count = items; while (count > 0) { pages++; count -= size.intValue(); }
    
    current = Math.max(0, Math.min(current, pages - 1));
    
    return new Pagination(items, size.intValue(), pages, current);
  }

  
  @GetMapping(value = "/option-list")
  @PreAuthorize("hasRole('USER')")
  public List<UserOptionList> getOptionList(final Authentication authentication) 
  { 
    return userRepository.getUserOptionList(); 
  }


  private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
 
  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserTransfer> getForm(@PathVariable("id") int userId, HttpServletRequest request, final Authentication authentication) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }
    
    User found = userRepository.findById(userId);   
    
    if (found != null)
    {
      userService.setLastActivityOn(authentication);

      Role adminRole = roleRepository.findByLabel("ROLE_ADMIN"); 
      Role regulRole = roleRepository.findByLabel("ROLE_REGUL"); 
      Role userRole = roleRepository.findByLabel("ROLE_USER");

      List<Role> l_roles = null;
      
      try { l_roles = userRepository.findByLoginName(authentication.getName()).getRoles(); } catch (Exception e) { l_roles = new ArrayList<Role>(); }
      
      
      UserTransfer u = new UserTransfer();
      
      u.setCreatedOn(found.hasCreatedOn() ? dtf.format(found.getCreatedOn()) : "");
      u.setUpdatedOn(found.hasUpdatedOn() ? dtf.format(found.getUpdatedOn()) : ""); 
      
      if (u.getUpdatedOn().equals(u.getCreatedOn())) { u.setUpdatedOn(""); }
      
      u.setLastActivityOn(found.hasLastActivityOn() ? dtf.format(found.getLastActivityOn()) : "");
 
      if (found.getStatus().equals(UserStatus.PENDING)) { u.setStatus("PENDING"); }
      else if (found.getStatus().equals(UserStatus.LOCKED)) { u.setStatus("LOCKED"); }
      else if (found.getStatus().equals(UserStatus.BANNED)) { u.setStatus("BANNED"); }
      else if (found.getStatus().equals(UserStatus.SLEEPING)) { u.setStatus("SLEEPING"); }
      else { u.setStatus("ACTIVE"); }

      u.setPassword("");

      u.setNickName(found.getNickName());
      u.setGroupName(found.getGroupName()); 
      u.setFirstName(found.getFirstName());
      u.setLastName(found.getLastName());

      u.setDisplayContactDetails(found.mustDisplayContactDetails());

      if (l_roles.contains(adminRole))
      {
        u.setUserId(found.getUserId());
        u.setSubscribeMotive(found.getSubscribeMotive());
        
        u.setLoginName(found.getLoginName());
        u.setSessionTimeout(found.getSessionTimeout());

        u.setAddress(found.getAddress());
        u.setZipCode(found.getZipCode());
        u.setTown(found.getTown());
        u.setCountry(found.getCountry());
        u.setPhone(found.getPhone());
        u.setEmail(found.getEmail());
      }
      else if (l_roles.contains(regulRole))
      {
        u.setUserId(found.getUserId());
        u.setSubscribeMotive(found.getSubscribeMotive());
              
        u.setLoginName("");
        u.setSessionTimeout(0);

        u.setAddress(found.getAddress());
        u.setZipCode(found.getZipCode());
        u.setTown(found.getTown());
        u.setCountry(found.getCountry());
        u.setPhone(found.getPhone());
        u.setEmail(found.getEmail());
      }
      else if (l_roles.contains(userRole))
      {
        u.setUserId(0);
        u.setSubscribeMotive("");

        u.setLoginName("");
        u.setSessionTimeout(0);

        if (found.mustDisplayContactDetails())
        {
          u.setAddress(found.getAddress());
          u.setZipCode(found.getZipCode());
          u.setTown(found.getTown());
          u.setCountry(found.getCountry());
          u.setPhone(found.getPhone());
          u.setEmail(found.getEmail());
        }
        else 
        {
          u.setAddress("");
          u.setZipCode("");
          u.setTown("");
          u.setCountry("");
          u.setPhone("");
          u.setEmail("");
        }
      }
      
      u.setStorageLimit(found.getStorageLimit());
       
      List<Role> u_roles = found.getRoles();       
      
      if (!(u.hasRole())) { for (Role role : u_roles) { if (role.isRole("ADMIN")) { u.setRole("ADMIN"); } } }
      if (!(u.hasRole())) { for (Role role : u_roles) { if (role.isRole("REGUL")) { u.setRole("REGUL"); } } }
      if (!(u.hasRole())) { u.setRole("USER"); } 

      return ResponseEntity.ok(u); 
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('REGUL')")
  public ResponseEntity<Object> create(@RequestBody(required = true) UserTransfer user, final Authentication authentication, HttpServletRequest request) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }

    Locale locale = localeResolver.resolveLocale(request);

    long cur = userRepository.count();
    
    long max = variableService.getLong("Quota", "MEMBERS_COUNT", 42);

    User found = null; 

    if (max > cur) { found = userRepository.findById(0); }
    
    if (found == null) 
    {
      if (!(user.getLoginName().isBlank()))
      {
        if (!(user.getNickName().isBlank()))
        {
          userService.setLastActivityOn(authentication);

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
          if (mdp != null) 
          { 
            if (!(mdp.isBlank())) 
            { 
              found.setPasswordHash(passwordEncoder.encode(salt + mdp.trim())); 
              found.setPasswordExpired(true);
            } 
          } 

          found.setSessionTimeout(user.getSessionTimeout());
          
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
          
          found.setStorageLimit(user.getStorageLimit());
         
          Role userRole = roleRepository.findByLabel("ROLE_USER");
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
                    
          userRepository.saveAndFlush(found);
          
          HomeInformationTransfer mt = new HomeInformationTransfer();
          mt.setInfo(messageSource.getMessage("user.created", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
       
    return ResponseEntity.notFound().build();
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('REGUL')")
  public ResponseEntity<Object> update(@PathVariable("id") int userId, @RequestBody(required = true) UserTransfer user, final Authentication authentication, HttpServletRequest request) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }

    Locale locale = localeResolver.resolveLocale(request);

    User found = userRepository.findById(userId);
    
    if (found != null)
    {
      userService.setLastActivityOn(authentication);

      found.setRoles(found.getRoles());

      found.setUpdatedOn(LocalDateTime.now());
      found.setEnabled(true);
      
      if (user.getStatus().equals("PENDING")) { found.setStatus(UserStatus.PENDING); }
      else if (user.getStatus().equals("LOCKED")) { found.setStatus(UserStatus.LOCKED); }
      else if (user.getStatus().equals("BANNED")) { found.setStatus(UserStatus.BANNED); }
      else if (user.getStatus().equals("SLEEPING")) { found.setStatus(UserStatus.SLEEPING); }
      else { found.setStatus(UserStatus.ACTIVE); }

      found.setSubscribeMotive(user.getSubscribeMotive());
      
      final String mdp = user.getPassword();
      if (mdp != null) 
      { 
        if (!(mdp.isBlank())) 
        { 
          found.setPasswordHash(passwordEncoder.encode(salt + mdp.trim())); 
        } 
      } 

      found.setSessionTimeout(user.getSessionTimeout());
      
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
      
      found.setStorageLimit(user.getStorageLimit());
      
      Role userRole = roleRepository.findByLabel("ROLE_USER");
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

      userRepository.saveAndFlush(found);
      
      HomeInformationTransfer mt = new HomeInformationTransfer();
      mt.setInfo(messageSource.getMessage("user.updated", null, locale));
    
      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('REGUL')")
  public ResponseEntity<Map<String, Boolean>> disable(@PathVariable("id") int userId, final Authentication authentication, HttpServletRequest request) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }

    Locale locale = localeResolver.resolveLocale(request);

    User found = userRepository.findById(userId);
    
    if (found != null)
    {
      userService.setLastActivityOn(authentication);

      refreshTokenRepository.deleteByUserId(found.getUserId());
      
      found.setUpdatedOn(LocalDateTime.now());
      found.setEnabled(false); 

      found.setLoginName(found.getLoginName() + "_" + UUID.randomUUID().toString());
      found.setNickName(found.getNickName() + "_" + UUID.randomUUID().toString());
 
      found.setFirstName("");
      found.setLastName("");
       
      found.setAddress("");
      found.setZipCode("");
      found.setTown("");
      found.setCountry("");
      found.setPhone("");
      found.setEmail("");

      userRepository.saveAndFlush(found);

      Map<String, Boolean> response = new HashMap<>();
      response.put("deleted", Boolean.TRUE);
      
      HomeInformationTransfer mt = new HomeInformationTransfer();
      mt.setAlert(messageSource.getMessage("user.deleted", null, locale));

      return ResponseEntity.ok(response); 
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  

  @PutMapping(value = "/activate")
  @PreAuthorize("hasRole('REGUL')")
  @Transactional
  public ResponseEntity<Object> activate(@RequestBody List<Integer> usersIds, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    if (usersIds != null)
    {
      if (usersIds.size() > 0)
      {
        userService.setLastActivityOn(authentication);

        userRepository.activate(usersIds);
        userRepository.flush();
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("changed", Boolean.TRUE);

        HomeInformationTransfer mt = new HomeInformationTransfer();
        mt.setAlert(messageSource.getMessage("users.activated", null, locale));

        return ResponseEntity.ok(response); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

}
