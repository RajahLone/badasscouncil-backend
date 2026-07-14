package fr.triplea.badasscouncil.security;

public class ResourceNotFoundException extends RuntimeException 
{

  private static final long serialVersionUID = 7551018488670969405L;

  public ResourceNotFoundException(String message) { super(message); }
  
}