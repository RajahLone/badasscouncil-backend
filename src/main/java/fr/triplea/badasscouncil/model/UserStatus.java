package fr.triplea.badasscouncil.model;

public enum UserStatus 
{
  
  ACTIVE("Active"), PENDING("Pending"), LOCKED("Locked"), BANNED("Banned"), SLEEPING("Sleeping");

  private String status;

  private UserStatus(String status) { this.status = status; }

  public String getStatus() { return this.status; }

  public static UserStatus getByStatus(String str) { for (UserStatus enu : UserStatus.values()) { if (enu.getStatus().equals(str)) { return enu; } } return null; }
  
  @Override
  public String toString() { return status; }

}
