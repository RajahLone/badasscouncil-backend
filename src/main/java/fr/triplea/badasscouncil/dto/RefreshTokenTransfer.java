package fr.triplea.badasscouncil.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenTransfer 
{

  private String accessToken;

  @NotBlank
  private String refreshToken;

  public void setAccessToken(String s) { this.accessToken = new String(s); }
  public String getAccessToken() { return this.accessToken; }

  public void setRefreshToken(String s) { this.refreshToken = new String(s); }
  public String getRefreshToken() { return this.refreshToken; }
  
}
