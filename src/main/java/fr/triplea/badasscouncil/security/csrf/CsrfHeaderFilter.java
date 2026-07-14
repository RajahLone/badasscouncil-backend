package fr.triplea.badasscouncil.security.csrf;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class CsrfHeaderFilter extends OncePerRequestFilter 
{
  
  private final Logger LOG = LoggerFactory.getLogger(CsrfHeaderFilter.class);

  public CsrfHeaderFilter() { LOG.info("CsrfHeaderFilter init"); }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
  {
    CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());   
    
    if (csrf != null) 
    {
      Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN"); // Angular: "XSRF" et non pas "CSRF"

      String token = csrf.getToken();
      
      if ((cookie == null) || ((token != null) && !(token.equals(cookie.getValue())))) 
      {
        cookie = new Cookie("XSRF-TOKEN", token);
        cookie.setPath("/");
        
        response.addCookie(cookie);
      }
    }
      
    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {}

}