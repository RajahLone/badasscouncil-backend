package fr.triplea.badasscouncil.dto;

public class Pagination 
{

  /** nombre d'éléments */
  int nombreElements = 0;
  /** nombre d'éléments dans une page */
  int taillePage = 100;
  /** nombre de pages */
  int nombrePages = 1;
  /** index de page, débute à 0 */
  int pageCourante = 0;

  public Pagination(int nombreElements, int taillePage, int nombrePage, int pageCourante) 
  {
    this.nombreElements = nombreElements;
    this.taillePage = taillePage;
    this.nombrePages = nombrePage;
    this.pageCourante = pageCourante;
  }

  public void setNombreElements(int nombreElements) { this.nombreElements = nombreElements; }
  public int getNombreElements() { return nombreElements; }

  public int getTaillePage() { return taillePage; }
  public void setTaillePage(int taillePage) { this.taillePage = taillePage; }
  
  public void setNombrePages(int nombrePage) { this.nombrePages = nombrePage; }
  public int getNombrePages() { return nombrePages; }
  
  public void setPageCourante(int pageCourante) { this.pageCourante = pageCourante; }
  public int getPageCourante() { return pageCourante; }

}
