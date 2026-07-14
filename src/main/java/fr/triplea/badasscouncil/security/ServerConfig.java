package fr.triplea.badasscouncil.security;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import fr.triplea.badasscouncil.security.xss.XssFilter;

@Configuration
@EnableScheduling
public class ServerConfig 
{

  @Bean
  public FilterRegistrationBean<XssFilter> loggingFilterRegistration() 
  {
    FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
    
    registrationBean.setFilter(new XssFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(1); 
    registrationBean.setName("xssFilter");
        
    return registrationBean;
  }

  @Bean
  public ServletWebServerFactory servletContainer() 
  {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
      
      @Override
      protected void postProcessContext(Context context) {
        var securityConstraint = new SecurityConstraint();
        
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        
        var collection = new SecurityCollection();
        
        collection.addPattern("/*");
        
        securityConstraint.addCollection(collection);
        
        context.addConstraint(securityConstraint);
      }
    };
    
    tomcat.addAdditionalTomcatConnectors(getHttpConnector());
    
    return tomcat;
  }

  private Connector getHttpConnector() 
  {
    var connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
  
    connector.setScheme("http");
    connector.setPort(8080);
    connector.setSecure(false);
    connector.setRedirectPort(8443);
    
    return connector;
  }

}