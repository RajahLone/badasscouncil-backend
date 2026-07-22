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
    
    addVariableIfMissing("Application", "TIME_ZONE", "Europe/Paris");
        
    addVariableIfMissing("Messages", "HOME_ERROR", " ");
    addVariableIfMissing("Messages", "HOME_WARN", " ");
    addVariableIfMissing("Messages", "HOME_INFO", " ");
    addVariableIfMissing("Messages", "HOME_MISC", " ");
    
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
  public void addVariableIfMissing(final String type, final String code, final String valeur) 
  {
    String str = variableRepository.findByFamilyAndCode(type, code);
    
    if (str == null) 
    { 
      Variable variable = new Variable(); 

      variable.setFamily(type);
      variable.setCode(code);
      variable.setContent(valeur);
      
      variableRepository.saveAndFlush(variable);
    }
    
  }

}
