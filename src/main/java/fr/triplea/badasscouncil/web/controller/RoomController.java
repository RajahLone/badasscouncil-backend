package fr.triplea.badasscouncil.web.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.badasscouncil.dao.RoomRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.HomeInformationTransfer;
import fr.triplea.badasscouncil.dto.NickNameOptionList;
import fr.triplea.badasscouncil.dto.RoomRecord;
import fr.triplea.badasscouncil.dto.RoomTransfer;
import fr.triplea.badasscouncil.model.Room;
import fr.triplea.badasscouncil.model.RoomPurgeType;
import fr.triplea.badasscouncil.model.RoomState;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/room")
public class RoomController 
{
  
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;
  
  @Value("${password.salt}")
  private String salt;
  
  @Autowired
  private PasswordEncoder passwordEncoder;


 
  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<RoomRecord> getList(final Authentication authentication) 
  { 
    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());

      if (found != null)
      {
        return roomRepository.listRooms(); 
      }
    }
    
    return new ArrayList<RoomRecord>();
  }

  private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
  
  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<RoomTransfer> getForm(@PathVariable("id") int roomId, final Authentication authentication) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }
    
    Room found = roomRepository.findById(roomId);

    User user = userRepository.findByLoginName(authentication.getName());

    if ((found != null) && (user != null))
    {
      if ((found.getRoomId().intValue() == roomId) && (found.getUser().getUserId().equals(user.getUserId()) || user.hasRoles("REGUL", "ADMIN")))
      {
        userService.setLastActivityOn(authentication);

        RoomTransfer r = new RoomTransfer();
        
        r.setCreatedOn(found.hasCreatedOn() ? dtf.format(found.getCreatedOn()) : "");
        r.setUpdatedOn(found.hasUpdatedOn() ? dtf.format(found.getUpdatedOn()) : "");
        r.setRoomId(found.getRoomId().intValue());   
        r.setName(found.getName());
        r.setState(found.getState().getState());
        r.setOwnerId(found.getUser().getUserId().intValue());
        r.setPassword("");
        r.setTopic(found.getTopic());
        r.setNotes(found.getNotes());
        r.setPurgeType(found.getPurgeType().getPurgeType());
        r.setMessagesLimit(found.getMessagesLimit().intValue());
        r.setTimeDuration(found.getTimeDuration().intValue());    
        
        return ResponseEntity.ok(r);
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> create(@RequestBody(required = true) RoomTransfer room, final Authentication authentication, HttpServletRequest request) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }

    Locale locale = localeResolver.resolveLocale(request);
    
    User user = userRepository.findByLoginName(authentication.getName());
    
    if (!(room.getName().isBlank()) && (user != null))
    {
      userService.setLastActivityOn(authentication);

      Room found = new Room();
      
      found.setName(room.getName());
      found.setState(RoomState.ACTIVE);
      found.setEnabled(true);
      
      found.setUser(user);
      
      if (room.hasPassword()) { found.setPasswordHash(passwordEncoder.encode(salt + room.getPassword().trim())); } else { found.setPasswordHash(""); } 
      
      found.setTopic(room.getTopic());
      found.setNotes(room.getNotes());

      if (room.getPurgeType().equals("MESSAGES_LIMITED")) { found.setPurgeType(RoomPurgeType.MESSAGES_LIMITED); }
      else if (room.getPurgeType().equals("TIME_LIMITED")) { found.setPurgeType(RoomPurgeType.TIME_LIMITED); }
      else { found.setPurgeType(RoomPurgeType.NEVER); }
      
      found.setMessagesLimit(room.getMessagesLimit());
      found.setTimeDuration(room.getTimeDuration());
      
      found.setListedUsersType(room.getListedUsersType());

                 
      roomRepository.saveAndFlush(found);
      
      HomeInformationTransfer mt = new HomeInformationTransfer();
      mt.setInfo(messageSource.getMessage("room.created", null, locale));

      return ResponseEntity.ok(mt);
    }
       
    return ResponseEntity.notFound().build();
  }


  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> update(@PathVariable("id") int roomId, @RequestBody(required = true) RoomTransfer room, final Authentication authentication, HttpServletRequest request) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }

    Locale locale = localeResolver.resolveLocale(request);

    Room found = roomRepository.findById(roomId);

    User user = userRepository.findByLoginName(authentication.getName());
    
    if ((found != null) && (user != null))
    {
      if ((found.getRoomId().intValue() == roomId) && (found.getUser().getUserId().equals(user.getUserId()) || user.hasRoles("REGUL", "ADMIN")))
      {
        userService.setLastActivityOn(authentication);

        found.setName(room.getName());

        if (room.getState().equals("LOCKED")) { found.setState(RoomState.LOCKED); }
        else if (room.getState().equals("TRASHED")) { found.setState(RoomState.TRASHED); }
        else { found.setState(RoomState.ACTIVE); }

        if (room.hasPassword())
        { 
          if (room.getPassword().equals("BLANK")) { found.setPasswordHash(""); }
          else { found.setPasswordHash(passwordEncoder.encode(salt + room.getPassword().trim()));  }
        } 
        
        found.setTopic(room.getTopic());
        found.setNotes(room.getNotes());

        if (room.getPurgeType().equals("MESSAGES_LIMITED")) { found.setPurgeType(RoomPurgeType.MESSAGES_LIMITED); }
        else if (room.getPurgeType().equals("TIME_LIMITED")) { found.setPurgeType(RoomPurgeType.TIME_LIMITED); }
        else { found.setPurgeType(RoomPurgeType.NEVER); }
        
        found.setMessagesLimit(room.getMessagesLimit());
        found.setTimeDuration(room.getTimeDuration());

        found.setListedUsersType(room.getListedUsersType());

        
        roomRepository.saveAndFlush(found);
        
        HomeInformationTransfer mt = new HomeInformationTransfer();
        mt.setInfo(messageSource.getMessage("room.updated", null, locale));
      
        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  
  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Map<String, Boolean>> disable(@PathVariable("id") int roomId, final Authentication authentication, HttpServletRequest request) 
  { 
    if (authentication == null) { return ResponseEntity.notFound().build(); }

    Locale locale = localeResolver.resolveLocale(request);

    Room found = roomRepository.findById(roomId);

    User user = userRepository.findByLoginName(authentication.getName());

    if ((found != null) && (user != null))
    {
      if (found.getUser().getUserId().equals(user.getUserId()) || user.hasRoles("REGUL", "ADMIN"))
      {
        userService.setLastActivityOn(authentication);

        found.setEnabled(false); 
        found.setState(RoomState.TRASHED);
        
        roomRepository.saveAndFlush(found);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        
        HomeInformationTransfer mt = new HomeInformationTransfer();
        mt.setAlert(messageSource.getMessage("room.deleted", null, locale));

        return ResponseEntity.ok(response); 
      }
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  
  @GetMapping(value = "/users-list")
  @PreAuthorize("hasRole('USER')")
  public List<NickNameOptionList> getUsers(final Authentication authentication) 
  { 
    if (authentication != null)
    {
      List<NickNameOptionList> l = userRepository.getAll();

      if (l != null) { return l; }
    }
    
    return new ArrayList<NickNameOptionList>();
  }

  @GetMapping(value = "/allowed-list/{id}")
  @PreAuthorize("hasRole('USER')")
  public List<NickNameOptionList> getAllowed(@PathVariable("id") int roomId, final Authentication authentication) 
  { 
    if (authentication != null)
    {
      List<NickNameOptionList> l = userRepository.getAllowedList(roomId);

      if (l != null) { return l; }
    }
    
    return new ArrayList<NickNameOptionList>();
  }
  
  @GetMapping(value = "/disallowed-list/{id}")
  @PreAuthorize("hasRole('USER')")
  public List<NickNameOptionList> getDisallowed(@PathVariable("id") int roomId, final Authentication authentication) 
  { 
    if (authentication != null)
    {
      List<NickNameOptionList> l = userRepository.getDisallowedList(roomId);

      if (l != null) { return l; }
    }
    
    return new ArrayList<NickNameOptionList>();
  }
  
  
}
