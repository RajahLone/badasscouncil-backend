package fr.triplea.badasscouncil.dto;

public record MessageShort
(
  String dateCreation,  
  int numeroMessage,
  String pseudonyme,
  String ligne,
  int numeroDestinataire,
  String pseudoDestinataire
) 
{
}
