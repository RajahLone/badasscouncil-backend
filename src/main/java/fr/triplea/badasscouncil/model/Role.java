package fr.triplea.badasscouncil.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "badasscouncil.roles")
@Table(name = "roles")
public class Role
{
  
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM-dd-yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime createdOn;
  
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM-dd-yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime updatedOn;
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id", nullable = false)
  private Integer roleId;
  
  private Boolean enabled;
  
  @Column(length = 64, nullable = false)
  private String label;

  @ManyToMany(mappedBy = "roles")
  private List<User> users;


  public Role() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
  
  public void setCreatedOn(LocalDateTime d) { this.createdOn = d; }
  public void setCreatedOn(String s) { this.createdOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getCreatedOn() { return this.createdOn; }
  
  public void setUpdatedOn(LocalDateTime d) { this.updatedOn = d; }
  public void setUpdatedOn(String s) { this.updatedOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getUpdatedOn() { return this.updatedOn; }
  
  public void setRoleId(Integer id) { this.roleId = id; }
  public Integer getRoleId() { return this.roleId; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  public void setLibelle(String str) { if (str != null) { this.label = StringUtils.truncate(str, 64); } }
  public String getLibelle() { return this.label; }
  @Transient
  public boolean isRole(String s) { if (this.label != null) { if (this.label.equals("ROLE_" + s)) { return true; } } return false; }
 
  public List<User> getParticipants() { return users; }
  public void setUsers(final List<User> users) { this.users = users; }

  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getLibelle() == null) ? 0 : getLibelle().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Role r = (Role) obj;
    if (getLibelle() == null) { if (r.getLibelle() == null) { return false; } } else if (!getLibelle().equals(r.getLibelle())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Role [id=").append(roleId)
           .append(", label=").append(label)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
           .append(enabled ? "" : ", inactif")
           .append("]");

    return builder.toString();
  }

}
