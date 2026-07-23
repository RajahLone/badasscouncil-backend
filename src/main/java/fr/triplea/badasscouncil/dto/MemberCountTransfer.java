package fr.triplea.badasscouncil.dto;

public class MemberCountTransfer
{

  long current = 0;
  long maximum = 0;
  
  public MemberCountTransfer() {}

  public void setCurrent(long n) { current = n; }
  public long getCurrent() { return current; }

  public void setMaximum(long n) { maximum = n; }
  public long getMaximum() { return maximum; }
  
}
