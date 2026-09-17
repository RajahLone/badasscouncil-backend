package fr.triplea.badasscouncil.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Entity(name = "badasscouncil.images")
@Table(name = "images")
public class Image
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
  @Column(name = "image_id", nullable = false)
  private Integer imageId;

  private Boolean enabled = true;
  
  @ManyToOne
  @JoinColumn(name="message_id", referencedColumnName="message_id")
  private Message message;
  
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
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="dest_id", referencedColumnName="user_id")
  private User dest;

  @Column(length = 1024)
  private String fileName;

  @Lob @JdbcTypeCode(Types.BINARY)
  @Column(name="thumbnail")
  private byte[] thumbnail;

  @Lob @JdbcTypeCode(Types.BINARY)
  @Column(name="data")
  private byte[] data;

  
  public Image() { super(); }


  
  public void setCreatedOn(LocalDateTime d) { this.createdOn = d; }
  public LocalDateTime getCreatedOn() { return this.createdOn; }
  
  public void setUpdatedOn(LocalDateTime d) { this.updatedOn = d; }
  public LocalDateTime getUpdatedOn() { return this.updatedOn; }
  
  public void setImageId(Integer id) { this.imageId = id; }
  public Integer getImageId() { return this.imageId; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  public void setMessage(Message m) { this.message = m; }
  public Message getMessage() { return this.message; }
  
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

  public void setDest(User d) { this.dest = d; }
  public User getDest() { return this.dest; }
  

  
  public void setFileName(String str) { if (str != null) { this.fileName = StringUtils.truncate(str, 1024); } }
  public String getFileName() { return this.fileName; }

  @Transient
  public void generateThumbnail(byte[] d) 
  { 
    if (d == null) { this.thumbnail = null; return; }
                
    try 
    { 
      ByteArrayInputStream bais = new ByteArrayInputStream(d);
      
      BufferedImage originalImage = ImageIO.read(bais);

      BufferedImage tn = Thumbnails.of(originalImage).crop(Positions.CENTER).size(Math.min(160, originalImage.getWidth()), Math.min(160, originalImage.getHeight())).asBufferedImage();
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      ImageIO.write(tn, "png", baos);
      
      this.thumbnail = baos.toByteArray();
      
      baos.flush();
      bais.close();
    } 
    catch(Exception e) { this.thumbnail = null; }
  }
  public void setThumbnail(byte[] v) { this.thumbnail = (v == null) ? null : v.clone(); }
  public String getThumbnail() { if (this.thumbnail != null) { return "data:image/png;base64," + Base64.getEncoder().encodeToString(this.thumbnail); } return ""; }
  @Transient
  public boolean hasThumbnail() { if (this.thumbnail != null) { return true; } return false; }
   
  public void setData(byte[] d) { this.data = (d == null) ? null : d.clone(); }
  public byte[] getData() { if (this.data != null) { return data; } return null; }
  @Transient
  public boolean hasData() { if (this.data != null) { return true; } return false; }

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getImageId() == null) ? 0 : getImageId().hashCode());
    result = (prime * result) + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
    result = (prime * result) + ((getMessage() == null) ? 0 : getMessage().hashCode());
    result = (prime * result) + ((getUser() == null) ? 0 : getUser().hashCode());
    result = (prime * result) + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
    result = (prime * result) + ((getDest() == null) ? 0 : getDest().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Image f = (Image) obj;
    if (getImageId() == null) { if (f.getImageId() == null) { return false; } } else if (!getImageId().equals(f.getImageId())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Attachment [id=").append(imageId)
           .append(", message=").append(message)
           .append(", user=").append(user)
           .append(", dest=").append(dest)
           .append(", IP=").append(ipAddress)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
           .append(enabled ? "" : ", disabled")
           .append("]");

    return builder.toString();
  }

}
