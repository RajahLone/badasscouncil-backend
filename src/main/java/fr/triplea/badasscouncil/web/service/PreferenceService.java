package fr.triplea.badasscouncil.web.service;

import java.util.Map;

import com.google.common.base.Splitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import fr.triplea.badasscouncil.dao.PreferenceRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.model.Preference;
import fr.triplea.badasscouncil.model.User;

@Service
public class PreferenceService 
{


  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PreferenceRepository preferenceRepository;

  
  public String getString(int actionId, final Authentication authentication)
  {
    String ret = null;
    
    if ((actionId >= Preference.FIRST_ACTION) && (actionId <= Preference.LAST_ACTION))
    {
      if (authentication != null) 
      { 
        User u = userRepository.findByLoginName(authentication.getName());
        
        if (u != null)
        {
          try 
          { 
            Preference pref = preferenceRepository.findByUserAndAction(u.getUserId(), actionId);
            
            if (pref != null) { ret = pref.getParameters(); }
          } 
          catch (Exception e) { ret = null; }
        }
      }
    }     
    
    return ret;
  }

  
  public Integer getInteger(int actionId, final Authentication authentication)
  {
    Integer ret = null;
    
    if ((actionId >= Preference.FIRST_ACTION) && (actionId <= Preference.LAST_ACTION))
    {
      if (authentication != null) 
      { 
        User u = userRepository.findByLoginName(authentication.getName());
        
        if (u != null)
        {
          try 
          { 
            Preference pref = preferenceRepository.findByUserAndAction(u.getUserId(), actionId);
            
            if (pref != null) 
            {
              ret = Integer.valueOf(pref.getInteger());
            }
          } 
          catch (Exception e) { ret = null; }
        }
      }
    }     
    
    return ret;
  }
  
  
  public Map<String, String> getMap(int actionId, final Authentication authentication)
  {
    Map<String, String> ret = null;
    
    if ((actionId >= Preference.FIRST_ACTION) && (actionId <= Preference.LAST_ACTION))
    {
      if (authentication != null) 
      { 
        User u = userRepository.findByLoginName(authentication.getName());
        
        if (u != null)
        {
          try 
          { 
            Preference pref = preferenceRepository.findByUserAndAction(u.getUserId(), actionId);
            
            if (pref != null) 
            {
              ret = Splitter.on('&').trimResults().withKeyValueSeparator('=').split(pref.getParameters());
            }
          } 
          catch (Exception e) { ret = null; }
        }
      }
    }     
    
    return ret;
  }

  
  public Preference set(int actionId, String parameters, final Authentication authentication) 
  { 
    Preference pref = null;
    
    if ((actionId >= Preference.FIRST_ACTION) && (actionId <= Preference.LAST_ACTION))
    {
      if (authentication != null) 
      { 
        User u = userRepository.findByLoginName(authentication.getName());
        
        if (u != null)
        {
          try 
          { 
            pref = preferenceRepository.findByUserAndAction(u.getUserId(), actionId);
            
            if (pref != null)
            {
              pref.setParameters(parameters);
            }
            else
            {
              pref = new Preference();
              
              pref.setUser(u);
              pref.setActionId(actionId);
              pref.setParameters(parameters);
            }
            
            preferenceRepository.saveAndFlush(pref);
           } 
          catch (Exception e) { pref = null; }
        }
      }
    }  
    
    return pref;
  }

      
}
