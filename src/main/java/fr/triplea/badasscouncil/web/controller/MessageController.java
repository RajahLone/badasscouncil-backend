package fr.triplea.badasscouncil.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.badasscouncil.dao.MessageRepository;
import fr.triplea.badasscouncil.dao.RoomRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.MessageShort;
import fr.triplea.badasscouncil.dto.MessageShortPass;
import fr.triplea.badasscouncil.dto.NickNameOptionList;
import fr.triplea.badasscouncil.model.Message;
import fr.triplea.badasscouncil.model.Room;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.web.service.UserService;

@RestController
@RequestMapping("/chat")
public class MessageController 
{
  
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);
  
  @Value("${password.salt}")
  private String salt;

  @Autowired
  public PasswordEncoder passwordEncoder;
 
  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoomRepository roomRepository;

  
  @GetMapping(value = "/nickname-list")
  @PreAuthorize("hasRole('USER')")
  public List<NickNameOptionList> getNickNames(final Authentication authentication) 
  { 
    if (authentication != null)
    {
      userService.setLastActivityOn(authentication);

      User found = userRepository.findByLoginName(authentication.getName());

      if (found != null)
      {
        return userRepository.getNickNameOptionList(found.getUserId()); 
      }
    }
    
    return new ArrayList<NickNameOptionList>();
  }

  @PostMapping(value = "/new/{room}/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> getNew(@PathVariable(name="room") int r, @PathVariable(name="last") int l, @RequestBody(required = true) MessageShortPass message, final Authentication authentication)
  { 
    List<MessageShort> mlist = null;

    Room room = roomRepository.findById(r);

    if (room != null) 
    { 
      boolean granted = true;
      
      if (room.hasPassword())
      {
        granted = false;
        
        if (passwordEncoder.matches(salt + message.getPassword(), room.getPasswordHash())) { granted = true; }
      }
      
      if ((authentication != null) && granted)
      {
        userService.setLastActivityOn(authentication);

        User found = userRepository.findByLoginName(authentication.getName());
        
        if ((found != null) && (l >= 0)) 
        {         
          mlist = messageRepository.findNew(r, found.getUserId(), l);
        }
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
        
    return mlist; 
  }

  @PostMapping(value = "/add/{room}/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> addMessage(@PathVariable(name="room") int r, @PathVariable("last") int l, @RequestBody(required = true) MessageShortPass message, final Authentication authentication)
  { 
    // TODO: pagination (500 per 500, backlogging)
    
    List<MessageShort> mlist = null;

    Room room = roomRepository.findById(r);

    if (room != null) 
    { 
      boolean granted = true;
      
      if (room.hasPassword())
      {
        granted = false;
        
        if (passwordEncoder.matches(salt + message.getPassword(), room.getPasswordHash())) { granted = true; }
      }
      
      if ((authentication != null) && (message != null) && granted)
      {
        userService.setLastActivityOn(authentication);

        User found = userRepository.findByLoginName(authentication.getName());
                
        if ((found != null) && (l >= 0)) 
        { 
          if (found.getNickName().equals(message.getNickName()))
          {
            String ligne = message.getContent();
            
            if (ligne == null) { ligne = ""; }
            
            if (!ligne.isBlank())
            {
              Message m = new Message();
              
              m.setMessageId(null);
              m.setRoom(room);
              m.setUser(found);
              m.setContent(ligne);
              
              User dest = userRepository.findById(message.getDestId());
              
              if (dest != null) { m.setDest(dest); } else { m.setDest(null); }
              
              messageRepository.saveAndFlush(m);
            }
            
            mlist = messageRepository.findNew(r, found.getUserId(), l);
          }
        }
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
    
    return mlist; 
  }
  
}
