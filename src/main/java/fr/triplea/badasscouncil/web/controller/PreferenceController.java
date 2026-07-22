package fr.triplea.badasscouncil.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.badasscouncil.dao.PreferenceRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.model.Preference;
import fr.triplea.badasscouncil.model.User;

@RestController
@RequestMapping("/preference")
public class PreferenceController 
{


  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PreferenceRepository preferenceRepository;

  
  @GetMapping(value = "/get")
  @PreAuthorize("hasRole('USER')")
  public Preference get(
      @RequestParam(name = "action", required = true) int actionId, 
      final Authentication authentication
      ) 
  { 
    Preference pref = new Preference();
    
    if ((actionId >= Preference.FIRST_ACTION) && (actionId <= Preference.LAST_ACTION))
    {
      if (authentication != null) 
      { 
        try 
        { 
          pref = preferenceRepository.findByUserAndAction(userRepository.findByLoginName(authentication.getName()).getUserId(), actionId);
        } 
        catch (Exception e) { pref = new Preference(); }
      }
    }    
    
    return pref; 
  }

  @GetMapping(value = "/set")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> set(      
      @RequestParam(name = "action", required = true) int actionId, 
      @RequestParam(name = "params", required = true) String parameters, 
      final Authentication authentication
      ) 
  { 
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
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("set", Boolean.TRUE);

            return ResponseEntity.ok(response); 
          } 
          catch (Exception e) { }
        }
      }
    }    
     
    return ResponseEntity.notFound().build();
  }

}
