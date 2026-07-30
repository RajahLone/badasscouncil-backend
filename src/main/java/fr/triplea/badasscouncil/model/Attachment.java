package fr.triplea.badasscouncil.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.hypersistence.utils.hibernate.type.basic.Inet;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLInetType;
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

@Entity(name = "badasscouncil.attachments")
@Table(name = "attachments")
public class Attachment
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
  @Column(name = "file_id", nullable = false)
  private Integer fileId;

  private Boolean enabled = true;
  
  @ManyToOne
  @JoinColumn(name="user_id", referencedColumnName="user_id")
  private User user;
  
  @Transient
  private Integer ownerId;
  @Transient
  private String ownerName;

  @Type(PostgreSQLInetType.class)
  @Column(name = "ip_address", columnDefinition = "inet")
  private Inet ipAddress;
  
  private String commentsPublic;

  private String commentsPrivate;

  @Column(length = 1024)
  private String archiveName;

  @Column(length = 1024)
  private String localName;
  
  private Integer versionNumber = 1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="dest_id", referencedColumnName="user_id")
  private User recipient;

  @Transient
  private Integer destId;

  private Boolean shared = false;
  
  private Integer lifeSpan;

  
  public Attachment() { super(); }


  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setCreatedOn(LocalDateTime d) { this.createdOn = d; }
  public void setCreatedOn(String s) { this.createdOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getCreatedOn() { return this.createdOn; }
  
  public void setUpdatedOn(LocalDateTime d) { this.updatedOn = d; }
  public void setUpdatedOn(String s) { this.updatedOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getUpdatedOn() { return this.updatedOn; }
  
  public void setFileId(Integer id) { this.fileId = id; }
  public Integer getFileId() { return this.fileId; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  public void setUser(User u) { this.user = u; }
  public User getUser() { return this.user; }
   
  @Transient
  public void setOwnerId(Integer id) { if (id != null) { this.ownerId = id; } }
  @Transient
  public Integer getOwnerId() { return this.ownerId; }
  
  @Transient
  public void setOwnerName(String str) { if (str != null) { this.ownerName = new String(str); } }
  @Transient
  public String getOwnerName() { return this.ownerName; }

  public void setIpAddress(Inet ip) { this.ipAddress = ip; }
  public void setIpAddress(String ip) { this.ipAddress = new Inet(ip); }
  public String getIpAddress() { return this.ipAddress.getAddress(); }
  
  
  public void setCommentsPublic(String str) { this.commentsPublic = new String(str); }
  public String getCommentsPublic() { return this.commentsPublic; }
 
  public void setCommentsPrivate(String str) { this.commentsPrivate = new String(str); }
  public String getCommentsPrivate() { return this.commentsPrivate; }

  
  public void setArchiveName(String str) { if (str != null) { this.archiveName = StringUtils.truncate(str, 1024); } }
  public String getArchiveName() { return this.archiveName; }
  
  public void setLocalName(String str) { if (str != null) { this.localName = StringUtils.truncate(str, 1024); } }
  public String getLocalName() { return this.localName; }

  public void setVersionNumber(int n) { this.versionNumber = Integer.valueOf(n); }
  public Integer getVersionNumber() { return this.versionNumber; }
  
  
  public void setRecipient(User u) { this.recipient = u; }
  public User getRecipient() { return this.recipient; }
   
  @Transient
  public void setDestId(Integer id) { if (id != null) { this.destId = id; } }
  @Transient
  public Integer getDestId() { return this.destId; }

  
  public void setShared(boolean b) { this.shared = Boolean.valueOf(b); }
  public Boolean getShared() { return this.shared; }
  @Transient
  public boolean isShared() { return (getShared().booleanValue()); }

  public void setLifeSpan(int n) { this.lifeSpan = Integer.valueOf(n); }
  public Integer getLifeSpan() { return this.lifeSpan; }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getFileId() == null) ? 0 : getFileId().hashCode());
    result = (prime * result) + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
    result = (prime * result) + ((getUser() == null) ? 0 : getUser().hashCode());
    result = (prime * result) + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
    result = (prime * result) + ((getCommentsPublic() == null) ? 0 : getCommentsPublic().hashCode());
    result = (prime * result) + ((getCommentsPrivate() == null) ? 0 : getCommentsPrivate().hashCode());
    result = (prime * result) + ((getArchiveName() == null) ? 0 : getArchiveName().hashCode());
    result = (prime * result) + ((getVersionNumber() == null) ? 0 : getVersionNumber().hashCode());
    result = (prime * result) + ((getShared() == null) ? 0 : getShared().hashCode());
    result = (prime * result) + ((getLifeSpan() == null) ? 0 : getLifeSpan().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Attachment f = (Attachment) obj;
    if (getFileId() == null) { if (f.getFileId() == null) { return false; } } else if (!getFileId().equals(f.getFileId())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Attachment [id=").append(fileId)
           .append(", user=").append(user)
           .append(", IP=").append(ipAddress)
           .append(", publicComment=").append(commentsPublic)
           .append(", privateComment=").append(commentsPrivate)
           .append(", archiveName=").append(archiveName)
           .append(", localName=").append(localName)
           .append(", version=").append(versionNumber)
           .append(", lifeSpan=").append(lifeSpan)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
           .append(shared ? "" : ", shared")
           .append(enabled ? "" : ", disabled")
           .append("]");

    return builder.toString();
  }

}
