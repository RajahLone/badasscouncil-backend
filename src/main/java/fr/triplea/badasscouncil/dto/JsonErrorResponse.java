package fr.triplea.badasscouncil.dto;

public class JsonErrorResponse 
{

  private int status;
  private String message;

  public JsonErrorResponse(int status, String message) 
  {
    this.status = status;
    this.message = message;
  }

  public void setStatus(int i) { this.status = i; }
  public int getStatus() { return this.status; }
  
  public void setMessage(String s) { this.message = new String(s); }
  public String getMessage() { return this.message; }
  
}
