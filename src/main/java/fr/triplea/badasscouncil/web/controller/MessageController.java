package fr.triplea.badasscouncil.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import fr.triplea.badasscouncil.dto.NickNameOptionList;
import fr.triplea.badasscouncil.model.Message;
import fr.triplea.badasscouncil.model.Room;
import fr.triplea.badasscouncil.model.User;

@RestController
@RequestMapping("/chat")
public class MessageController 
{
  
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private MessageRepository messageRepository;

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
      User found = userRepository.findByLoginName(authentication.getName());

      if (found != null)
      {
        return userRepository.getNickNameOptionList(found.getUserId()); 
      }
    }
    
    return new ArrayList<NickNameOptionList>();
  }

  @GetMapping(value = "/new/{room}/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> getNew(@PathVariable(name="room") int r, @PathVariable(name="last") int l, final Authentication authentication)
  { 
    List<MessageShort> mlist = null;

    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if ((found != null) && (l >= 0)) 
      {         
        mlist = messageRepository.findNew(r, found.getUserId(), l);
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
        
    return mlist; 
  }

  @PostMapping(value = "/add/{room}/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> addMessage(@PathVariable(name="room") int r, @PathVariable("last") int l, @RequestBody(required = true) MessageShort message, final Authentication authentication)
  { 
    List<MessageShort> mlist = null;

    if ((authentication != null) && (message != null))
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      Room room = roomRepository.findById(r);
      
      if ((room != null) && (found != null) && (l >= 0)) 
      { 
        if (found.getNickName().equals(message.nickName()))
        {
          String ligne = message.content();
          
          if (ligne == null) { ligne = ""; }
          
          if (!ligne.isBlank())
          {
            Message m = new Message();
            
            m.setMessageId(null);
            m.setRoom(room);
            m.setUser(found);
            m.setContent(ligne);
            
            User dest = userRepository.findById(message.destId());
            
            if (dest != null) { m.setDest(dest); } else { m.setDest(null); }
            
            messageRepository.saveAndFlush(m);
          }
          
          mlist = messageRepository.findNew(r, found.getUserId(), l);
        }
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
    
    return mlist; 
  }
  
}
