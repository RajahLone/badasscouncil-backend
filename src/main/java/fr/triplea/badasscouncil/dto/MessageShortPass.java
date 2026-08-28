package fr.triplea.badasscouncil.dto;

public class MessageShortPass
{
  String createdOn;
  int messageId;
  String nickName;
  String content;
  int destId;
  String destName;
  String password;
  
  public String getCreatedOn() { return createdOn; }
  public int getMessageId() { return messageId; }
  public String getNickName() { return nickName; }
  public String getContent() { return content; }
  public int getDestId() { return destId; }
  public String getDestName() { return destName; }
  public String getPassword() { return password; }
  
  public void setCreatedOn(String createdOn) { this.createdOn = createdOn; }
  public void setMessageId(int messageId) { this.messageId = messageId; }
  public void setNickName(String nickName) { this.nickName = nickName; }
  public void setContent(String content) { this.content = content; }
  public void setDestId(int destId) { this.destId = destId; }
  public void setDestName(String destName) { this.destName = destName; }
  public void setPassword(String password) { this.password = password; }
  
}
