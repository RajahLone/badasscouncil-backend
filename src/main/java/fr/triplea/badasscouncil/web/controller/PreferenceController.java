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

import fr.triplea.badasscouncil.model.Preference;
import fr.triplea.badasscouncil.web.service.PreferenceService;

@RestController
@RequestMapping("/preference")
public class PreferenceController 
{


  @Autowired
  private PreferenceService preferenceService;

  
  @GetMapping(value = "/get")
  @PreAuthorize("hasRole('USER')")
  public String get(
      @RequestParam(name = "action", required = true) int actionId, 
      final Authentication authentication
      ) 
  { 
    return preferenceService.getString(actionId, authentication);
  }

  @GetMapping(value = "/set")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> set(      
      @RequestParam(name = "action", required = true) int actionId, 
      @RequestParam(name = "params", required = true) String parameters, 
      final Authentication authentication
      ) 
  { 
    Preference pref = preferenceService.set(actionId, parameters, authentication);
    
    if (pref != null) 
    { 
      Map<String, Boolean> response = new HashMap<>();
      response.put("set", Boolean.TRUE);

      return ResponseEntity.ok(response); 
    }
      
    return ResponseEntity.notFound().build();
  }

}
