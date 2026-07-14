package fr.triplea.badasscouncil.dto;

public class WebcamTransfer
{

  Integer id = 0;
  
  Long crc32 = 0l;
  
  String vue = null;


  public void setId(Integer id) { this.id = id; }
  public Integer getId() { return id; }


  public void setCrc32(Long crc32) { this.crc32 = crc32; }
  public Long getCrc32() { return crc32; }


  public void setVue(String vue) { this.vue = new String(vue); }
  public String getVue() { return vue; }
  
  @Override
  public String toString() { return "WebcamTransfer [id=" + id + ", crc32=" + crc32 + "]"; }
  
}
