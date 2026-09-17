package fr.triplea.badasscouncil.model;

public enum MessageType 
{
  
  TEXT("TEXT"), IMAGES("IMAGES"), URL("URL");

  private String messageType;

  private MessageType(String method) { this.messageType = method; }

  public String getMessageType() { return this.messageType; }

  public static MessageType getByMessageType(String str) { for (MessageType enu : MessageType.values()) { if (enu.getMessageType().equals(str)) { return enu; } } return null; }
  
  @Override
  public String toString() { return messageType; }

}
