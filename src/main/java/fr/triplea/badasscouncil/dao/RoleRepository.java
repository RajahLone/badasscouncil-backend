package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.model.Role;


public interface RoleRepository extends JpaRepository<Role, Integer> 
{

  @NativeQuery("SELECT DISTINCT r.* FROM badasscouncil.roles AS r WHERE r.roleId = :id ")
  Role findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT r.* FROM badasscouncil.roles AS r WHERE r.label = :label ")
  Role findByLabel(@Param("label") String label);
  
  @NativeQuery("SELECT DISTINCT r.* FROM badasscouncil.roles AS r ")
  List<Role> findAll();
  
  @Override
  void delete(Role role);

}
