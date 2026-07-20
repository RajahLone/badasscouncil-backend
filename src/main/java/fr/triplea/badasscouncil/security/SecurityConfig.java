package fr.triplea.badasscouncil.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.transport.HttpsRedirectFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.security.web.csrf.CsrfFilter;

import static org.springframework.security.config.Customizer.withDefaults;

import fr.triplea.badasscouncil.security.cors.CorsFilter;
import fr.triplea.badasscouncil.security.csrf.CsrfHeaderFilter;
import fr.triplea.badasscouncil.security.jwt.JwtTokenFilter;

import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration
@EnableWebMvc
@EnableMethodSecurity
public class SecurityConfig
{
 
  @Bean
  public ResourceBundleMessageSource messageSource() 
  {
    var source = new ResourceBundleMessageSource();
  
    source.setBasenames("langs/messages");
    source.setUseCodeAsDefaultMessage(true);

    return source;
  }

  @Autowired
  private MyUserDetailsService myUserDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(11); }
  
  @Bean
  public AuthenticationManager authenticationManager() throws Exception 
  {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(myUserDetailsService);
    
    authProvider.setPasswordEncoder(passwordEncoder());
    
    return new ProviderManager(authProvider);
  }
  
  @Bean
  public SecurityContextRepository securityContextRepository() 
  {
    return new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository());
  }

  @Bean
  public SessionRegistry sessionRegistry() { return new SessionRegistryImpl(); }

  @Bean
  public JwtTokenFilter jwtTokenFilter() { return new JwtTokenFilter(); }
  
  Class<? extends UsernamePasswordAuthenticationFilter> upaf_clazz = UsernamePasswordAuthenticationFilter.class;

  @Bean
  public CorsFilter corsFilter() { return new CorsFilter(); }
 
  Class<? extends HttpsRedirectFilter> cpf_clazz = HttpsRedirectFilter.class;

  private CsrfTokenRepository csrfTokenRepository() 
  {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    
    repository.setHeaderName("X-XSRF-TOKEN"); // Angular: "XSRF" used, not "CSRF"
    
    return repository;
  }
  
  @Bean
  public CsrfHeaderFilter csrfHeaderFilter() { return new CsrfHeaderFilter(); }
  
  Class<? extends CsrfFilter> csrfhf_clazz = CsrfFilter.class;
  
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception 
  {
    http.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository()))
        .redirectToHttps(withDefaults())
        .authenticationManager(authenticationManager())
        .authorizeHttpRequests((ahreq) -> ahreq
          .requestMatchers("/misc/**", "/sign/**").permitAll()
          .requestMatchers("/account/**", "/preference/**", "/message/**", "/attachment/**").hasRole("USER")
          .requestMatchers("/user/**").hasRole("REGUL")
          .requestMatchers("/variable/**").hasRole("ADMIN")
          .anyRequest().authenticated()
          )
        .addFilterBefore(jwtTokenFilter(), upaf_clazz)
        .addFilterBefore(corsFilter(), cpf_clazz)
        .addFilterAfter(csrfHeaderFilter(), csrfhf_clazz)
        .securityContext(sc -> sc.securityContextRepository(securityContextRepository).requireExplicitSave(true))
        .headers(headers -> headers
          .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
          .contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'"))
          .frameOptions(fopt -> fopt.sameOrigin())
          .cacheControl(cache -> cache.disable())
          .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true).maxAgeInSeconds(31536000))
          .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.SAME_ORIGIN))
          )
        .sessionManagement(session -> session.maximumSessions(2).sessionRegistry(sessionRegistry()))
        ;
        
    return http.build();
  }

}
