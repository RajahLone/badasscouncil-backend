package fr.triplea.badasscouncil.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.model.MyUserDetails;
import fr.triplea.badasscouncil.model.User;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService 
{

  @Autowired
  private UserRepository userRepository;

  
  public MyUserDetailsService() { }

  
  @Override
  public MyUserDetails loadUserByUsername(final String loginName) throws UsernameNotFoundException 
  {
    try 
    {
      final User u = userRepository.findByLoginName(loginName);
      
      if (u == null) { throw new UsernameNotFoundException("Login name not found: " + loginName); }

      return MyUserDetails.createInstance(u);
    } 
    catch (final Exception e) { throw new RuntimeException(e); }
   }

}
