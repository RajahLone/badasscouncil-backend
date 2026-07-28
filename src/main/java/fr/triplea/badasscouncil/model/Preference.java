package fr.triplea.badasscouncil.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "badasscouncil.preferences")
@Table(name = "preferences")
public class Preference
{
/*
    created_on timestamp without time zone NOT NULL DEFAULT now(),
    updated_on timestamp without time zone,
    preference_id integer NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id integer NOT NULL,
    action_id integer NOT NULL,
    parameters character varying(4000) COLLATE pg_catalog."default",
*/  

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
  @Column(name = "preference_id", nullable = false)
  private Integer preferenceId;

  @ManyToOne
  @JoinColumn(name="user_id", referencedColumnName="user_id")
  private User user;
  
  private Integer actionId;

  @Column(length = 4000, nullable = false)
  private String parameters;
  
  
  public Preference() { super(); }
  
  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.createdOn = d; }
  public void setDateCreation(String s) { this.createdOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.createdOn; }
  
  public void setDateModification(LocalDateTime d) { this.updatedOn = d; }
  public void setDateModification(String s) { this.updatedOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.updatedOn; }
  
  public void setPreferenceId(Integer id) { this.preferenceId = id; }
  public Integer getPreferenceId() { return this.preferenceId; }
  
  public void setUser(User u) { this.user = u; }
  public User getUser() { return this.user; }
  
  public static final int FIRST_ACTION     = 1;
  public static final int USERS_PAGE_SIZE  = 1;
  public static final int FILES_PER_MEMBER = 2;
  public static final int USERS_FILTERS    = 3;
  public static final int FILES_FILTERS    = 4;
  public static final int LAST_ACTION      = 4;
  
  public void setActionId(int a) { this.actionId = Integer.valueOf(a); }
  public Integer getActionId() { return this.actionId; }
  
  public void setParameters(String str) { if (str != null) { this.parameters = StringUtils.truncate(str, 4000); } }
  public String getParameters() { return this.parameters; }
  @Transient
  public int getInteger() { int v = 0; try { v = Integer.parseInt(getParameters()); } catch(Exception e) { v = 0; } return v; }

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getPreferenceId() == null) ? 0 : getPreferenceId().hashCode());
    result = (prime * result) + ((getUser() == null) ? 0 : getUser().hashCode());
    result = (prime * result) + ((getActionId() == null) ? 0 : getActionId().hashCode());
    result = (prime * result) + ((getParameters() == null) ? 0 : getParameters().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Preference p = (Preference) obj;
    if (getPreferenceId() == null) { if (p.getPreferenceId() == null) { return false; } } else if (!getPreferenceId().equals(p.getPreferenceId())) { return false; }
    if (getUser() == null) { if (p.getUser() == null) { return false; } } else if (!getUser().equals(p.getUser())) { return false; }
    if (getActionId() == null) { if (p.getActionId() == null) { return false; } } else if (!getActionId().equals(p.getActionId())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Preference [id=").append(preferenceId)
           .append(", user=").append(user)
           .append(", actionId=").append(actionId)
           .append(", parameters=").append(parameters)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
           .append("]");

    return builder.toString();
  }

}
