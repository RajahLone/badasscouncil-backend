package fr.triplea.badasscouncil.dto;

public record ImageDownload
(
  int imageId,
  int userId,
  int destId,
  String fileName,
  byte[] data
)
{}
