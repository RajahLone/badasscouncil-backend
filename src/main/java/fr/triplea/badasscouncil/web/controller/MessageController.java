package fr.triplea.badasscouncil.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.triplea.badasscouncil.dao.ImageRepository;
import fr.triplea.badasscouncil.dao.MessageRepository;
import fr.triplea.badasscouncil.dao.RoomRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.MessageShort;
import fr.triplea.badasscouncil.dto.MessageShortPass;
import fr.triplea.badasscouncil.dto.NickNameOptionList;
import fr.triplea.badasscouncil.dto.Pagination;
import fr.triplea.badasscouncil.model.Image;
import fr.triplea.badasscouncil.model.Message;
import fr.triplea.badasscouncil.model.MessageType;
import fr.triplea.badasscouncil.model.Room;
import fr.triplea.badasscouncil.model.RoomState;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.web.service.UserService;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/chat")
public class MessageController 
{
  
  //@SuppressWarnings("unused") 
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
  
  @Autowired
  private ImageRepository imageRepository;

  
  @GetMapping(value = "/nickname-list")
  @PreAuthorize("hasRole('USER')")
  public List<NickNameOptionList> listNickNames(final Authentication authentication) 
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

  @GetMapping(value = "/count/{room}")
  @PreAuthorize("hasRole('USER')")
  public Pagination count(@PathVariable(name="room") int r, final Authentication authentication)
  { 
    int n = 0;
    
    Room room = roomRepository.findById(r);

    if (room != null) 
    { 
      boolean granted = true;
            
      if (room.getState().equals(RoomState.LOCKED))
      {
        granted = false;
        
        if (userService.hasSameId(authentication, room.getUser().getUserId()) || userService.canRegulate(authentication)) { granted = true; }
      }
      
      if ((authentication != null) && granted)
      {
        userService.setLastActivityOn(authentication);

        User found = userRepository.findByLoginName(authentication.getName());
        
        if (found != null) 
        {         
          granted = false;
          
          if (found.hasRoles("ADMIN") || (room.getUser().getUserId().equals(found.getUserId()))) 
          { 
            granted = true; 
          } 
          else 
          {
             switch(room.getListedUsersType())
            {
              case RoomController.LISTED_USERS_TYPE_ALL:
                granted = true;
                break;
              case RoomController.LISTED_USERS_TYPE_ALLOWED:
                List<Integer> a = roomRepository.findAllowedUsers(r);
                if (a != null) { if (a.size() > 0) { if (a.contains(found.getUserId())) { granted = true; } } }
                break;
              case RoomController.LISTED_USERS_TYPE_DISALLOWED:
                granted = true;
                List<Integer> d = roomRepository.findDisallowedUsers(r);
                if (d != null) { if (d.size() > 0) { if (d.contains(found.getUserId())) { granted = false; } } }
                break;
            }
          }

          if (granted) { n = (int)messageRepository.count(r); }
        }
      }
    }
        
    return new Pagination(n, 500, 1, 0); 
  }

