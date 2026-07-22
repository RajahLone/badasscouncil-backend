package fr.triplea.badasscouncil.dto;

public class UserTransfer
{
  
  private String createdOn;
  private String updatedOn;
  private int userId;

  private String status;

  private String role;
  private String loginName;
  private String password;
  private String lastActivityOn;
  private int sessionTimeout;

  private String subscribeMotive;
  
  private String nickName;
  private String groupName;
  private String firstName;
  private String lastName;

  private boolean displayContactDetails;
  private String address;
  private String zipCode;
  private String town;
  private String country;
  private String phone;
  private String email;
   
  private String answer;
  
  public String getCreatedOn() { return createdOn; }
  public void setCreatedOn(String str) { this.createdOn = str; }
  
  public String getUpdatedOn() { return updatedOn; }
  public void setUpdatedOn(String str) { this.updatedOn = str; }
  
  public int getUserId() { return userId; }
  public void setUserId(int id) { this.userId = id; }
  
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public boolean hasRole() { if (this.role != null) { if (!(this.role.isBlank())) { return true; }} return false; }

  public String getStatus() { return status; }
  public void setStatus(String str) { this.status = str; }

  
  public String getLoginName() { return loginName; }
  public void setLoginName(String str) { this.loginName = str; }
  
  public String getPassword() { return password; }
  public void setPassword(String str) { this.password = str; }
  
  public String getLastActivityOn() { return lastActivityOn; }
  public void setLastActivityOn(String str) { this.lastActivityOn = str; }

  public int getSessionTimeout() { return sessionTimeout; }
  public void setSessionTimeout(int timeout) { this.sessionTimeout = timeout; }

  
  public String getSubscribeMotive() { return subscribeMotive; }
  public void setSubscribeMotive(String str) { this.subscribeMotive = str; }
  
  
  public String getNickName() { return nickName; }
  public void setNickName(String str) { this.nickName = str; }
  
  public String getGroupName() { return groupName; }
  public void setGroupName(String str) { this.groupName = str; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String str) { this.firstName = str; }

  public String getLastName() { return lastName; }
  public void setLastName(String str) { this.lastName = str; }
  
  
  public boolean getDisplayContactDetails() { return displayContactDetails; }
  public boolean mustDisplayContactDetails() { return displayContactDetails; }
  public void setDisplayContactDetails(boolean b) { this.displayContactDetails = b; }
   
  public String getAddress() { return address; }
  public void setAddress(String str) { this.address = str; }
  
  public String getZipCode() { return zipCode; }
  public void setZipCode(String str) { this.zipCode = str; }
  
  public String getTown() { return town; }
  public void setTown(String str) { this.town = str; }
  
  public String getCountry() { return country; }
  public void setCountry(String str) { this.country = str; }
  
  public String getPhone() { return phone; }
  public void setPhone(String str) { this.phone = str; }
  
  public String getEmail() { return email; }
  public void setEmail(String str) { this.email = str; }
  
  public String getAnswer() { return answer; }
  public void setAnswer(String str) { this.answer = str; }
 
}
