package fr.triplea.badasscouncil.dto;

public record RoomRecord 
(
  String createdOn,
  String updatedOn,
  int roomId,
  String name,
  String state, 
  int ownerId,
  String password,
  String topic,
  String notes,
  String purgeType,
  int messagesLimit,
  int timeDuration,
  int listedUsersType
)
{ 
}
