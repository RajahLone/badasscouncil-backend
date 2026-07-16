package fr.triplea.badasscouncil.dto;

public record UserList
(
  int userId,
  String status,
  String nickName,
  String groupName,
  String firstName,
  String lastName,
  String email
) 
{
}
