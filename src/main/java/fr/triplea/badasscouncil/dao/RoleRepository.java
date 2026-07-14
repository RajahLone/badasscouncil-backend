package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.model.Role;


public interface RoleRepository extends JpaRepository<Role, Integer> 
{

  @NativeQuery("SELECT DISTINCT r.* FROM vote.roles AS r WHERE r.numero_role = :id ")
  Role findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT r.* FROM vote.roles AS r WHERE r.libelle = :libelle ")
  Role findByLibelle(@Param("libelle") String libelle);
  
  @NativeQuery("SELECT DISTINCT r.* FROM vote.roles AS r ")
  List<Role> findAll();
  
  @Override
  void delete(Role role);

}
