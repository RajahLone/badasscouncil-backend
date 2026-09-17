package fr.triplea.badasscouncil.dto;

public record MessageShort
(
  String createdOn,  
  int messageId,
  String messageType,
  String nickName,
  String content,
  int destId,
  String destName
) 
{
}
