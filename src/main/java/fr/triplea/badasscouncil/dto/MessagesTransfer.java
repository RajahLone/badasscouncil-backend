package fr.triplea.badasscouncil.dto;

public class MessagesTransfer
{

  String erreur;
  String alerte;
  String information;
  String autre;
  
  public MessagesTransfer() {}

  public void setError(String erreur) { if (erreur != null) { if (!(erreur.isBlank())) { this.erreur = erreur; } } }
  public String getErreur() { return this.erreur; }

  public void setAlerte(String alerte) { if (alerte != null) { if (!(alerte.isBlank())) { this.alerte = alerte; } } }
  public String getAlerte() { return this.alerte; }

  public void setInformation(String information) { if (information != null) { if (!(information.isBlank())) { this.information = information; } } }
  public String getInformation() { return this.information; }

  public void setMiscellaneous(String autre) { if (autre != null) { if (!(autre.isBlank())) { this.autre = autre; } } }
  public String getAutre() { return this.autre; }

}
