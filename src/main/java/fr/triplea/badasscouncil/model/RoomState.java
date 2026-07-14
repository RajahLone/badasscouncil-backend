package fr.triplea.badasscouncil.model;

public enum RoomState 
{
  
  ACTIVE("ACTIVE"), LOCKED("LOCKED"), TRASHED("TRASHED");

  private String state;

  private RoomState(String status) { this.state = status; }

  public String getState() { return this.state; }

  public static RoomState getByState(String str) { for (RoomState enu : RoomState.values()) { if (enu.getState().equals(str)) { return enu; } } return null; }
  
  @Override
  public String toString() { return state; }

}
