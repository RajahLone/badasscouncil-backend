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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "badasscouncil.variables")
@Table(name = "variables")
public class Variable
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
  @Column(name = "var_id", nullable = false)
  private Integer variableId;
  
  @Column(length = 64, nullable = false)
  private String family;

  @Column(length = 64, nullable = false)
  private String code;

  @Column(length = 4000)
  private String content;

  @Column(length = 4000)
  private String notes;
  
  
  public Variable() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
  
  public void setCreatedOn(LocalDateTime d) { this.createdOn = d; }
  public void setCreatedOn(String s) { this.createdOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getCreatedOn() { return this.createdOn; }
  
  public void setUpdatedOn(LocalDateTime d) { this.updatedOn = d; }
  public void setUpdatedOn(String s) { this.updatedOn = LocalDateTime.parse(s, df); }
  public LocalDateTime getUpdatedOn() { return this.updatedOn; }
  
  public void setVariableId(Integer id) { this.variableId = id; }
  public Integer getVariableId() { return this.variableId; }
  
  public void setFamily(String str) { if (str != null) { this.family = StringUtils.truncate(str, 64); } }
  public String getFamily() { return this.family; }
  public boolean hasFamily() { if (this.family == null) { return false; } if (this.family.isBlank()) { return false; } return true; }
  
  public void setCode(String str) { if (str != null) { this.code = StringUtils.truncate(str, 64); } }
  public String getCode() { return this.code; }
  public boolean hasCode() { if (this.code == null) { return false; } if (this.code.isBlank()) { return false; } return true; }
  
  public void setContent(String str) { if (str != null) { this.content = StringUtils.truncate(str, 4000); } }
  public String getContent() { return this.content; }
  
  public void setNotes(String str) { if (str != null) { this.notes = StringUtils.truncate(str, 4000); } }
  public String getNotes() { return this.notes; }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getFamily() == null) ? 0 : getFamily().hashCode());
    result = (prime * result) + ((getCode() == null) ? 0 : getCode().hashCode());
    result = (prime * result) + ((getContent() == null) ? 0 : getContent().hashCode());
    result = (prime * result) + ((getNotes() == null) ? 0 : getNotes().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Variable v = (Variable) obj;
    if (getFamily() == null) { if (v.getFamily() == null) { return false; } } else if (!getFamily().equals(v.getFamily())) { return false; }
    if (getCode() == null) { if (v.getCode() == null) { return false; } } else if (!getCode().equals(v.getCode())) { return false; }
    if (getContent() == null) { if (v.getContent() == null) { return false; } } else if (!getContent().equals(v.getContent())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Variable [id=").append(variableId)
           .append(", family=").append(family)
           .append(", code=").append(code)
           .append(", content=").append(content)
           .append(", notes=").append(notes)
           .append(", created=").append(createdOn)
           .append(", updated=").append(updatedOn)
          .append("]");

    return builder.toString();
  }

}
