package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.MessageShort;
import fr.triplea.badasscouncil.model.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> 
{

  @NativeQuery("SELECT DISTINCT "
             + "  TO_CHAR(m.created_on, 'MM-DD-YYYY HH24:MI:SS') as created_on, "
             + "  m.message_id, "
             + "  p.nick_name, "
             + "  m.content, "
             + "  CASE WHEN m.dest_id IS NULL THEN 0 ELSE m.dest_id END AS dest_id, "
             + "  CASE WHEN m.dest_id IS NULL THEN '' ELSE d.nick_name END AS dest_name "
             + "FROM badasscouncil.messages AS m "
             + "INNER JOIN badasscouncil.users AS p ON m.user_id = p.user_id "
             + "LEFT JOIN badasscouncil.users AS d ON m.dest_id = d.user_id "
             + "WHERE "
             + "     (m.room_id = :room) "
             + " AND (m.message_id > :last) "
             + " AND ((m.dest_id = :user) OR (m.user_id = :user) OR (m.dest_id IS NULL)) "
             + "ORDER BY m.message_id ASC ")
  List<MessageShort> findNew(@Param("room") int room, @Param("user") int user, @Param("last") int last);

  
  @Modifying(clearAutomatically = true)
  @NativeQuery("DELETE FROM badasscouncil.messages AS m WHERE m.room_id = :room ")
  void deleteAllByRoom(@Param("room") int room);
  
  @NativeQuery("SELECT DISTINCT COUNT(m.*) FROM badasscouncil.messages AS m WHERE m.room_id = :room AND m.message_id NOT IN (SELECT n.message_id FROM badasscouncil.messages AS n ORDER BY n.message_id DESC LIMIT :limit) ")
  long countMessagesLimited(@Param("room") int room, @Param("limit") int limit);
  
  @Modifying(clearAutomatically = true)
  @NativeQuery("DELETE FROM badasscouncil.messages AS m WHERE m.room_id = :room AND m.message_id NOT IN (SELECT n.message_id FROM badasscouncil.messages AS n ORDER BY n.message_id DESC LIMIT :limit) ")
  void deleteMesagesLimited(@Param("room") int room, @Param("limit") int limit);
  
  @NativeQuery("SELECT DISTINCT COUNT(m.*) FROM badasscouncil.messages AS m WHERE m.room_id = :room AND :lifespan > 0 AND (m.created_on::timestamp + CONCAT('', :lifespan, ' minutes')::interval) < NOW() ")
  long countLifeLimited(@Param("room") int room, @Param("lifespan") int lifespan);
  
  @Modifying(clearAutomatically = true)
  @NativeQuery("DELETE FROM badasscouncil.messages AS m WHERE m.room_id = :room AND :lifespan > 0 AND (m.created_on::timestamp + CONCAT('', :lifespan, ' minutes')::interval) < NOW() ")
  void deleteLifeLimited(@Param("room") int room, @Param("lifespan") int lifespan);

  @NativeQuery("SELECT DISTINCT COUNT(m.*) FROM badasscouncil.messages AS m WHERE m.user_id = :owner ")
  long countOwnedMessages(@Param("owner") int owner);

}
