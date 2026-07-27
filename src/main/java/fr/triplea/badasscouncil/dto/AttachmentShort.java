package fr.triplea.badasscouncil.dto;

import fr.triplea.badasscouncil.model.Attachment;

public record AttachmentShort
(
  String createdOn,
  String updatedOn,
  Integer fileId,
  Integer ownerId,
  String ownerName,
  String IpAddress,
  String commentsPublic,
  String commentsPrivate,
  String archiveName,
  String localName,
  Integer versionNumber,
  Boolean shared
) 
{ 
  public Attachment toAttachment(boolean owning) 
  {
    Attachment p = new Attachment();
    
    p.setCreatedOn(createdOn);
    p.setUpdatedOn(updatedOn);
    p.setFileId(fileId);
    p.setOwnerId(ownerId);
    p.setOwnerName(ownerName);
    p.setIpAddress(owning ? IpAddress : "");

    p.setCommentsPublic(commentsPublic);
    p.setCommentsPrivate(owning ? commentsPrivate : "");
    p.setArchiveName(archiveName);
    p.setLocalName(owning ? localName : "");
    p.setVersionNumber(versionNumber);    
    p.setShared(shared);
    
    return p;
  }  
}
