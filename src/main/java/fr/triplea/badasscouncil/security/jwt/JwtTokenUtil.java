package fr.triplea.badasscouncil.security.jwt;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import fr.triplea.badasscouncil.model.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil 
{
  //@SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenUtil.class);

  @Value("${jwttoken.secret}")
  private String jwtTokenSecret;
 
  @Value("${jwttoken.expiration}")
  private long jwtTokenExpiration;

  private SecretKeySpec secret_key = null;
  
  private void setSecretKey() { if (secret_key == null) { try { secret_key = new SecretKeySpec(jwtTokenSecret.getBytes("UTF-8"), "HmacSHA256"); } catch (UnsupportedEncodingException e) { } } }
  
  public String generateJwtToken(Authentication authentication) 
  {
    MyUserDetails userPrincipal = (MyUserDetails)authentication.getPrincipal();
    
    return generateTokenFromPseudonyme(userPrincipal.getUsername());
  }
  
  public String generateTokenFromPseudonyme(String pseudonyme) 
  {
    setSecretKey();

    return Jwts.builder()
        .subject(pseudonyme)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtTokenExpiration))
        .signWith(secret_key)
        .compact();
  }
  
  public boolean validateJwtToken(String token) 
  {
    setSecretKey();

    try 
    {
      Jwts.parser().verifyWith(secret_key).build().parseSignedClaims(token);
     
      return true;
    }
    catch(UnsupportedJwtException exp) { LOG.error("claimsJws argument does not represent Claims JWS; " + exp.getMessage()); }
    catch(MalformedJwtException exp) { LOG.error("claimsJws string is not a valid JWS; " + exp.getMessage()); }
    catch(ExpiredJwtException exp) { /*LOG.info("Claims has an expiration time before the method is invoked; " + exp.getMessage());*/ }
    catch(IllegalArgumentException exp) { LOG.error("claimsJws string is null or empty or only whitespace; " + exp.getMessage()); }
    
    return false;
  }
  
  public String getUserNameFromJwtToken(String token) 
  {
    Claims claims = Jwts.parser().verifyWith(secret_key).build().parseSignedClaims(token).getPayload();
    
    return claims.getSubject();
  }
}