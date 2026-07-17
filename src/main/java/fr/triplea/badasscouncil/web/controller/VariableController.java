package fr.triplea.badasscouncil.web.controller;


import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.badasscouncil.dao.VariableRepository;
import fr.triplea.badasscouncil.dto.MessagesTransfer;
import fr.triplea.badasscouncil.dto.VariableFamily;
import fr.triplea.badasscouncil.model.Variable;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/variable")
public class VariableController 
{

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;

  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('ADMIN')")
  public List<Variable> getList(@RequestParam(required = false) String family) 
  { 
    if (family != null) { if (family.isBlank()) { family = null; } }
 
    return variableRepository.findByFamily(family); 
  }
  
  @GetMapping(value = "/option-list")
  @PreAuthorize("hasRole('ADMIN')")
  public List<VariableFamily> getOptionList() 
  { 
    return variableRepository.getFamilies(); 
  }
 
  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Variable> getForm(@PathVariable("id") int numeroVariable) 
  { 
    Variable v = variableRepository.findById(numeroVariable);
    
    if (v != null) { return ResponseEntity.ok(v); } 
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> create(@RequestBody(required = true) Variable variable, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Variable found = variableRepository.findById(0);
    
    if (found == null) { variable.setId(null); }
    
    if (variable.hasFamily() && variable.hasCode()) 
    { 
      variableRepository.saveAndFlush(variable); 
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("variable.created", null, locale));

      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build();
  }
 
  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> update(@PathVariable("id") int numeroVariable, @RequestBody(required = true) Variable variable, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Variable found = variableRepository.findById(numeroVariable);
    
    if (found != null)
    {
      found.setFamily(variable.getFamily());
      found.setCode(variable.getCode());
      found.setContent(variable.getContent());
      found.setNotes(variable.getNotes());
      
      variableRepository.saveAndFlush(found);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("variable.updated", null, locale));

      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> deleteVariable(@PathVariable int id, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Variable found = variableRepository.findById(id);

    if (found != null) 
    { 
      variableRepository.deleteById(id); 
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("variable.deleted", null, locale));

      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build(); 
  }

}
