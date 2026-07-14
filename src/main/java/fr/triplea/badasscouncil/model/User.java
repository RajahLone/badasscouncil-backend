package fr.triplea.badasscouncil.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Sets;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "badasscouncil.users")
@Table(name = "users")
public class User
{

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime createdOn;
  
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime updatedOn;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "users_roles", 
             joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), 
             inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
  private List<Role> roles;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", nullable = false)
  private Integer id;

  private Boolean enabled = true;


  @Enumerated(EnumType.STRING) 
  private UserStatus status;

  @Column(length = 128, unique = true, nullable = false)
  private String loginName;

  @Column(length = 256)
  private String passwordHash;
  
  private Boolean passwordExpired = false;
  
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime expiredOn;
  
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime lastActivityOn;
  
  private Integer sessionTimeout = 15;
  

  @Column(length = 512)
  private String subscribeMotive;

  
  @Column(length = 128, nullable = false)
  private String nickName;
  
  @Column(length = 128)
  private String groupName;

  @Column(length = 128)
  private String firstName;

  @Column(length = 128)
  private String lastName;

  
  private Boolean displayCoordinates = false;
  
  @Column(length = 256)
  private String address;

  @Column(length = 16)
  private String zipCode;

  @Column(length = 128)
  private String town;

  @Column(length = 128)
  private String country;

  @Column(length = 32)
  private String phone;

  @Column(length = 128)
  private String email;

  
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
  private RefreshToken refreshToken;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
  private List<Preference> preferences;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
  private List<Attachment> files;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
  private List<Message> messagesParticpant;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dest")
  private List<Message> messagesDestinataire;

  
  public User() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setCreatedOn(LocalDateTime d) { this.createdOn = d; }
  public void setCreatedOn(String s) { this.createdOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getCreatedOn() { return this.createdOn; }
  public boolean hasCreatedOn() { return (this.createdOn != null); }
  
  public void setUpdatedOn(LocalDateTime d) { this.updatedOn = d; }
  public void setUpdatedOn(String s) { this.updatedOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getUpdatedOn() { return this.updatedOn; }
  public boolean hasUpdatedOn() { return (this.updatedOn != null); }
  
  public List<Role> getRoles() { return roles; }
  public void setRoles(final List<Role> roles) { this.roles = roles; }
  
  public void setId(Integer id) { this.id = id; }
  public Integer getId() { return this.id; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  
  public void setStatus(UserStatus enu) { this.status = enu; }
  public UserStatus getStatus() { return this.status; }

  
  public void setLoginName(String str) { if (str != null) { this.loginName = StringUtils.truncate(str, 128); } }
  public String getLoginName() { return this.loginName; }
  public boolean hasLoginName() { if (this.loginName == null) { return false; } if (this.loginName.isBlank()) { return false; } return true; }  
  
  public void setPasswordHash(String str) { if (str != null) { this.passwordHash = StringUtils.truncate(str, 256); } }
  public String getPasswordHash() { return this.passwordHash; }
   
  public void setPasswordExpired(boolean b) { this.passwordExpired = Boolean.valueOf(b); }
  public Boolean getPasswordExpired() { return this.passwordExpired; }
  @Transient
  public boolean isPasswordExpired() { return (getPasswordExpired().booleanValue()); }
  
  public void setExpiredOn(LocalDateTime d) { this.expiredOn = d; }
  public LocalDateTime getExpiredOn() { return this.expiredOn; }
  
  public void setLastActivityOn(LocalDateTime d) { this.lastActivityOn = d; }
  public LocalDateTime getLastActivityOn() { return this.lastActivityOn; }
  @Transient
  public boolean hasLastActivityOn() { return (this.lastActivityOn != null); }

  public void setSessionTimeout(int n) { this.sessionTimeout = Integer.valueOf(n); }
  public Integer getSessionTimeout() { return this.sessionTimeout; }

  
  public void setSubscribeMotive(String str) { if (str != null) { this.subscribeMotive = StringUtils.truncate(str, 512); } }
  public String getSubscribeMotive() { return this.subscribeMotive; }
  public boolean hasSubscribeMotive() { if (this.subscribeMotive == null) { return false; } if (this.subscribeMotive.isBlank()) { return false; } return true; }
  
  
  public void setNickName(String str) { if (str != null) { this.nickName = StringUtils.truncate(str, 128); } }
  public String getNickName() { return this.nickName; }
  public boolean hasNickName() { if (this.nickName == null) { return false; } if (this.nickName.isBlank()) { return false; } return true; }
  
  public void setGroupName(String str) { if (str != null) { this.groupName = StringUtils.truncate(str, 128); } }
  public String getGroupName() { return this.groupName; }
  
  public void setFirstName(String str) { if (str != null) { this.firstName = StringUtils.truncate(str, 128); } }
  public String getFirstName() { return this.firstName; }
  
  public void setLastName(String str) { if (str != null) { this.lastName = StringUtils.truncate(str, 128); } }
  public String getLastName() { return this.lastName; }

  
  public void setDisplayCoordinates(boolean b) { this.displayCoordinates = Boolean.valueOf(b); }
  public Boolean getDisplayCoordinates() { return this.displayCoordinates; }
  @Transient
  public boolean mustDisplayCoordinates() { return (getPasswordExpired().booleanValue()); }

  public void setAddress(String str) { if (str != null) { this.address = StringUtils.truncate(str, 256); } }
  public String getAddress() { return this.address; }
  
  public void setZipCode(String str) { if (str != null) { this.zipCode = StringUtils.truncate(str, 16); } }
  public String getZipCode() { return this.zipCode; }
  
  public void setTown(String str) { if (str != null) { this.town = StringUtils.truncate(str, 128); } }
  public String getTown() { return this.town; }
  
  public void setCountry(String str) { if (str != null) { this.country = StringUtils.truncate(str, 128); } }
  public String getCountry() { return this.country; }
  
  public void setPhone(String str) { if (str != null) { this.phone = StringUtils.truncate(str, 32); } }
  public String getPhone() { return this.phone; }
  
  public void setEmail(String str) { if (str != null) { this.email = StringUtils.truncate(str, 128); } }
  public String getEmail() { return this.email; }
  
   
   
  
  @Transient
  public boolean hasAnyRoles(String... roles) { return hasAnyRoles(Arrays.asList(roles)); }

  @Transient
  public boolean hasAnyRoles(List<String> roles) 
  {
    Set<String> _roles = this.getRoles().stream().map(Role::getLibelle).collect(Collectors.toSet());
      
    Sets.SetView<String> intersection = Sets.intersection(Sets.newHashSet(roles), _roles);
      
    return !intersection.isEmpty();
  }

  @Transient
  public boolean hasRoles(String... roles) { return hasRoles(Arrays.asList(roles)); }

  @Transient
  public boolean hasRoles(List<String> roles) 
  {
    Set<String> _roles = this.getRoles().stream().map(Role::getLibelle).collect(Collectors.toSet());
      
    return _roles.containsAll(roles);
  }

  
  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getId() == null) ? 0 : getId().hashCode());
    result = (prime * result) + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
    result = (prime * result) + ((getLoginName() == null) ? 0 : getLoginName().hashCode());
    result = (prime * result) + ((getNickName() == null) ? 0 : getNickName().hashCode());
    result = (prime * result) + ((getGroupName() == null) ? 0 : getGroupName().hashCode());
    result = (prime * result) + ((getFirstName() == null) ? 0 : getFirstName().hashCode());
    result = (prime * result) + ((getLastName() == null) ? 0 : getLastName().hashCode());
    result = (prime * result) + ((getAddress() == null) ? 0 : getAddress().hashCode());
    result = (prime * result) + ((getZipCode() == null) ? 0 : getZipCode().hashCode());
    result = (prime * result) + ((getTown() == null) ? 0 : getTown().hashCode());
    result = (prime * result) + ((getCountry() == null) ? 0 : getCountry().hashCode());
    result = (prime * result) + ((getPhone() == null) ? 0 : getPhone().hashCode());
    result = (prime * result) + ((getEmail() == null) ? 0 : getEmail().hashCode());
    result = (prime * result) + ((getStatus() == null) ? 0 : getStatus().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final User u = (User) obj;
    if (getId() == null) { if (u.getId() == null) { return false; } } else if (!getId().equals(u.getId())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("User [id=").append(id)
           .append(", login=").append(loginName)
           .append(", nickname=").append(nickName)
           .append(", groupName=").append(groupName)
           .append(", firstName=").append(firstName)
           .append(", lastName=").append(lastName)
           .append(", address=").append(address)
           .append(", zip=").append(zipCode)
           .append(", town=").append(town)
           .append(", country=").append(country)
           .append(", phone=").append(phone)
           .append(", email=").append(email)
           .append(", status=").append(status)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
           .append(", last=").append(lastActivityOn)
           .append(enabled ? ", roles=" + roles : ", inactif")
           .append("]");

    return builder.toString();
  }

}
