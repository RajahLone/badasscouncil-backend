package fr.triplea.badasscouncil.web.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import fr.triplea.badasscouncil.dao.AttachmentRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.model.Attachment;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.UserStatus;

@Service
public class UserService 
{

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private AttachmentRepository attachmentRepository;

  /** if user can upload attachment or not */
  public final synchronized boolean canUpload(final Authentication authentication)
  {
    boolean ret = false;
    
    User u = userRepository.findByLoginName(authentication.getName());
    
    if (u != null)
    {
      List<String> roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

      if (roles.contains("ROLE_ADMIN")) { ret = true; }
      else if (u.getStorageLimit() == 0) { ret = true; } 
      else if (u.getStorageLimit() > 0)
      {
        try 
        {
          long maxSize = u.getStorageLimit() * 1073741824l;
          long curSize = 0l;
          
          Attachment a = null;
          
          List<Integer> ids = attachmentRepository.findByOwner(u.getUserId());
          
          if (ids != null)
          {
            if (ids.size() > 0)
            {
              for (int i = 0; i < ids.size(); i++)
              {
                a = attachmentRepository.findById(ids.get(i).intValue());
                
                if (a != null)
                {
                  File f = new File("../uploads/" + a.getLocalName());
                  
                  if (f.exists() && f.isFile()) { curSize += f.length(); }
                }
              }
            }
          }
          
          if (curSize < maxSize) { ret = true; }
        }
        catch (Exception e) { LOG.error(e.toString()); }
      }
    }
    
    return ret;
  }
 
  /** returns 0 if ROLE_ADMIN, else if USER id */
  public final synchronized int getUserId(final Authentication authentication)
  {
    int userId = -1; // -1 = not found
    
    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if (found != null)
      {
        userId = found.getUserId();
        
        List<String> roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) { userId = 0; }
      }
    }
    
    return userId;
  }

  /**  */
  public final synchronized boolean hasSameId(final Authentication authentication, final Integer id)
  {
    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if (found != null) { return found.getUserId().equals(id); }
    }
    
    return false;
  }

  /** returns true if user has ADMIN or REGUL role */
  public final synchronized boolean canRegulate(final Authentication authentication)
  {
    if (authentication != null)
    {
      User found = userRepository.findByLoginName(authentication.getName());
      
      if (found != null)
      {
        List<String> roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) { return true; }
        if (roles.contains("ROLE_REGUL")) { return true; }
      }
    }
    
    return false;
  }


  /** returns true if user has ADMIN */
  public final synchronized boolean isAdmin(final Integer userId)
  {
    if (userId > 0)
    {
      User found = userRepository.findById(userId.intValue());
      
      if (found != null)
      {
        return found.hasRoles("ADMIN");
      }
    }
    
    return false;
  }
  
  /** set user's last activity timestamp and change SLEEPING status to ACTIVE */
  public final synchronized void setLastActivityOn(final Authentication authentication)
  {
    if (authentication == null) { return; }
    
    User u = userRepository.findByLoginName(authentication.getName());
    
    if (u != null)
    {
      u.setLastActivityOn(LocalDateTime.now());
      
      if (u.getStatus().equals(UserStatus.SLEEPING)) 
      { 
        u.setUpdatedOn(LocalDateTime.now());
        u.setStatus(UserStatus.ACTIVE); 
      }
      
      userRepository.saveAndFlush(u);
    }
  }
  
}
