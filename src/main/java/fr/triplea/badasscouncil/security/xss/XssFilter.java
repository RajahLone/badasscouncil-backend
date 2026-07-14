package fr.triplea.badasscouncil.security.xss;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

@WebFilter("/*")
@Order(1)
public class XssFilter implements Filter 
{
  
  private final Logger LOG = LoggerFactory.getLogger(XssFilter.class);

  public XssFilter() { LOG.info("XssFilter init"); }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
  {
    chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
  }

}