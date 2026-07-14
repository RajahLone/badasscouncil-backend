package fr.triplea.badasscouncil.dto;

public record UserOptionList
(
  Integer userId, 
  String nickName, 
  String groupName, 
  String firstName,
  String lastName
) 
{ }