  @PostMapping(value = "/new/{room}/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> getNewLines(@PathVariable(name="room") int r, @PathVariable(name="last") int l, @RequestBody(required = true) MessageShortPass message, final Authentication authentication)
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
      
      if (room.getState().equals(RoomState.LOCKED))
      {
        granted = false;
        
        if (userService.hasSameId(authentication, room.getUser().getUserId()) || userService.canRegulate(authentication)) { granted = true; }
      }
      
      if ((authentication != null) && granted)
      {
        userService.setLastActivityOn(authentication);

        User found = userRepository.findByLoginName(authentication.getName());
        
        if ((found != null) && (l >= 0)) 
        {         
          granted = false;
          
          if (found.hasRoles("ADMIN") || (room.getUser().getUserId().equals(found.getUserId()))) 
          { 
            granted = true; 
          } 
          else 
          {
             switch(room.getListedUsersType())
            {
              case RoomController.LISTED_USERS_TYPE_ALL:
                granted = true;
                break;
              case RoomController.LISTED_USERS_TYPE_ALLOWED:
                List<Integer> a = roomRepository.findAllowedUsers(r);
                if (a != null) { if (a.size() > 0) { if (a.contains(found.getUserId())) { granted = true; } } }
                break;
              case RoomController.LISTED_USERS_TYPE_DISALLOWED:
                granted = true;
                List<Integer> d = roomRepository.findDisallowedUsers(r);
                if (d != null) { if (d.size() > 0) { if (d.contains(found.getUserId())) { granted = false; } } }
                break;
            }
          }

          if (granted) { mlist = messageRepository.findNew(r, found.getUserId(), l); }
        }
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
        
    return mlist; 
  }

  @PostMapping(value = "/old/{room}/{first}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> getOldLines(@PathVariable(name="room") int r, @PathVariable(name="first") int f, @RequestBody(required = true) MessageShortPass message, final Authentication authentication)
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
      
      if (room.getState().equals(RoomState.LOCKED))
      {
        granted = false;
        
        if (userService.hasSameId(authentication, room.getUser().getUserId()) || userService.canRegulate(authentication)) { granted = true; }
      }
      
      if ((authentication != null) && granted)
      {
        userService.setLastActivityOn(authentication);

        User found = userRepository.findByLoginName(authentication.getName());
        
        if ((found != null) && (f >= 0)) 
        {         
          granted = false;
          
          if (found.hasRoles("ADMIN") || (room.getUser().getUserId().equals(found.getUserId()))) 
          { 
            granted = true; 
          } 
          else 
          {
             switch(room.getListedUsersType())
            {
              case RoomController.LISTED_USERS_TYPE_ALL:
                granted = true;
                break;
              case RoomController.LISTED_USERS_TYPE_ALLOWED:
                List<Integer> a = roomRepository.findAllowedUsers(r);
                if (a != null) { if (a.size() > 0) { if (a.contains(found.getUserId())) { granted = true; } } }
                break;
              case RoomController.LISTED_USERS_TYPE_DISALLOWED:
                granted = true;
                List<Integer> d = roomRepository.findDisallowedUsers(r);
                if (d != null) { if (d.size() > 0) { if (d.contains(found.getUserId())) { granted = false; } } }
                break;
            }
          }

          if (granted) { mlist = messageRepository.findOld(r, found.getUserId(), f); }
        }
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
        
    return mlist; 
  }

  @PostMapping(value = "/add/txt/{room}/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> addText(@PathVariable(name="room") int r, @PathVariable("last") int l, @RequestBody(required = true) MessageShortPass message, final Authentication authentication)
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
      
      if (room.getState().equals(RoomState.LOCKED))
      {
        granted = false;
        
        if (userService.hasSameId(authentication, room.getUser().getUserId()) || userService.canRegulate(authentication)) { granted = true; }
      }
      
      if ((authentication != null) && (message != null) && granted)
      {
        userService.setLastActivityOn(authentication);

        User found = userRepository.findByLoginName(authentication.getName());
                
        if ((found != null) && (l >= 0)) 
        { 
          granted = false;
          
          if (found.hasRoles("ADMIN") || (room.getUser().getUserId().equals(found.getUserId()))) 
          { 
            granted = true; 
          } 
          else 
          {
             switch(room.getListedUsersType())
            {
              case RoomController.LISTED_USERS_TYPE_ALL:
                granted = true;
                break;
              case RoomController.LISTED_USERS_TYPE_ALLOWED:
                List<Integer> a = roomRepository.findAllowedUsers(r);
                if (a != null) { if (a.size() > 0) { if (a.contains(found.getUserId())) { granted = true; } } }
                break;
              case RoomController.LISTED_USERS_TYPE_DISALLOWED:
                granted = true;
                List<Integer> d = roomRepository.findDisallowedUsers(r);
                if (d != null) { if (d.size() > 0) { if (d.contains(found.getUserId())) { granted = false; } } }
                break;
            }
          }

          if (found.getNickName().equals(message.getNickName()) && granted)
          {
            String ligne = message.getContent();
            
            if (ligne == null) { ligne = ""; }
            
            if (!ligne.isBlank())
            {
              Message m = new Message();
              
              m.setMessageId(null);
              m.setMessageType(MessageType.TEXT);
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
  
  @PostMapping(value = "/add/img/{room}/{last}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> addImages(@PathVariable(name="room") int r, @PathVariable("last") int l, @RequestParam(name="file") MultipartFile[] files, final Authentication authentication, HttpServletRequest request)
  { 
    List<MessageShort> mlist = null;

    Room room = roomRepository.findById(r);

    MessageShortPass message = null;
    
    if ((room != null) && (files != null)) 
    { 
      boolean granted = true;
      
      if (files.length > 0)
      {
        if (files[0].getContentType().contains("application/json")) // first file is json for MessageShortPass, next are image files
        {
          ObjectMapper mapper = new ObjectMapper();
          
          try { message = mapper.readValue(files[0].getBytes(), MessageShortPass.class); } catch (Exception e) { LOG.error(e.toString()); message = null; }
        }
      }
      
      if (room.hasPassword())
      {
        granted = false;
                
        if (message != null) { if (passwordEncoder.matches(salt + message.getPassword(), room.getPasswordHash())) { granted = true; } }
      }
      
      if (room.getState().equals(RoomState.LOCKED))
      {
        granted = false;
        
        if (userService.hasSameId(authentication, room.getUser().getUserId()) || userService.canRegulate(authentication)) { granted = true; }
      }
      
      if ((authentication != null) && (message != null) && (files.length > 1) && granted)
      {
        userService.setLastActivityOn(authentication);

        User found = userRepository.findByLoginName(authentication.getName());
                
        if ((found != null) && (l >= 0)) 
        { 
          granted = false;
          
          if (found.hasRoles("ADMIN") || (room.getUser().getUserId().equals(found.getUserId()))) 
          { 
            granted = true; 
          } 
          else 
          {
             switch(room.getListedUsersType())
            {
              case RoomController.LISTED_USERS_TYPE_ALL:
                granted = true;
                break;
              case RoomController.LISTED_USERS_TYPE_ALLOWED:
                List<Integer> a = roomRepository.findAllowedUsers(r);
                if (a != null) { if (a.size() > 0) { if (a.contains(found.getUserId())) { granted = true; } } }
                break;
              case RoomController.LISTED_USERS_TYPE_DISALLOWED:
                granted = true;
                List<Integer> d = roomRepository.findDisallowedUsers(r);
                if (d != null) { if (d.size() > 0) { if (d.contains(found.getUserId())) { granted = false; } } }
                break;
            }
          }

          if (found.getNickName().equals(message.getNickName()) && granted)
          {
            Message m = new Message();
            
            m.setMessageId(null);
            m.setMessageType(MessageType.IMAGES);
            m.setRoom(room);
            m.setUser(found);
            m.setContent("");
            
            User dest = userRepository.findById(message.getDestId());
            
            if (dest != null) { m.setDest(dest); } else { m.setDest(null); }

            messageRepository.saveAndFlush(m);

            StringBuffer sb = new StringBuffer();
            
            for (int f = 1; f < files.length; f++)
            {
              try 
              {                
                if (files[f].getContentType().startsWith("image/"))
                {
                  Image i = new Image();
                  
                  i.setImageId(null);
                  i.setEnabled(true);
                  i.setIpAddress(new Inet(this.getClientIP(request)));
                  i.setMessage(m);
                  i.setUser(found);
                  if (dest != null) { i.setDest(dest); } else { i.setDest(null); }
                  i.setFileName(files[f].getOriginalFilename());
                  i.generateThumbnail(files[f].getBytes());
                  i.setData(files[f].getBytes());

                  imageRepository.saveAndFlush(i);

                  sb.append("img_" + i.getImageId());
                  
                  if (f < (files.length - 1)) { sb.append("|"); }
                }
              } 
              catch (IOException e) { LOG.error(files[f].getName() + " -> " + e.toString()); }
            }
            
            m.setContent(sb.toString());
            
            messageRepository.saveAndFlush(m);
            
            mlist = messageRepository.findNew(r, found.getUserId(), l);
          }
        }
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
    
    return mlist; 
  }
  
  private final String getClientIP(HttpServletRequest request) 
  {
    final String h = request.getHeader("X-Forwarded-For");
    
    if (h != null) { if (!(h.isBlank())) { if (!(h.contains(request.getRemoteAddr()))) { return h.split(",")[0]; } } } 
    
    return request.getRemoteAddr();
  }

}
