package fr.triplea.badasscouncil.dto;

public class HomeInformationTransfer
{

  String error;
  String alert;
  String info;
  String other;
  
  public HomeInformationTransfer() {}

  public void setError(String str) { if (str != null) { if (!(str.isBlank())) { this.error = str; } } }
  public String getError() { return this.error; }

  public void setAlert(String str) { if (str != null) { if (!(str.isBlank())) { this.alert = str; } } }
  public String getAlert() { return this.alert; }

  public void setInfo(String str) { if (str != null) { if (!(str.isBlank())) { this.info = str; } } }
  public String getInfo() { return this.info; }

  public void setOther(String str) { if (str != null) { if (!(str.isBlank())) { this.other = str; } } }
  public String getOther() { return this.other; }

}
