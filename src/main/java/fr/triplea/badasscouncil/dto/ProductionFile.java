package fr.triplea.badasscouncil.dto;

public record ProductionFile
(
  int numeroProduction,
  int numeroGestionnaire,
  String titre,
  String nomArchive,
  String archive
) 
{ }
