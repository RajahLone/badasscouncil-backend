package fr.triplea.badasscouncil.dto;


public class UserCredentials
{
  
  private int userId;

  private String loginName;
  
  private String password;
  
  private String nickName;

  private String groupName;
  
  private String role;
  
  private int sessionTimeout;
  
  private String accessToken;
  
  private String refreshToken;

  private String error;

  public UserCredentials() {}
   
  public void setUserId(int n) { this.userId = n; }
  public int getUserId() { return this.userId; }

  public void setLoginName(String s) { this.loginName = new String(s); }
  public String getLoginName() { return this.loginName; }

  public void setPassword(String s) { this.password = new String(s); }
  public String getPassword() { return this.password; }

  public void setNickName(String s) { this.nickName = new String(s); }
  public String getNickName() { return this.nickName; }

  public void setGroupName(String s) { this.groupName = new String(s); }
  public String getGroupName() { return this.groupName; }
  
  public void setRole(String s) { this.role = new String(s); }
  public String getRole() { return this.role; }
  public boolean hasRole() { if (this.role != null) { if (!(this.role.isBlank())) { return true; }} return false; }

  public void setSessionTimeout(int i) { this.sessionTimeout = i; }
  public int getSessionTimeout() { return this.sessionTimeout; }

  public void setAccessToken(String s) { this.accessToken = new String(s); }
  public String getAccessToken() { return this.accessToken; }

  public void setRefreshToken(String s) { this.refreshToken = new String(s); }
  public String getRefreshToken() { return this.refreshToken; }
  
  public void setError(String s) { this.error = new String(s); }
  public String getError() { return this.error; }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
      
    builder.append("UserTransfer [loginName=").append(this.loginName).append(", role=").append(role).append("]");
      
    return builder.toString();
  }
 
}
