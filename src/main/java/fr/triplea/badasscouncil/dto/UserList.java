package fr.triplea.badasscouncil.dto;

public record UserList
(
  int userId,
  String nickName,
  String groupName,
  String firstName,
  String lastName,
  String email,
  String status
) 
{
}
