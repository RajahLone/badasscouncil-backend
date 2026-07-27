package fr.triplea.badasscouncil.dto;

public class ItemCountTransfer
{

  long current = 0;
  long maximum = 0;
  
  public ItemCountTransfer() {}

  public void setCurrent(long n) { current = n; }
  public long getCurrent() { return current; }

  public void setMaximum(long n) { maximum = n; }
  public long getMaximum() { return maximum; }
  
}
