package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
      + "  '' AS password, "
      + "  r.topic, "
      + "  r.notes, "
      + "  r.purge_type, "
      + "  r.messages_limit, "
      + "  r.time_duration "
      + "FROM badasscouncil.rooms AS r "
      + "WHERE r.enabled IS TRUE AND r.state <> 'TRASHED'::badasscouncil.room_state "
      + "ORDER BY r.name ")
  List<RoomRecord> listRooms();

  @NativeQuery("SELECT DISTINCT r.room_id FROM badasscouncil.rooms AS r WHERE r.state = 'TRASHED'::badasscouncil.room_state ")
  List<Integer> findTrashedState();

  @NativeQuery("SELECT DISTINCT r.room_id FROM badasscouncil.rooms AS r WHERE r.purge_type = 'MESSAGES_LIMITED'::badasscouncil.room_purge_type ")
  List<Integer> findLimitedMessages();

  @NativeQuery("SELECT DISTINCT r.room_id FROM badasscouncil.rooms AS r WHERE r.purge_type = 'TIME_LIMITED'::badasscouncil.room_purge_type ")
  List<Integer> findLimitedLife();
  
  
  @Override
  void delete(Room r);

}
