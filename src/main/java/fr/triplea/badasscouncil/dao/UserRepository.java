package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.UserList;
import fr.triplea.badasscouncil.dto.UserOptionList;
import fr.triplea.badasscouncil.dto.NickNameOptionList;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.model.Role;

public interface UserRepository extends JpaRepository<User, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS u WHERE u.user_id = :id AND u.enabled IS TRUE ")
  User findById(@Param("id") int id);
  
  @NativeQuery("SELECT DISTINCT COUNT(u.*) AS nombre FROM badasscouncil.users AS u WHERE u.enabled IS TRUE ")
  long count();  
  
  @NativeQuery("SELECT DISTINCT COUNT(u.*) AS nombre FROM badasscouncil.users AS u WHERE u.enabled IS TRUE AND UPPER(u.nick_name) = :nick AND UPPER(u.group_name) = :group ")
  long countForNickGroup(@Param("nick") String nick, @Param("group") String group);
  
  @NativeQuery("SELECT DISTINCT COUNT(u.*) AS nombre FROM badasscouncil.users AS u WHERE u.enabled IS TRUE AND UPPER(u.login_name) = :login")
  long count(@Param("login") String login);
  
  @NativeQuery("SELECT DISTINCT "
      + "COUNT(u.*) AS nombre "
      + "FROM badasscouncil.users AS u "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status IS NULL) OR (u.status = (:status)::badasscouncil.user_status)) ")
  Integer countForNameStatus(@Param("name") String name, @Param("status") String status);
  
  @NativeQuery("SELECT DISTINCT "
      + "u.user_id, "
      + "u.status, "
      + "u.nick_name, "
      + "u.group_name, "
      + "u.first_name, "
      + "u.last_name, "
      + "CASE WHEN u.display_contact_details = true THEN u.email ELSE '' END AS email  "
      + "FROM badasscouncil.users AS u "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status IS NULL) OR (u.status = (:status)::badasscouncil.user_status)) "
      + "ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC "
      + "LIMIT :limit OFFSET :start ")
  List<UserList> getPageOrderedByName(@Param("name") String name, @Param("status") String status, @Param("start") int start, @Param("limit") Integer limit);
  
  @NativeQuery("SELECT DISTINCT "
      + "u.user_id, "
      + "u.status, "
      + "u.nick_name, "
      + "u.group_name, "
      + "u.first_name, "
      + "u.last_name, "
      + "CASE WHEN u.display_contact_details = true THEN u.email ELSE '' END AS email  "
      + "FROM badasscouncil.users AS u "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status IS NULL) OR (u.status = (:status)::badasscouncil.user_status)) "
      + "ORDER BY u.nick_name DESC, u.group_name DESC, u.first_name DESC, u.last_name DESC "
      + "LIMIT :limit OFFSET :start ")
  List<UserList> getPageOrderedByNameInverted(@Param("name") String name, @Param("status") String status, @Param("start") int start, @Param("limit") Integer limit);
  
  @NativeQuery("SELECT DISTINCT "
      + "u.user_id, "
      + "u.status, "
      + "u.nick_name, "
      + "u.group_name, "
      + "u.first_name, "
      + "u.last_name, "
      + "CASE WHEN u.display_contact_details = true THEN u.email ELSE '' END AS email  "
      + "FROM badasscouncil.users AS u "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status IS NULL) OR (u.status = (:status)::badasscouncil.user_status)) "
      + "ORDER BY u.user_id ASC "
      + "LIMIT :limit OFFSET :start ")
  List<UserList> getPageOrderedBySubscriptionDate(@Param("name") String name, @Param("status") String status, @Param("start") int start, @Param("limit") Integer limit);
  
  @NativeQuery("SELECT DISTINCT "
      + "u.user_id, "
      + "u.status, "
      + "u.nick_name, "
      + "u.group_name, "
      + "u.first_name, "
      + "u.last_name, "
      + "CASE WHEN u.display_contact_details = true THEN u.email ELSE '' END AS email  "
      + "FROM badasscouncil.users AS u "
      + "WHERE u.enabled IS TRUE "
      + "AND ((:name IS NULL) OR (UPPER(u.nick_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.group_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.first_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.last_name) LIKE CONCAT('%', :name, '%')) OR (UPPER(u.email) LIKE CONCAT('%', :name, '%'))) "
      + "AND ((:status IS NULL) OR (u.status = (:status)::badasscouncil.user_status)) "
      + "ORDER BY u.user_id DESC "
      + "LIMIT :limit OFFSET :start ")
  List<UserList> getPageOrderedBySubscriptionDateInverted(@Param("name") String name, @Param("status") String status, @Param("start") int start, @Param("limit") Integer limit);

  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS u WHERE u.enabled IS TRUE ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC ")
  List<User> findAll();
  
  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users_roles AS ru INNER JOIN badasscouncil.users AS u ON ru.user_id = u.user_id INNER JOIN badasscouncil.roles AS r ON ru.numero_role = r.numero_role WHERE u.enabled IS TRUE AND r.enabled IS TRUE AND ru.numero_role = :role ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC ")
  List<User> findByRole(@Param("role") Role role);

  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS u WHERE u.enabled IS TRUE AND u.login_name = :login ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC ")
  User findByLoginName(@Param("login") String login_name);
  
  @NativeQuery("SELECT DISTINCT u.* FROM badasscouncil.users AS u WHERE u.status = :status AND u.enabled IS TRUE ORDER BY u.nick_name ASC, u.group_name ASC, u.first_name ASC, u.last_name ASC ")
  List<User> findByStatus(@Param("status") String status);

  @NativeQuery("SELECT DISTINCT u.user_id, u.nick_name, u.group_name, u.first_name, u.last_name FROM badasscouncil.users AS u WHERE u.enabled IS TRUE ORDER BY u.nick_name ASC, u.group_name ASC ")
  List<UserOptionList> getUserOptionList();

  @NativeQuery("SELECT DISTINCT u.user_id, u.nick_name, u.group_name FROM badasscouncil.users AS u WHERE (u.enabled IS TRUE) AND (u.user_id <> :id) AND (LENGTH(u.nick_name) > 0) ORDER BY u.nick_name ASC ")
  List<NickNameOptionList> getNickNameOptionList(@Param("id") int id);


  @Modifying
  @NativeQuery("UPDATE badasscouncil.users SET updated_on = NOW(), status = 'ACTIVE'::badasscouncil.user_status WHERE user_id IN :ids ")
  void activate(@Param("ids") List<Integer> usersIds);

  
  @Modifying
  @NativeQuery("UPDATE badasscouncil.users SET updated_on = NOW(), status = 'SLEEPING'::badasscouncil.user_status WHERE user_id IN (SELECT DISTINCT u.user_id FROM badasscouncil.users AS u WHERE u.enabled IS TRUE AND (u.last_activity_on::timestamp + CONCAT('', :months, ' months')::interval) < NOW()) ")
  void setSleepingStatus(@Param("months") int months);

  
  @Override
  void delete(User participant);
  
  @NativeQuery("SELECT DISTINCT u.user_id FROM badasscouncil.users AS u WHERE u.enabled IS FALSE ")
  List<Integer> findDisabled();
  
  @NativeQuery("DELETE FROM badasscouncil.users AS u WHERE u.user_id = :id AND u.enabled IS FALSE ")
  void deleteById(@Param("id") int id);

}
