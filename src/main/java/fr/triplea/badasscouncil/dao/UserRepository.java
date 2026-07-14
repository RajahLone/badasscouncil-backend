package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.UserList;
import fr.triplea.badasscouncil.dto.UserOptionList;
import fr.triplea.badasscouncil.dto.NickNameOptionList;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Role;

public interface UserRepository extends JpaRepository<User, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS p WHERE u.user_id = :id AND u.enabled IS TRUE ")
  User findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT "
      + "COUNT(u.*) AS nombre "
      + "FROM badasscouncil.users AS p "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status = 0) OR (:status = 1 AND u.status = 'LOCKED'::badasscouncil.statut_participant)) ")
  Integer count(@Param("name") String name, @Param("status") int status);
  
  @NativeQuery("SELECT DISTINCT "
      + "u.user_id, "
      + "u.status, "
      + "u.nick_name, "
      + "u.group_name, "
      + "u.first_name, "
      + "u.last_name, "
      + "u.email "
      + "FROM badasscouncil.users AS p "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status = 0) OR (:status = 1 AND u.statut = 'LOCKED'::badasscouncil.statut_participant)) "
      + "ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC "
      + "LIMIT :limit OFFSET :start ")
  List<UserList> getPageOrderedByNom(@Param("name") String name, @Param("status") int status, @Param("start") int start, @Param("limit") Integer limit);
  
  @NativeQuery("SELECT DISTINCT "
      + "u.user_id, "
      + "u.nom, "
      + "u.prenom, "
      + "u.nick_name, "
      + "u.groupe, "
      + "u.email, "
      + "u.statut, "
      + "u.flag_jour1, "
      + "u.flag_jour2, "
      + "u.flag_jour3, "
      + "u.flag_dodo_sur_place, "
      + "u.flag_arrive "
      + "FROM badasscouncil.users AS p "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status = 0) OR (:status = 1 AND u.statut = 'LOCKED'::badasscouncil.statut_participant)) "
      + "ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC "
      + "LIMIT :limit OFFSET :start ")
  List<UserList> getPageOrderedByDateInscription(@Param("name") String name, @Param("status") int status, @Param("start") int start, @Param("limit") Integer limit);

  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS p WHERE u.enabled IS TRUE ORDER BY u.nom ASC, u.prenom ASC, u.nick_name ASC ")
  List<User> findAll();
  
  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users_roles AS rp INNER JOIN badasscouncil.users AS p ON ru.user_id = u.user_id INNER JOIN badasscouncil.roles AS r ON ru.numero_role = r.numero_role WHERE u.enabled IS TRUE AND r.enabled IS TRUE AND ru.numero_role = :role ORDER BY u.nom ASC, u.prenom ASC, u.nick_name ASC ")
  List<User> findByRole(@Param("role") Role role);

  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS p WHERE u.enabled IS TRUE AND u.login_name = :login ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC ")
  User findByLoginName(@Param("login") String login_name);
  
  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS p WHERE u.status = :status AND u.enabled IS TRUE ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC ")
  List<User> findByStatus(@Param("status") String status);
 
  @Override
  void delete(User participant);

  @NativeQuery("SELECT DISTINCT u.user_id, u.nick_name, u.group_name FROM badasscouncil.users AS p WHERE u.enabled IS TRUE ORDER BY u.nick_name ASC, u.group_name ASC ")
  List<UserOptionList> getUserOptionList();

  @NativeQuery("SELECT DISTINCT u.user_id, u.nick_name FROM badasscouncil.users AS p WHERE (u.enabled IS TRUE) AND (u.user_id <> :id) AND (LENGTH(u.nick_name) > 0) ORDER BY u.nick_name ASC ")
  List<NickNameOptionList> getNickNameOptionList(@Param("id") int id);


}
