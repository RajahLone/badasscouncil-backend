package fr.triplea.badasscouncil.security;

public class ResourceNotFoundException extends RuntimeException 
{

  private static final long serialVersionUID = -4890815205057322578L;

  public ResourceNotFoundException(String message) { super(message); }
  
}