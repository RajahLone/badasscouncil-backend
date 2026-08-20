package fr.triplea.badasscouncil.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity(name = "badasscouncil.messages")
@Table(name = "messages")
public class Message
{
   
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy' HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime createdOn;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="room_id", referencedColumnName="room_id")
  private Room room;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_id", nullable = false)
  private Integer messageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="user_id", referencedColumnName="user_id")
  private User user;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="dest_id", referencedColumnName="user_id")
  private User dest;

  @Column(length = 4000, nullable = false)
  private String content;

  
  public Message() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
  
  public void setDateCreation(LocalDateTime d) { this.createdOn = d; }
  public void setDateCreation(String s) { this.createdOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.createdOn; }
  
  public void setRoom(Room r) { this.room = r; }
  public Room getRoom() { return this.room; }
  
  public void setMessageId(Integer id) { this.messageId = id; }
  public Integer getMessageId() { return this.messageId; }
  
  public void setUser(User p) { this.user = p; }
  public User getUser() { return this.user; }

  public void setDest(User d) { this.dest = d; }
  public User getDest() { return this.dest; }

  public void setContent(String str) { if (str != null) { this.content = StringUtils.truncate(str, 4000); } }
  public String getContent() { return this.content; }

  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getMessageId() == null) ? 0 : getMessageId().hashCode());
    result = (prime * result) + ((getUser() == null) ? 0 : getUser().hashCode());
    result = (prime * result) + ((getDest() == null) ? 0 : getDest().hashCode());
    result = (prime * result) + ((getContent() == null) ? 0 : getContent().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Message m = (Message) obj;
    if (getMessageId() == null) { if (m.getMessageId() == null) { return false; } } else if (!getMessageId().equals(m.getMessageId())) { return false; }
    if (getUser() == null) { if (m.getUser() == null) { return false; } } else if (!getUser().equals(m.getUser())) { return false; }
    if (getDest() == null) { if (m.getDest() == null) { return false; } } else if (!getDest().equals(m.getDest())) { return false; }
    if (getContent() == null) { if (m.getContent() == null) { return false; } } else if (!getContent().equals(m.getContent())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Message [id=").append(messageId)
           .append(", room=").append(room)
           .append(", user=").append(user)
           .append(", dest=").append(dest)
           .append(", content=").append(content)
           .append(", created=").append(createdOn)
           .append("]");

    return builder.toString();
  }

}
