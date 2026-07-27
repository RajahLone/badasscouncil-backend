package fr.triplea.badasscouncil.web.controller;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import fr.triplea.badasscouncil.dao.VariableRepository;
import fr.triplea.badasscouncil.dto.CaptchaTransfer;
import fr.triplea.badasscouncil.dto.HomeInformationTransfer;
import fr.triplea.badasscouncil.dto.ItemCountTransfer;
import fr.triplea.badasscouncil.model.Quote;
import fr.triplea.badasscouncil.model.User;

@RestController
@RequestMapping("/misc")
public class MiscController 
{

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private QuoteRepository quoteRepository;


  @GetMapping(value = "/welcome")
  public ResponseEntity<HomeInformationTransfer> getWelcomeMessage() 
  { 
    HomeInformationTransfer mt = new HomeInformationTransfer();

    mt.setError(variableRepository.findByFamilyAndCode("Messages", "HOME_ERROR"));
    mt.setAlert(variableRepository.findByFamilyAndCode("Messages", "HOME_WARN"));
    mt.setInfo(variableRepository.findByFamilyAndCode("Messages", "HOME_INFO"));
    mt.setOther(variableRepository.findByFamilyAndCode("Messages", "HOME_MISC"));
    
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
      ct.setQuestion(variableRepository.findByFamilyAndCode("CAPTCHA", "SUBSCRIBE_QUESTION"));
      ct.setResponse(variableRepository.findByFamilyAndCode("CAPTCHA", "SUBSCRIBE_RESPONSE"));
      
    }
    else if (page.equalsIgnoreCase("login"))
    {
      ct.setQuestion(variableRepository.findByFamilyAndCode("CAPTCHA", "LOGIN_QUESTION"));
      ct.setResponse(variableRepository.findByFamilyAndCode("CAPTCHA", "LOGIN_RESPONSE"));
    }
    
    ct.validate();
        
    return ResponseEntity.ok(ct); 
  }
  
  @GetMapping(value = "/count/members")
  public ResponseEntity<ItemCountTransfer> getMembersCount() 
  { 
    ItemCountTransfer ict = new ItemCountTransfer();

    ict.setCurrent(userRepository.count());
    
    long max = 42;
    try { max = Long.parseLong(variableRepository.findByFamilyAndCode("Quota", "MEMBERS_COUNT")); } catch (Exception e) { max = -1; }
    if (max < 1) { max = 42; }
    
    ict.setMaximum(max);
    
    return ResponseEntity.ok(ict); 
  }
  
  @GetMapping(value = "/count/files/everyone")
  public ResponseEntity<ItemCountTransfer> getEveryoneAttachmentsCount(final Authentication authentication) 
  { 
    ItemCountTransfer ict = new ItemCountTransfer();

    ict.setCurrent(attachmentRepository.countForEveryone(this.getUserId(authentication)));
    
    long max = 16;
    try { max = Long.parseLong(variableRepository.findByFamilyAndCode("Quota", "FILES_PER_MEMBER")); } catch (Exception e) { max = -1; }
    if (max < 1) { max = 16; }
        
    ict.setMaximum(max);
    
    return ResponseEntity.ok(ict); 
  }
  
  @GetMapping(value = "/count/files/owner")
  public ResponseEntity<ItemCountTransfer> getOwnerAttachmentsCount(final Authentication authentication) 
  { 
    ItemCountTransfer ict = new ItemCountTransfer();

    ict.setCurrent(attachmentRepository.countForOwnerOnly(userRepository.findByLoginName(authentication.getName()).getUserId()));
    
    long max = 16;
    try { max = Long.parseLong(variableRepository.findByFamilyAndCode("Quota", "FILES_PER_MEMBER")); } catch (Exception e) { max = -1; }
    if (max < 1) { max = 16; }
        
    ict.setMaximum(max);
    
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


  /** returns 0 if ROLE_ADMIN, else if USER id */
  private final int getUserId(Authentication auth)
  {
    int userId = -1; // -1 = not found
    
    if (auth != null)
    {
      User found = userRepository.findByLoginName(auth.getName());
      
      if (found != null)
      {
        userId = found.getUserId();
        
        List<String> roles = auth.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) { userId = 0; }
      }
    }
    
    return userId;
  }

}
