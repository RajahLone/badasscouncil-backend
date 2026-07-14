package fr.triplea.badasscouncil.security.cors;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class CorsFilter extends OncePerRequestFilter 
{
  
  private final Logger LOG = LoggerFactory.getLogger(CorsFilter.class);

  @Value("${cors.allow.origin}")
  private String AccessControlAllowOriginUrl;

  public CorsFilter() { LOG.info("CorsFilter init"); }

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException
  {
    response.setHeader("Access-Control-Allow-Origin", AccessControlAllowOriginUrl);
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-XSRF-TOKEN");

    if (request.getMethod().equals("OPTIONS")) { response.setStatus(HttpServletResponse.SC_ACCEPTED); return; }

    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {}
  
}