package fr.triplea.badasscouncil.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity(name = "badasscouncil.quotes")
@Table(name = "quotes")
public class Quote
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
  @Column(name = "quote_id", nullable = false)
  private Integer quoteId;

  @Column(length = 4000)
  private String content;
  
  
  public Quote() { super(); }

  
  public void setCreatedOn(LocalDateTime d) { this.createdOn = d; }
  public LocalDateTime getCreatedOn() { return this.createdOn; }
  
  public void setUpdatedOn(LocalDateTime d) { this.updatedOn = d; }
  public LocalDateTime getUpdatedOn() { return this.updatedOn; }
  
  public void setQuoteId(Integer id) { this.quoteId = id; }
  public Integer getQuoteId() { return this.quoteId; }
  
  public void setContent(String str) { if (str != null) { this.content = StringUtils.truncate(str, 4000); } }
  public String getContent() { return this.content; }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getContent() == null) ? 0 : getContent().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Quote v = (Quote) obj;
    if (getContent() == null) { if (v.getContent() == null) { return false; } } else if (!getContent().equals(v.getContent())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Variable [id=").append(quoteId)
           .append(", content=").append(content)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
          .append("]");

    return builder.toString();
  }

}
