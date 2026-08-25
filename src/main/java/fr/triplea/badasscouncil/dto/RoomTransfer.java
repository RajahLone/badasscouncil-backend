package fr.triplea.badasscouncil.dto;

public class RoomTransfer 
{ 
  
  private String createdOn;
  private String updatedOn;
  private int roomId;

  private String name;
  private String state; 
  private int ownerId;
  private String password;
  private String topic;
  private String notes;
  
  private String purgeType;
  private int messagesLimit;
  private int timeDuration;

  public RoomTransfer() {}
  
  public String getCreatedOn() { return createdOn; }
  public void setCreatedOn(String str) { createdOn = str; }
  
  public String getUpdatedOn() { return updatedOn; }
  public void setUpdatedOn(String str) { updatedOn = str; }
  
  public int getRoomId() { return roomId; }
  public void setRoomId(int id) { roomId = id; }
  
  public String getName() { return name; }
  public void setName(String str) { name = str; }
  
  public String getState() { return state; }
  public void setState(String str) { state = str; }
  
  public int getOwnerId() { return ownerId; }
  public void setOwnerId(int id) { ownerId = id; }

  public String getPassword() { return password; }
  public void setPassword(String str) { password = str; }
  public boolean hasPassword() { if (password == null) { return false; } return (password.length() > 0); }

  public String getTopic() { return topic; }
  public void setTopic(String str) { topic = str; }
  
  public String getNotes() { return notes; }
  public void setNotes(String str) { notes = str; }

  public String getPurgeType() { return purgeType; }
  public void setPurgeType(String str) { purgeType = str; }

  public int getMessagesLimit() { return messagesLimit; }
  public void setMessagesLimit(int n) { messagesLimit = n; }
  
  public int getTimeDuration() { return timeDuration; }
  public void setTimeDuration(int n) { timeDuration = n; }

}
