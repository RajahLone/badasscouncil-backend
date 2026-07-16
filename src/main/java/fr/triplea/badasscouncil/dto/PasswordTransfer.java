package fr.triplea.badasscouncil.dto;

public class PasswordTransfer
{

  private String loginName;
  private String oldPassword;
  private String newPassword;
  private String error;
  private String success;

  public PasswordTransfer() {}

  public void setLoginName(String s) { if (s != null) { if (!(s.isBlank())) { this.loginName = s; } } }
  public String getLoginName() { return this.loginName; }

  public void setOldPassword(String s) { if (s != null) { if (!(s.isBlank())) { this.oldPassword = s; } } }
  public String getOldPassword() { return this.oldPassword; }

  public void setNewPassword(String s) { if (s != null) { if (!(s.isBlank())) { this.newPassword = s; } } }
  public String getNewPassword() { return this.newPassword; }
  
  public void setError(String s) { this.error = s; }
  public String getError() { return this.error; }
  
  public void setSuccess(String s) { this.success = s; }
  public String getSuccess() { return this.success; }

  @Override
  public String toString() 
  { 
    final StringBuilder builder = new StringBuilder();
    
    builder.append("PasswordTransfer [loginName=").append(this.loginName).append("]");
      
    return builder.toString();
  }

}
