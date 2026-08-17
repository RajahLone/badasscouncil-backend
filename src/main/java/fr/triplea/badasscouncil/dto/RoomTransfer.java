package fr.triplea.badasscouncil.dto;

public class RoomTransfer 
{ 
  
  int roomId;
  String name;
  String state; 
 
  int ownerId;
  String password;
  String topic;
  
  String purgeMethod;
  int messagesLimit;
  int timeDuration;
  
  
  public int getRoomId() { return roomId; }
  public void setRoomId(int id) { this.roomId = id; }
  
  public String getName() { return name; }
  public void setName(String str) { this.name = str; }
  
  public String getState() { return state; }
  public void setState(String str) { this.state = str; }
  
  public int getOwnerId() { return ownerId; }
  public void setOwnerId(int id) { this.ownerId = id; }

  public String getPassword() { return password; }
  public void setPassword(String str) { this.password = str; }
  public boolean hasPassword() { if (password == null) { return false; } return (password.length() > 0); }

  public String getTopic() { return topic; }
  public void setTopic(String str) { this.topic = str; }
  
  public String getPurgeMethod() { return purgeMethod; }
  public void getPurgeMethod(String str) { this.purgeMethod = str; }

  public int getMessagesLimit() { return messagesLimit; }
  public void setMessagesLimit(int n) { this.messagesLimit = n; }
  
  public int getTimeDuration() { return timeDuration; }
  public void setTimeDuration(int n) { this.timeDuration = n; }

}
