package fr.triplea.badasscouncil.dto;

public record AttachmentFile
(
  int fileId,
  int ownerId,
  String archiveName,
  String archive
) 
{ }
