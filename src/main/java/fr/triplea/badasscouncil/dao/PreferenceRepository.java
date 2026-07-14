package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Preference;

public interface PreferenceRepository extends JpaRepository<Preference, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM vote.preferences AS p WHERE p.numero_preference = :id ")
  Preference findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT p.* FROM vote.preferences AS p WHERE p.numero_participant = :participant AND p.numero_traitement = :traitement ")
  List<Preference> findByParticipantAndTraitement(@Param("participant") User participant, @Param("numTtt") int traitement);

}
