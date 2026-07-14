package fr.triplea.badasscouncil.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.badasscouncil.dao.PreferenceRepository;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Preference;

@RestController
@RequestMapping("/preference")
public class PreferenceController 
{

  @Autowired
  private PreferenceRepository preferenceRepository;

  
  @PostMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<Preference> get(@RequestParam(required = true) User userId, @RequestParam(required = false) int actionId) 
  { 
    return preferenceRepository.findByParticipantAndTraitement(userId, actionId); 
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public Preference create(@RequestBody(required = true) Preference preference) 
  { 
    Preference found = preferenceRepository.findById(0);

    if (found == null) { preference.setId(null); }
    
    return  preferenceRepository.saveAndFlush(preference);
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Preference> update(@PathVariable("id") int preferenceId, @RequestBody(required = true) Preference preference) 
  { 
    Preference found = preferenceRepository.findById(preferenceId);
    
    if (found != null)
    {
      found.setUser(preference.getUser());
      found.setActionId(preference.getActionId());
      found.setParameters(preference.getParameters());
      
      preferenceRepository.saveAndFlush(found);
    
      return ResponseEntity.ok(found);
    }
    
    return ResponseEntity.notFound().build();
  }
 
}
