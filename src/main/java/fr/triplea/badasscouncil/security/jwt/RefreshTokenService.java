package fr.triplea.badasscouncil.security.jwt;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dao.RefreshTokenRepository;
import fr.triplea.badasscouncil.model.RefreshToken;
import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService 
{

  @Value("${jwttoken.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository participantRepository;

  public RefreshToken findByToken(String token) { return refreshTokenRepository.findByToken(token); }

  public RefreshToken createRefreshToken(Integer userId) 
  {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(participantRepository.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.saveAndFlush(refreshToken);
    
    return refreshToken;
  }

  public RefreshToken verifyExpiration(RefreshToken token) { if (token.getExpiryDate().compareTo(Instant.now()) < 0) { refreshTokenRepository.delete(token); return null; } return token; }

  @Transactional
  public int deleteByNumeroParticipant(Integer numeroParticipant) { return refreshTokenRepository.deleteByUserId(numeroParticipant); }
  
}