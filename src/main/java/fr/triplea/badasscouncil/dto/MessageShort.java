package fr.triplea.badasscouncil.dto;

public record MessageShort
(
  String createdOn,  
  int messageId,
  String nickName,
  String content,
  int destId,
  String destName
) 
{
}
