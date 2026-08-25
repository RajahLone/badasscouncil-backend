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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "badasscouncil.rooms")
@Table(name = "rooms")
public class Room
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
  @Column(name = "room_id", nullable = false)
  private Integer roomId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="user_id", referencedColumnName="user_id")
  private User user;
  
  private Boolean enabled;
  
  
  @Enumerated(EnumType.STRING) 
  private RoomState state;
 
  
  @Column(length = 128, nullable = false)
  private String name;

  @Column(length = 256)
  private String passwordHash;

  @Column(length = 512)
  private String topic;

  @Column(length = 4000)
  private String notes;


  @Enumerated(EnumType.STRING) 
  private RoomPurgeType purgeType;

  private Integer messagesLimit = 1000;

  private Integer timeDuration = 4321;
  
  public Room() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
  
  public void setCreatedOn(LocalDateTime d) { this.createdOn = d; }
  public void setCreatedOn(String s) { this.createdOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getCreatedOn() { return this.createdOn; }
  @Transient
  public boolean hasCreatedOn() { return (this.createdOn != null); }
  
  public void setUpdatedOn(LocalDateTime d) { this.updatedOn = d; }
  public void setUpdatedOn(String s) { this.updatedOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getUpdatedOn() { return this.updatedOn; }
  @Transient
  public boolean hasUpdatedOn() { return (this.updatedOn != null); }
  
  public void setRoomId(Integer id) { this.roomId = id; }
  public Integer getRoomId() { return this.roomId; }
  
  public void setUser(final User user) { this.user = user; }
  public User getUser() { return user; }

  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  
  public void setState(RoomState enu) { this.state = enu; }
  public RoomState getState() { return this.state; }
 
  
  public void setName(String str) { if (str != null) { this.name = StringUtils.truncate(str, 128); } }
  public String getName() { return this.name; }
  
  public void setPasswordHash(String str) { if (str != null) { this.passwordHash = StringUtils.truncate(str, 256); } }
  public String getPasswordHash() { return this.passwordHash; }
  @Transient
  public boolean hasPassword() { if (this.passwordHash == null) { return false; } return (this.passwordHash.length() > 0); }

  public void setTopic(String str) { if (str != null) { this.topic = StringUtils.truncate(str, 512); } }
  public String getTopic() { return this.topic; }
  @Transient
  public boolean hasTopic() { if (this.topic == null) { return false; } return (this.topic.length() > 0); }
  
  public void setNotes(String str) { if (str != null) { this.notes = StringUtils.truncate(str, 4000); } }
  public String getNotes() { return this.notes; }
  

  public void setPurgeType(RoomPurgeType enu) { this.purgeType = enu; }
  public RoomPurgeType getPurgeType() { return this.purgeType; }

  public void setMessagesLimit(int max) { this.messagesLimit = Integer.valueOf(max); }
  public Integer getMessagesLimit() { return this.messagesLimit; }

  public void setTimeDuration(int max) { this.timeDuration = Integer.valueOf(max); }
  public Integer getTimeDuration() { return this.timeDuration; }

  
  
  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getName() == null) ? 0 : getName().hashCode());
    result = (prime * result) + ((getTopic() == null) ? 0 : getTopic().hashCode());
    result = (prime * result) + ((getNotes() == null) ? 0 : getNotes().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Room r = (Room) obj;
    if (getName() == null) { if (r.getName() == null) { return false; } } else if (!getName().equals(r.getName())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Room [id=").append(roomId)
           .append(", name=").append(name)
           .append(", topic=").append(topic)
           .append(", notes=").append(notes)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
           .append(enabled ? "" : ", disabled")
           .append("]");

    return builder.toString();
  }

}
