package fr.triplea.badasscouncil.model;

public enum RoomPurgeType 
{
  
  NEVER("NEVER"), MESSAGES_LIMITED("MESSAGES_LIMITED"), TIME_LIMITED("TIME_LIMITED");

  private String purgeType;

  private RoomPurgeType(String method) { this.purgeType = method; }

  public String getPurgeType() { return this.purgeType; }

  public static RoomPurgeType getByPurgeType(String str) { for (RoomPurgeType enu : RoomPurgeType.values()) { if (enu.getPurgeType().equals(str)) { return enu; } } return null; }
  
  @Override
  public String toString() { return purgeType; }

}
