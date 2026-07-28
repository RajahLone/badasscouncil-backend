package fr.triplea.badasscouncil.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.triplea.badasscouncil.dao.VariableRepository;

@Service
public class VariableService 
{


  @Autowired
  private VariableRepository variableRepository;

  
  public String getString(String type, String key) { return variableRepository.findByFamilyAndCode(type, key); }

  public long getLong(String type, String key, long defaultValue)
  {
    long value = defaultValue;
    
    try { value = Long.parseLong(variableRepository.findByFamilyAndCode(type, key)); } catch (Exception e) { value = defaultValue; }

    return value;
  }
      
}
