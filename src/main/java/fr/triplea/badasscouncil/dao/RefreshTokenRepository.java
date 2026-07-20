package fr.triplea.badasscouncil.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.triplea.badasscouncil.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> 
{
  
  RefreshToken findByToken(String token);

  @Modifying(clearAutomatically = true)
  @NativeQuery("DELETE FROM badasscouncil.refreshtoken AS r WHERE r.user_id = :numero ")
  int deleteByNumeroParticipant(@Param("numero") int numeroParticipant);
  
}
