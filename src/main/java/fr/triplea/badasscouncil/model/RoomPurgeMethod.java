package fr.triplea.badasscouncil.model;

public enum RoomPurgeMethod 
{
  
  NEVER("NEVER"), MESSAGES_LIMITED("MESSAGES_LIMITED"), TIME_LIMITED("TIME_LIMITED"), WHEN_DEPOPULATED("Sleeping");

  private String purgeMethod;

  private RoomPurgeMethod(String method) { this.purgeMethod = method; }

  public String getPurgeMethod() { return this.purgeMethod; }

  public static RoomPurgeMethod getByPurgeMethod(String str) { for (RoomPurgeMethod enu : RoomPurgeMethod.values()) { if (enu.getPurgeMethod().equals(str)) { return enu; } } return null; }
  
  @Override
  public String toString() { return purgeMethod; }

}
