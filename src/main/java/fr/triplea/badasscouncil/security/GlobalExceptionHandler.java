package fr.triplea.badasscouncil.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.triplea.badasscouncil.dto.JsonErrorResponse;
import fr.triplea.badasscouncil.security.jwt.RefreshTokenException;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
  
  //@SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  
  @Value("${production.mode}")
  private boolean modeProduction;
  

  @ExceptionHandler(value = RefreshTokenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<JsonErrorResponse> handleTokenRefreshException(RefreshTokenException ex) 
  {
    LOG.error(ex.getMessage()); //ex.printStackTrace();
    
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());

    return new ResponseEntity<>(jer, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<JsonErrorResponse> handleAllExceptions(Exception ex) 
  {
    LOG.error(ex.getMessage()); //ex.printStackTrace();
    
    String message =  ex.getMessage();
    
    if (modeProduction) { if (message.contains("JDBC") || message.contains("SQL")) { message = "JDBC or SQL error, please contact the administrator to look in logs"; } }
    
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);

    return new ResponseEntity<>(jer, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<JsonErrorResponse> handleNullPointerException(NullPointerException ex) 
  {
    LOG.error(ex.getMessage()); //ex.printStackTrace();
    
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

    return new ResponseEntity<>(jer, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<JsonErrorResponse> handleNotFound(ResourceNotFoundException ex) 
  {
    LOG.error(ex.getMessage()); //ex.printStackTrace();
    
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    
    return new ResponseEntity<>(jer, HttpStatus.NOT_FOUND);
  }

}