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
  Integer versionNumber
) 
{ 
  public Attachment toAttachment() 
  {
    Attachment p = new Attachment();
    
    p.setCreatedOn(createdOn);
    p.setUpdatedOn(updatedOn);
    p.setId(fileId);
    p.setOwnerId(ownerId);
    p.setOwnerName(ownerName);
    p.setIpAddress(IpAddress);

    p.setCommentsPublic(commentsPublic);
    p.setCommentsPrivate(commentsPrivate);
    p.setArchiveName(archiveName);
    p.setLocalName(localName);
    p.setVersionNumber(versionNumber);
    
    return p;
  }  
}
