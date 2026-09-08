package fr.triplea.badasscouncil;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.triplea.badasscouncil.dao.MessageRepository;
import fr.triplea.badasscouncil.dao.QuoteRepository;
import fr.triplea.badasscouncil.dao.RoleRepository;
import fr.triplea.badasscouncil.dao.RoomRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dao.VariableRepository;
import fr.triplea.badasscouncil.dto.NickNameOptionList;
import fr.triplea.badasscouncil.dto.RoomRecord;
import fr.triplea.badasscouncil.model.Message;
import fr.triplea.badasscouncil.model.Quote;
import fr.triplea.badasscouncil.model.Role;
import fr.triplea.badasscouncil.model.Room;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Variable;

@Component
public class CreateDefaultValues implements ApplicationListener<ContextRefreshedEvent>
{
  
  boolean initialise = false;

  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) 
  {
    if (initialise) { return; } 

    Locale.setDefault(Locale.ENGLISH);
      
    
    addRoleIfMissing("ROLE_ADMIN");
    addRoleIfMissing("ROLE_REGUL");
    addRoleIfMissing("ROLE_USER");
        
    
    String tz = addVariableIfMissing("Application", "TIME_ZONE", "Europe/Paris", "");
        
    if (tz != null) { TimeZone.setDefault(TimeZone.getTimeZone(tz)); } 
    
    
    addVariableIfMissing("Messages", "HOME_ERROR", " ", "If not blank, this error message will be displayed for all (even not logged people) on the home page.");
    addVariableIfMissing("Messages", "HOME_WARN", " ", "If not blank, this warning message will be displayed for all (even not logged people) on the home page.");
    addVariableIfMissing("Messages", "HOME_INFO", " ", "If not blank, this information message will be displayed for all (even not logged people) on the home page.");
    addVariableIfMissing("Messages", "HOME_MISC", " ", "If not blank, this neutral message will be displayed for all (even not logged people) on the home page.");

    addVariableIfMissing("CAPTCHA", "SUBSCRIBE_QUESTION", " ", "If question and its response not blank, this will be displayed when subscribing. Choose a private question, on which response is unknown to the internet.");
    addVariableIfMissing("CAPTCHA", "SUBSCRIBE_RESPONSE", " ", "If not blank, mandatory response is required for the subscription to succeed.");
    addVariableIfMissing("CAPTCHA", "LOGIN_QUESTION", " ", "If question and its reponse not blank, this will be displayed when signing in. Choose a private question, on which response is unknown to the internet.");
    addVariableIfMissing("CAPTCHA", "LOGIN_RESPONSE", " ", "If not blank, mandatory response is required when signing in.");

    addVariableIfMissing("Quota", "MEMBERS_COUNT", "42", "Maximum count for members.");
    addVariableIfMissing("Quota", "FILES_PER_MEMBER", "16", "Maximum files per members.");
    addVariableIfMissing("Quota", "FILE_SIZE", "1000", "Maximum file size (in MB).");
    addVariableIfMissing("Quota", "STORAGE_DEFAULT", "0", "-1 : not yet allowed to upload, 0 : follows FILES_PER_MEMBER * FILE_SIZE limit, > 0 : limit in GB ");

    addVariableIfMissing("Users", "SLEEPING_STATUS_AFTER", "3", "set SLEEPING status after N months of inactivity, 0 to avoid status change ");
    
    addQuote(1, "What happens in your Bad Ass instance... will eventually be purged.");
    addQuote(2, "Where you can count very few people and, at least, a terrifyingly good person.");
    addQuote(3, "Headology is not reserved solely for women.");
    addQuote(4, "We few, we happy few, we band of bastards.");
    addQuote(5, "Home to at least one goat.");
    addQuote(6, "Named after a donkey that refused to move.");
    addQuote(7, "A hidden village tucked in a narrow valley between steep woods.");
    addQuote(8, "The name may sound crude, but you are not one for senseless niceties.");
    addQuote(9, "You're not allowed to think about the Dungeon Dimensions.");
    
    //addLinesForTests();
    
    initialise = true;
  }
  

  @Autowired
  private RoleRepository roleRepository;

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


  @Autowired
  private VariableRepository variableRepository;

  @Transactional
  public String addVariableIfMissing(final String type, final String code, final String content, final String notes) 
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
    
    return str;
  }

  
  @Autowired
  private QuoteRepository quoteRepository;

  @Transactional
  public void addQuote(final int id, final String content) 
  {
    Quote quote = quoteRepository.findById(id);
    
    if (quote == null) 
    { 
      quote = new Quote(); 
      
      quote.setContent(content); 

      quote = quoteRepository.saveAndFlush(quote);
    }
  }
  

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private MessageRepository messageRepository;

  @Transactional
  public void addLinesForTests() 
  {
    List<RoomRecord> rl = roomRepository.listRooms();
    List<NickNameOptionList> ul = userRepository.getAll();
 
    if ((rl != null) && (ul != null))
    {
      if ((rl.size() > 0) && (ul.size() > 0))
      {
        long n = messageRepository.count(rl.get(0).roomId()) - 1;
        
        if (n > 3000) { return; }
        
        Room r = roomRepository.findById(rl.get(0).roomId());
        User u = userRepository.findById(ul.get(0).userId().intValue());
        
        if ((r != null) && (u != null))
        {
          for (int i = 0; i < 1000; i++)
          {
            Message m = new Message();
            
            m.setMessageId(null);
            m.setRoom(r);
            m.setUser(u);
            m.setContent("test line " + (n + i));
            m.setDest(null);
            
            messageRepository.saveAndFlush(m);
          }
        }
      }
    }
  }
  
  
}
