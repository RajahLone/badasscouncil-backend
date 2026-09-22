package fr.triplea.badasscouncil.dto;

public class ApplicationInfo
{
  String version;
  String framework;
  String date;
  String[] authors;
  
  public String getVersion() { return version; }
  public String getFramework() { return framework; }
  public String getDate() { return date; }
  public String[] getAuthors() { return authors; }
  
  public void setVersion(String str) { this.version = str; }
  public void setFramework(String str) { this.framework = str; }
  public void setDate(String str) { this.date = str; }
  public void setAuthors(String[] strs) { this.authors = strs; }
   
}
