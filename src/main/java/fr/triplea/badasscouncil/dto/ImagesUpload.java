package fr.triplea.badasscouncil.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record ImagesUpload
(
  MessageShortPass message,
  List<MultipartFile> files
) 
{}
