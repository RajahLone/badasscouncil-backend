package fr.triplea.badasscouncil.web.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.badasscouncil.dao.AttachmentRepository;
import fr.triplea.badasscouncil.dao.QuoteRepository;
import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dto.CaptchaTransfer;
import fr.triplea.badasscouncil.dto.HomeInformationTransfer;
import fr.triplea.badasscouncil.dto.ItemCountTransfer;
import fr.triplea.badasscouncil.model.Quote;
import fr.triplea.badasscouncil.web.service.UserService;
import fr.triplea.badasscouncil.web.service.VariableService;

@RestController
@RequestMapping("/misc")
public class MiscController 
{

  @Autowired
  private VariableService variableService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private QuoteRepository quoteRepository;


  @GetMapping(value = "/welcome")
  public ResponseEntity<HomeInformationTransfer> getWelcomeMessage() 
  { 
    HomeInformationTransfer mt = new HomeInformationTransfer();

    mt.setError(variableService.getString("Messages", "HOME_ERROR"));
    mt.setAlert(variableService.getString("Messages", "HOME_WARN"));
    mt.setInfo(variableService.getString("Messages", "HOME_INFO"));
    mt.setOther(variableService.getString("Messages", "HOME_MISC"));
    
    return ResponseEntity.ok(mt); 
  }

  @GetMapping(value = "/question/{type}")
  public ResponseEntity<CaptchaTransfer> getQuestion(@PathVariable("type") String page) 
  { 
    CaptchaTransfer ct = new CaptchaTransfer();

    ct.setQuestion("");
    ct.setResponse("");
    
    if (page.equalsIgnoreCase("subscribe"))
    {
      ct.setQuestion(variableService.getString("CAPTCHA", "SUBSCRIBE_QUESTION"));
      ct.setResponse(variableService.getString("CAPTCHA", "SUBSCRIBE_RESPONSE"));
      
    }
    else if (page.equalsIgnoreCase("login"))
    {
      ct.setQuestion(variableService.getString("CAPTCHA", "LOGIN_QUESTION"));
      ct.setResponse(variableService.getString("CAPTCHA", "LOGIN_RESPONSE"));
    }
    
    ct.validate();
        
    return ResponseEntity.ok(ct); 
  }
  
  @GetMapping(value = "/count/members")
  public ResponseEntity<ItemCountTransfer> getMembersCount() 
  { 
    ItemCountTransfer ict = new ItemCountTransfer();

    ict.setCurrent(userRepository.count());    
    ict.setMaximum(variableService.getLong("Quota", "MEMBERS_COUNT", 42));
    
    return ResponseEntity.ok(ict); 
  }
  
  @GetMapping(value = "/count/files/everyone")
  public ResponseEntity<ItemCountTransfer> getEveryoneAttachmentsCount(final Authentication authentication) 
  { 
    ItemCountTransfer ict = new ItemCountTransfer();

    ict.setCurrent(attachmentRepository.countForEveryone(userService.getUserId(authentication)));
    ict.setMaximum(variableService.getLong("Quota", "FILES_PER_MEMBER", 16));
    
    return ResponseEntity.ok(ict); 
  }

  @GetMapping(value = "/count/files/owner")
  public ResponseEntity<ItemCountTransfer> getOwnerAttachmentsCount(final Authentication authentication) 
  { 
    ItemCountTransfer ict = new ItemCountTransfer();

    ict.setCurrent(attachmentRepository.countForOwnerOnly(userRepository.findByLoginName(authentication.getName()).getUserId()));
    ict.setMaximum(variableService.getLong("Quota", "FILES_PER_MEMBER", 16));
    ict.setCapability(userService.canUpload(authentication));
    
    return ResponseEntity.ok(ict); 
  }
  
  @GetMapping(value = "/max/file/size")
  public ResponseEntity<ItemCountTransfer> getMaxFileSize() 
  { 
    ItemCountTransfer ict = new ItemCountTransfer();

    ict.setCurrent(0);
    ict.setMaximum(variableService.getLong("Quota", "FILE_SIZE", 1000));
    
    return ResponseEntity.ok(ict); 
  }
  
  @GetMapping(value = { "/quote", "/quote/{id} "})
  public ResponseEntity<Quote> getQuote(@PathVariable("id") Optional<Integer> quoteId) 
  { 
    Quote quote = null;
    
    if (quoteId.isPresent()) { quote = quoteRepository.findById(quoteId.get().intValue()); }
    
    if (quote == null) { quote = quoteRepository.getRandom(); }
    
    return ResponseEntity.ok(quote); 
  }



}
