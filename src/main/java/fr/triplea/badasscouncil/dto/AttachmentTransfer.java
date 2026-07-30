package fr.triplea.badasscouncil.dto;

public record AttachmentTransfer
(
  int fileId,
  int ownerId,
  String commentsPublic,
  String commentsPrivate,
  String archiveName,
  String localName,
  int versionNumber,
  boolean shared,
  int lifeSpan
) 
{ }
