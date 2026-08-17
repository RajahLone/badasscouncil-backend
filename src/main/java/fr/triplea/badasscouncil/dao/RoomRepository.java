package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.RoomTransfer;
import fr.triplea.badasscouncil.model.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT r.* FROM badasscouncil.rooms AS r WHERE r.room_id = :id ")
  Room findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT "
      + "r.room_id, "
      + "r.name, "
      + "r.status, "
      + "r.user_id as owner_id, "
      + "r.password_hash as password, "
      + "r.topic, "
      + "r.purge_method, "
      + "r.messages_limit, "
      + "r.time_duration "
      + "FROM badasscouncil.rooms AS r "
      + "WHERE r.enabled IS TRUE "
      + "ORDER BY r.name ")
  List<RoomTransfer> list();
  
  @Override
  void delete(Room r);

}
