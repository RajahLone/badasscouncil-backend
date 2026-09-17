package fr.triplea.badasscouncil.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.ImageDownload;
import fr.triplea.badasscouncil.model.Image;


public interface ImageRepository extends JpaRepository<Image, Integer> 
{

  @NativeQuery("SELECT DISTINCT i.image_id, i.user_id, CASE WHEN i.dest_id IS NULL THEN 0 ELSE i.dest_id END AS dest_id, i.file_name, i.thumbnail AS data FROM badasscouncil.images AS i WHERE i.message_id = :msg AND i.image_id = :img AND i.enabled IS TRUE ")
  ImageDownload findThumbnailById(@Param("msg") int messageId, @Param("img") int imageId);

  @NativeQuery("SELECT DISTINCT i.image_id, i.user_id, CASE WHEN i.dest_id IS NULL THEN 0 ELSE i.dest_id END AS dest_id, i.file_name, i.data FROM badasscouncil.images AS i WHERE i.message_id = :msg AND i.image_id = :img AND i.enabled IS TRUE ")
  ImageDownload findPlainById(@Param("msg") int messageId, @Param("img") int imageId);

  // table has ON DELETE CASCADE on foreign key, for automatic purge when messages or users are deleted
}
