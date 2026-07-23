package fr.triplea.badasscouncil.dto;

public class CaptchaTransfer
{

  String question = "";
  String response = "";
  
  public CaptchaTransfer() {}

  public void setQuestion(String str) { if (str != null) { question = str.trim(); } }
  public String getQuestion() { return question; }

  public void setResponse(String str) { if (str != null) { response = str.trim(); } }
  public String getResponse() { return response; }

  public void validate() 
  {
    if ((question == null) || (response == null))
    {
      question = "";
      response = "";
    }
    if ((question.isEmpty()) || (response.isEmpty()))
    {
      question = "";
      response = "";
    }
    
    response = "You wouldn't cheat on a sphinge, believe me."; // don't send an
  }
  
}
