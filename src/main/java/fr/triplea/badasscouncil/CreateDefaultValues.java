package fr.triplea.badasscouncil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dao.RoleRepository;
import fr.triplea.badasscouncil.dao.VariableRepository;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Role;
import fr.triplea.badasscouncil.model.Variable;

@Component
public class CreateDefaultValues implements ApplicationListener<ContextRefreshedEvent>
{
  
  boolean initialise = false;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private VariableRepository variableRepository;

  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) 
  {
    if (initialise) { return; } 

    Locale.setDefault(Locale.ENGLISH);
      
    addRoleIfMissing("ROLE_ADMIN");
    addRoleIfMissing("ROLE_REGUL");
    Role userRole = addRoleIfMissing("ROLE_USER");
    
    
    List<User> users = userRepository.findAll();
    
    for (User user : users)
    {
      boolean changed = false;
      
      List<Role> roles = user.getRoles();
      
      if (roles == null) 
      { 
        roles = Arrays.asList(userRole);
        changed = true;
      } 
      else 
      { 
        if (!roles.contains(userRole)) 
        { 
          roles.add(userRole);
          changed = true;
        }
      }
      
      if (changed)
      {
        user.setRoles(roles); 
        userRepository.saveAndFlush(user);
      }
    }
    
    addVariableIfMissing("Application", "TIME_ZONE", "Europe/Paris", "");
        
    addVariableIfMissing("Messages", "HOME_ERROR", " ", "If not blank, this error message will be displayed for all (even not logged people) on the home page.");
    addVariableIfMissing("Messages", "HOME_WARN", " ", "If not blank, this warning message will be displayed for all (even not logged people) on the home page.");
    addVariableIfMissing("Messages", "HOME_INFO", " ", "If not blank, this information message will be displayed for all (even not logged people) on the home page.");
    addVariableIfMissing("Messages", "HOME_MISC", " ", "If not blank, this neutral message will be displayed for all (even not logged people) on the home page.");

    addVariableIfMissing("CAPTCHA", "SUBSCRIBE_QUESTION", " ", "If question and its response not blank, this will be displayed when subscribing. Choose a private question, on which response is unknown to the internet.");
    addVariableIfMissing("CAPTCHA", "SUBSCRIBE_RESPONSE", " ", "If not blank, mandatory response is required for the subscription to succeed.");
    addVariableIfMissing("CAPTCHA", "LOGIN_QUESTION", " ", "If question and its reponse not blank, this will be displayed when signing in. Choose a private question, on which response is unknown to the internet.");
    addVariableIfMissing("CAPTCHA", "LOGIN_RESPONSE", " ", "If not blank, mandatory response is required when signing in.");

    initialise = true;
  }

  @Transactional
  public Role addRoleIfMissing(final String libelle) 
  {
    Role role = roleRepository.findByLabel(libelle);
    
    if (role == null) 
    { 
      role = new Role(); 
      
      role.setLibelle(libelle); 

      role = roleRepository.saveAndFlush(role);
    }
     
    return role;
  }

  @Transactional
  public void addVariableIfMissing(final String type, final String code, final String content, final String notes) 
  {
    String str = variableRepository.findByFamilyAndCode(type, code);
    
    if (str == null) 
    { 
      Variable variable = new Variable(); 

      variable.setFamily(type);
      variable.setCode(code);
      variable.setContent(content);
      variable.setNotes(notes);
      
      variableRepository.saveAndFlush(variable);
    }
    
  }

}
