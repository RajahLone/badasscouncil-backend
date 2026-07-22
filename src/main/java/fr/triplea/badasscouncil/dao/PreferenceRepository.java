package fr.triplea.badasscouncil.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.model.Preference;

public interface PreferenceRepository extends JpaRepository<Preference, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT p.* FROM badasscouncil.preferences AS p WHERE p.preference_id = :id ")
  Preference findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT p.* FROM badasscouncil.preferences AS p WHERE p.user_id = :user AND p.action_id = :action ")
  Preference findByUserAndAction(@Param("user") int user, @Param("action") int action);

}
