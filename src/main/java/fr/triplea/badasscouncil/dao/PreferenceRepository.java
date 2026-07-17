package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Preference;

public interface PreferenceRepository extends JpaRepository<Preference, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM badasscouncil.preferences AS p WHERE p.preferenceId = :id ")
  Preference findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT p.* FROM badasscouncil.preferences AS p WHERE p.userId = :user AND p.action_id = :action ")
  List<Preference> findByParticipantAndTraitement(@Param("user") User user, @Param("action") int action);

}
