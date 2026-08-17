package fr.triplea.badasscouncil.web.controller;

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
import fr.triplea.badasscouncil.dto.RoomTransfer;
import fr.triplea.badasscouncil.model.Room;
import fr.triplea.badasscouncil.model.RoomPurgeMethod;
import fr.triplea.badasscouncil.model.RoomState;
import fr.triplea.badasscouncil.model.User;
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
  public List<RoomTransfer> getList(final Authentication authentication) 
  { 
    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());

      if (found != null)
      {
        return roomRepository.list(); 
      }
    }
    
    return new ArrayList<RoomTransfer>();
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
      Room found = new Room();
      
      found.setName(room.getName());
      found.setState(RoomState.ACTIVE);
      
      found.setUser(user);
      
      if (room.getPassword() != null) { if (room.hasPassword()) { found.setPasswordHash(passwordEncoder.encode(salt + room.getPassword().trim())); } } 
      
      found.setTopic(room.getTopic());

      if (room.getPurgeMethod().equals("MESSAGES_LIMITED")) { found.setPurgeMethod(RoomPurgeMethod.MESSAGES_LIMITED); }
      else if (room.getPurgeMethod().equals("TIME_LIMITED")) { found.setPurgeMethod(RoomPurgeMethod.TIME_LIMITED); }
      else if (room.getPurgeMethod().equals("WHEN_DEPOPULATED")) { found.setPurgeMethod(RoomPurgeMethod.WHEN_DEPOPULATED); }
      else { found.setPurgeMethod(RoomPurgeMethod.NEVER); }
      
      found.setMessagesLimit(room.getMessagesLimit());
      found.setTimeDuration(room.getTimeDuration());

                 
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
      if ((found.getRoomId().intValue() == roomId) && (found.getUser().equals(user) || user.hasRoles("REGUL", "ADMIN")))
      {
        found.setName(room.getName());

        if (room.getState().equals("LOCKED")) { found.setState(RoomState.LOCKED); }
        else if (room.getState().equals("TRASHED")) { found.setState(RoomState.TRASHED); }
        else { found.setState(RoomState.ACTIVE); }

        found.setPasswordHash(passwordEncoder.encode(salt + room.getPassword().trim())); 

        found.setTopic(room.getTopic());

        if (room.getPurgeMethod().equals("MESSAGES_LIMITED")) { found.setPurgeMethod(RoomPurgeMethod.MESSAGES_LIMITED); }
        else if (room.getPurgeMethod().equals("TIME_LIMITED")) { found.setPurgeMethod(RoomPurgeMethod.TIME_LIMITED); }
        else if (room.getPurgeMethod().equals("WHEN_DEPOPULATED")) { found.setPurgeMethod(RoomPurgeMethod.WHEN_DEPOPULATED); }
        else { found.setPurgeMethod(RoomPurgeMethod.NEVER); }
        
        found.setMessagesLimit(room.getMessagesLimit());
        found.setTimeDuration(room.getTimeDuration());

        
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
      if (found.getUser().equals(user) || user.hasRoles("REGUL", "ADMIN"))
      {
        found.setEnabled(false); 
        
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

}
