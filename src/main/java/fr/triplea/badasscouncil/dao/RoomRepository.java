package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.RoomRecord;
import fr.triplea.badasscouncil.model.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT r.* FROM badasscouncil.rooms AS r WHERE r.room_id = :id ")
  Room findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT "
      + "  TO_CHAR(r.created_on, 'MM-DD-YYYY HH24:MI:SS') as created_on, "
      + "  TO_CHAR(r.updated_on, 'MM-DD-YYYY HH24:MI:SS') as updated_on, "
      + "  r.room_id, "
      + "  r.name, "
      + "  r.state, "
      + "  r.user_id AS owner_id, "
      + "  CASE WHEN LENGTH(r.password_hash) > 0 THEN 'needed' ELSE '' END AS password, "
      + "  r.topic, "
      + "  r.notes, "
      + "  r.purge_type, "
      + "  r.messages_limit, "
      + "  r.time_duration, "
      + "  r.listed_users_type "
      + "FROM badasscouncil.rooms AS r "
      + "WHERE r.enabled IS TRUE AND r.state <> 'TRASHED'::badasscouncil.room_state "
      + "ORDER BY r.name ")
  List<RoomRecord> listRooms();

  @NativeQuery("SELECT DISTINCT "
      + "  TO_CHAR(r.created_on, 'MM-DD-YYYY HH24:MI:SS') as created_on, "
      + "  TO_CHAR(r.updated_on, 'MM-DD-YYYY HH24:MI:SS') as updated_on, "
      + "  r.room_id, "
      + "  r.name, "
      + "  r.state, "
      + "  r.user_id AS owner_id, "
      + "  CASE WHEN LENGTH(r.password_hash) > 0 THEN 'needed' ELSE '' END AS password, "
      + "  r.topic, "
      + "  r.notes, "
      + "  r.purge_type, "
      + "  r.messages_limit, "
      + "  r.time_duration, "
      + "  r.listed_users_type "
      + "FROM badasscouncil.rooms AS r "
      + "WHERE r.enabled IS TRUE"
      + "  AND r.state <> 'TRASHED'::badasscouncil.room_state"
      + "  AND ( "
      + "       (r.user_id = :user) "
      + "    OR (r.listed_users_type = 0) "
      + "    OR (r.listed_users_type = 1 AND r.room_id     IN (SELECT rau.room_id FROM badasscouncil.rooms_allowed_users    AS rau WHERE rau.user_id = :user)) "
      + "    OR (r.listed_users_type = 2 AND r.room_id NOT IN (SELECT rdu.room_id FROM badasscouncil.rooms_disallowed_users AS rdu WHERE rdu.user_id = :user)) "
      + "      ) "
      + "ORDER BY r.name ")
  List<RoomRecord> listRooms(@Param("user") int user);

  @NativeQuery("SELECT DISTINCT r.room_id FROM badasscouncil.rooms AS r WHERE r.state = 'TRASHED'::badasscouncil.room_state ")
  List<Integer> findTrashedState();

  @NativeQuery("SELECT DISTINCT r.room_id FROM badasscouncil.rooms AS r WHERE r.purge_type = 'MESSAGES_LIMITED'::badasscouncil.room_purge_type ")
  List<Integer> findLimitedMessages();

  @NativeQuery("SELECT DISTINCT r.room_id FROM badasscouncil.rooms AS r WHERE r.purge_type = 'TIME_LIMITED'::badasscouncil.room_purge_type ")
  List<Integer> findLimitedLife();
  
  
  @NativeQuery("SELECT rau.user_id FROM badasscouncil.rooms_allowed_users AS rau WHERE rau.room_id = :room ")
  List<Integer> findAllowedUsers(@Param("room") int room);
  
  @NativeQuery("SELECT COUNT(rau.*) FROM badasscouncil.rooms_allowed_users AS rau WHERE rau.room_id = :room ")
  long countAllowedUsers(@Param("room") int room);

  @Modifying(flushAutomatically = true)
  @NativeQuery("INSERT INTO badasscouncil.rooms_allowed_users (room_id, user_id) VALUES (:room, :user) ")
  void addAllowedUser(@Param("room") int room, @Param("user") int user);

  @Modifying(clearAutomatically = true)
  @NativeQuery("DELETE FROM badasscouncil.rooms_allowed_users AS rau WHERE rau.room_id = :room ")
  void removeAllowedUsers(@Param("room") int room);
  

  @NativeQuery("SELECT rdu.user_id FROM badasscouncil.rooms_disallowed_users AS rdu WHERE rdu.room_id = :room ")
  List<Integer> findDisallowedUsers(@Param("room") int room);
  
  @NativeQuery("SELECT COUNT(rdu.*) FROM badasscouncil.rooms_disallowed_users AS rdu WHERE rdu.room_id = :room ")
  long countDisallowedUsers(@Param("room") int room);

  @Modifying(flushAutomatically = true)
  @NativeQuery("INSERT INTO badasscouncil.rooms_disallowed_users (room_id, user_id) VALUES (:room, :user) ")
  void addDisallowedUser(@Param("room") int room, @Param("user") int user);

  @Modifying(clearAutomatically = true)
  @NativeQuery("DELETE FROM badasscouncil.rooms_disallowed_users AS rdu WHERE rdu.room_id = :room ")
  void removeDisallowedUsers(@Param("room") int room);
  
  
  @Override
  void delete(Room r);

}
