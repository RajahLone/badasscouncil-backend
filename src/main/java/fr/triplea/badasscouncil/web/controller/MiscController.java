package fr.triplea.badasscouncil.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dao.VariableRepository;
import fr.triplea.badasscouncil.dto.CaptchaTransfer;
import fr.triplea.badasscouncil.dto.HomeInformationTransfer;
import fr.triplea.badasscouncil.dto.MemberCountTransfer;

@RestController
@RequestMapping("/misc")
public class MiscController 
{

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private UserRepository userRepository;
 

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
  public ResponseEntity<MemberCountTransfer> getMembersCount() 
  { 
    MemberCountTransfer mct = new MemberCountTransfer();

    mct.setCurrent(userRepository.count());
    
    long max = 42;
    try { max = Long.parseLong(variableRepository.findByFamilyAndCode("Quota", "MEMBERS_COUNT")); } catch (Exception e) { max = -1; }
    if (max < 1) { max = 42; }
    
    mct.setMaximum(max);
    
    return ResponseEntity.ok(mct); 
  }
 
}
