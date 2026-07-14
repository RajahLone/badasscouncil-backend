package fr.triplea.badasscouncil.dto;

import java.util.Base64;

public class ProductionChoice 
{
  
  Integer numeroProduction;
  String type;
  String titre;
  String auteurs;
  String groupes;
  String plateforme;
  Integer numeroOrdre;
  byte[] vignette;
  
  public ProductionChoice(Integer numeroProduction, String type, String titre, String auteurs, String groupes, String plateforme, Integer numeroOrdre, byte[] vignette)
  {
    this.numeroProduction = numeroProduction;
    this.type = type;
    this.titre = titre;
    this.auteurs = auteurs;
    this.groupes = groupes;
    this.plateforme = plateforme;
    this.numeroOrdre = numeroOrdre;
    this.vignette = vignette;
  }

  public void setNumeroProduction(Integer numeroProduction) { this.numeroProduction = numeroProduction; }
  public Integer getNumeroProduction() { return numeroProduction; }

  public void setType(String type) { this.type = type; }
  public String getType() { return type; }

  public void setTitre(String titre) { this.titre = titre; }
  public String getTitre() { return titre; }

  public void setAuteurs(String auteurs) { this.auteurs = auteurs; }
  public String getAuteurs() { return auteurs; }

  public void setGroupes(String groupes) { this.groupes = groupes; }
  public String getGroupes() { return groupes; }

  public void setPlateforme(String plateforme) { this.plateforme = plateforme; }
  public String getPlateforme() { return plateforme; }

  public void setNumeroOrdre(Integer numeroOrdre) { this.numeroOrdre = numeroOrdre; }
  public Integer getNumeroOrdre() { return numeroOrdre; }

  public void setVignette(byte[] vignette) { this.vignette = vignette; }
  public String getVignette() { if (this.vignette == null) { return ""; } return "data:image/png;base64," + Base64.getEncoder().encodeToString(this.vignette); }
  
}
