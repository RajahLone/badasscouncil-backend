package fr.triplea.badasscouncil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${admin.email.address}")
  private String adminEmailAddress;
  
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
      
    Role adminRole = addRoleIfMissing("ROLE_ADMIN");
    Role regulRole = addRoleIfMissing("ROLE_REGUL");
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
      
      if (adminEmailAddress != null)
      {
        if (user.getEmail().equalsIgnoreCase(adminEmailAddress))
        {
          if (!roles.contains(adminRole)) { roles.add(adminRole); changed = true; }
          if (!roles.contains(regulRole)) { roles.add(regulRole); changed = true; }
        }
      }
      
      if (changed)
      {
        user.setRoles(roles); 
        userRepository.saveAndFlush(user);
      }
    }
    
    addVariableIfMissing("Application", "TIME_ZONE", "Europe/Paris");
   
    addVariableIfMissing("Navigation", "LISTING_MAX_USERS", "300");
     
    addVariableIfMissing("Messages", "ACCUEIL_ERREUR", "message d'erreur paramétrable côté backend.");
    addVariableIfMissing("Messages", "ACCUEIL_ALERTE", "message d'alerte paramétrable côté backend.  ");
    addVariableIfMissing("Messages", "ACCUEIL_INFORMATION", "message d'information paramétrable côté backend.  ");
    addVariableIfMissing("Messages", "ACCUEIL_AUTRE", "message neutre paramétrable côté backend.  ");
    
    initialise = true;
  }

  @Transactional
  public Role addRoleIfMissing(final String libelle) 
  {
    Role role = roleRepository.findByLibelle(libelle);
    
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
