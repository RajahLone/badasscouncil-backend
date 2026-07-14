package fr.triplea.badasscouncil.model;


import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.Instant;

import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity(name = "badasscouncil.refreshtoken")
@Table(name = "refreshtoken")
public class RefreshToken 
{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  @Column(nullable = false, unique = true, length = 4000)
  private String token;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Instant expiryDate;

  
  public void setId(Integer id) { this.id = id; }
  public Integer getId() { return this.id; }
  
  public void setUser(User u) { this.user = u; }
  public User getUser() { return this.user; }
  
  public void setToken(String str) { if (str != null) { this.token = StringUtils.truncate(str, 4000); } }
  public String getToken() { return this.token; }
 
  public void setExpiryDate(Instant d) { this.expiryDate = d; }
  public Instant getExpiryDate() { return this.expiryDate; }

}
