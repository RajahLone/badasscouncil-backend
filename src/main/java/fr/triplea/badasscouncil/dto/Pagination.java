package fr.triplea.badasscouncil.dto;

public class Pagination 
{

  /** items number */
  int items = 0;
  /** items number in a page */
  int size = 100;
  /** pages number */
  int total = 1;
  /** current page, starts at 0 */
  int current = 0;

  public Pagination(int items, int size, int total, int current) 
  {
    this.items = items;
    this.size = size;
    this.total = total;
    this.current = current;
  }

  public void setItems(int items) { this.items = items; }
  public int getItems() { return items; }

  public void setSize(int size) { this.size = size; }
  public int getSize() { return size; }
  
  public void setTotal(int total) { this.total = total; }
  public int getTotal() { return total; }
  
  public void setCurrent(int current) { this.current = current; }
  public int getCurrent() { return current; }

}
