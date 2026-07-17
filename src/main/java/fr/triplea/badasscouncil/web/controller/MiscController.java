package fr.triplea.badasscouncil.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.badasscouncil.dao.VariableRepository;
import fr.triplea.badasscouncil.dto.MessagesTransfer;

@RestController
@RequestMapping("/misc")
public class MiscController 
{

  @Autowired
  private VariableRepository variableRepository;
 
  @GetMapping(value = "/welcome")
  public ResponseEntity<MessagesTransfer> getWelcomeMessage() 
  { 
    MessagesTransfer mt = new MessagesTransfer();

    mt.setError(variableRepository.findByFamilyAndCode("Messages", "HOME_ERROR"));
    mt.setAlerte(variableRepository.findByFamilyAndCode("Messages", "HOME_WARN"));
    mt.setInformation(variableRepository.findByFamilyAndCode("Messages", "HOME_INFO"));
    mt.setMiscellaneous(variableRepository.findByFamilyAndCode("Messages", "HOME_MISC"));
    
    return ResponseEntity.ok(mt); 
  }

 
}
