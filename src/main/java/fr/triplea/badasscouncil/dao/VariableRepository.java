package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.VariableFamily;
import fr.triplea.badasscouncil.model.Variable;


public interface VariableRepository extends JpaRepository<Variable, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT v.* FROM badasscouncil.variables AS v ORDER BY v.family ASC, v.code ASC ")
  List<Variable> findAll();
  
  @NativeQuery("SELECT DISTINCT v.* FROM badasscouncil.variables AS v WHERE v.var_id = :id ")
  Variable findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT v.* FROM badasscouncil.variables AS v WHERE ((:family IS NULL) OR (v.family = :family)) ORDER BY v.family ASC, v.code ASC ")
  List<Variable> findByFamily(@Param("family") String family);

  @NativeQuery("SELECT DISTINCT v.content FROM badasscouncil.variables AS v WHERE v.family = :family AND v.code = :code ")
  String findByFamilyAndCode(@Param("family") String family, @Param("code") String code);

  @NativeQuery("SELECT DISTINCT v.family FROM badasscouncil.variables AS v ORDER BY v.family ASC ")
  List<VariableFamily> getFamilies();
  
}
