package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.AttachmentFile;
import fr.triplea.badasscouncil.dto.AttachmentShort;
import fr.triplea.badasscouncil.model.Attachment;


public interface AttachmentRepository extends JpaRepository<Attachment, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT COUNT(a.*) AS nombre FROM badasscouncil.attachments AS a WHERE a.enabled IS TRUE AND ((:owner = 0) OR (a.user_id = :owner) OR (a.dest_id = :owner) OR (a.shared IS TRUE)) ")
  int countForEveryone(@Param("owner") int owner);  

  @NativeQuery("SELECT DISTINCT COUNT(a.*) AS nombre FROM badasscouncil.attachments AS a WHERE a.enabled IS TRUE AND (a.user_id = :owner) ")
  int countForOwnerOnly(@Param("owner") int owner);  

  @NativeQuery("SELECT DISTINCT a.* FROM badasscouncil.attachments AS a WHERE a.file_id = :id AND a.enabled IS TRUE ")
  Attachment findById(@Param("id") int fileId);
    
  @NativeQuery("SELECT DISTINCT " 
      + "TO_CHAR(a.created_on, 'MM-DD-YYYY HH24:MI:SS') as created_on, "
      + "TO_CHAR(a.updated_on, 'MM-DD-YYYY HH24:MI:SS') as updated_on, "
      + "a.file_id, "
      + "a.user_id AS owner_id, "
      + "CONCAT(u.nick_name, ' / ', u.group_name) AS ower_name, "
      + "CAST(a.ip_address AS VARCHAR) AS ip_address, "
      + "a.comments_public, "
      + "a.comments_private, "
      + "a.archive_name, "
      + "a.local_name, "
      + "a.version_number, "
      + "a.dest_id, "
      + "a.shared "
      + "FROM badasscouncil.attachments AS a "
      + "INNER JOIN badasscouncil.users AS u ON a.user_id = u.user_id "
      + "WHERE a.file_id = :id "
      + "  AND a.enabled IS TRUE ")
  AttachmentShort searchById(@Param("id") Integer id);
  
  @NativeQuery("SELECT DISTINCT " 
      + "TO_CHAR(a.created_on, 'MM-DD-YYYY HH24:MI:SS') as created_on, "
      + "TO_CHAR(a.updated_on, 'MM-DD-YYYY HH24:MI:SS') as updated_on, "
      + "a.file_id, "
      + "a.user_id AS owner_id, "
      + "CONCAT(u.nick_name, ' / ', u.group_name) AS ower_name, "
      + "CAST(a.ip_address AS VARCHAR) AS ip_address, "
      + "a.comments_public, "
      + "a.comments_private, "
      + "a.archive_name, "
      + "a.local_name, "
      + "a.version_number, "
      + "a.dest_id, "
      + "a.shared "
      + "FROM badasscouncil.attachments AS a "
      + "INNER JOIN badasscouncil.users AS u ON a.user_id = u.user_id "
      + "WHERE a.enabled IS TRUE "
      + "  AND ((:owner = 0) OR (a.user_id = :owner) OR (a.dest_id = :owner) OR (a.shared IS TRUE)) "
      + "ORDER BY a.archive_name ASC, a.file_id "
      + "LIMIT :limit OFFSET :start ")
  List<AttachmentShort> findByOwner(@Param("owner") Integer owner, @Param("start") int start, @Param("limit") Integer limit);

  
  @NativeQuery("SELECT DISTINCT " 
      + "a.file_id, "
      + "a.user_id AS owner_id, "
      + "a.archive_name, "
      + "'' AS archive "
      + "FROM badasscouncil.attachments AS a "
      + "WHERE a.file_id = :file "
      + "  AND a.enabled IS TRUE ")
  AttachmentFile findByIdForUpload(@Param("file") Integer file);
  
   
  
  @Override
  void delete(Attachment production);
  
}
