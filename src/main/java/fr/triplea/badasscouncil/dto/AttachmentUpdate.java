package fr.triplea.badasscouncil.dto;

public record AttachmentUpdate
(
  String createdOn,
  String updatedOn,
  int fileId,
  int ownerId,
  String ownerName,
  String IpAddress,
  String commentsPublic,
  String commentsPrivate,
  String archiveName,
  String localName,
  int versionNumber,
  int destId,
  boolean shared,
  int lifeSpan
) 
{ }
