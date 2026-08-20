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
  
  private String purgeMethod;
  private int messagesLimit;
  private int timeDuration;
  
  
  public RoomTransfer(String c, String u, int r, String n, String s, int o, String p, String t, String m, int l, int d) 
  {
    this.createdOn = c;
    this.updatedOn = u;
    this.roomId = r;
    this.name = n;
    this.state = s;
    this.ownerId = o;
    this.password = p;
    this.topic = t;
    this.purgeMethod = m;
    this.messagesLimit = l;
    this.timeDuration = d;
  }

  
  public String getCreatedOn() { return createdOn; }
  public void setCreatedOn(String str) { this.createdOn = str; }
  
  public String getUpdatedOn() { return updatedOn; }
  public void setUpdatedOn(String str) { this.updatedOn = str; }
  
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
  public void setPurgeMethod(String str) { this.purgeMethod = str; }

  public int getMessagesLimit() { return messagesLimit; }
  public void setMessagesLimit(int n) { this.messagesLimit = n; }
  
  public int getTimeDuration() { return timeDuration; }
  public void setTimeDuration(int n) { this.timeDuration = n; }

}
