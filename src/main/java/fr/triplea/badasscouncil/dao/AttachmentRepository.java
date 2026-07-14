package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.ProductionChoice;
import fr.triplea.badasscouncil.dto.ProductionFile;
import fr.triplea.badasscouncil.dto.ProductionItem;
import fr.triplea.badasscouncil.dto.AttachmentShort;
import fr.triplea.badasscouncil.model.Attachment;


public interface AttachmentRepository extends JpaRepository<Attachment, Integer> 
{

  @NativeQuery("SELECT DISTINCT p.* FROM vote.productions AS p WHERE p.numero_production = :numero AND p.flag_actif IS TRUE ")
  Attachment findById(@Param("numero") int numeroProduction);

  @NativeQuery("SELECT DISTINCT "
             + " p.* "
             + "FROM vote.presentations AS s "
             + "INNER JOIN vote.productions AS p ON s.numero_production = p.numero_production "
             + "WHERE s.numero_production = :numeroProduction AND s.numero_categorie = :numeroCategorie AND p.flag_actif IS TRUE ")
  Attachment findByIdLinkedByCategorie(@Param("numeroCategorie") int numeroCategorie, @Param("numeroProduction") int numeroProduction);
  
  @NativeQuery("SELECT DISTINCT " 
              + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
              + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
              + "p.numero_production, "
              + "CAST(p.adresse_ip AS VARCHAR) AS adresse_ip, "
              + "p.type, "
              + "p.titre, "
              + "p.auteurs, "
              + "p.groupes, "
              + "p.plateforme, "
              + "p.commentaire, "
              + "p.informations_privees, "
              + "p.numero_participant AS numero_gestionnaire, "
              + "CONCAT(g.pseudonyme, ' = ', g.nom, ' ', g.prenom) AS nom_gestionnaire, "
              + "p.nom_archive, "
              + "p.vignette, "
              + "p.numero_version,"
              + "0 AS numero_categorie, "
              + "0 AS ordre_presentation, "
              + "0 AS etat_media "
              + "FROM vote.productions AS p "
              + "INNER JOIN vote.participants AS g ON p.numero_participant = g.numero_participant "
              + "WHERE p.flag_actif IS TRUE "
              + "  AND ((:numero = 0) OR (:numero = p.numero_participant)) "
              + "  AND ((:solo = 0) OR ((:solo = 1) AND p.numero_production NOT IN (SELECT DISTINCT s.numero_production FROM vote.presentations AS s))) "
              + "  AND ((:type IS NULL) OR (p.type = (:type)::vote.type_production)) "
              + "ORDER BY p.titre, p.auteurs, p.groupes  ASC ")
  List<AttachmentShort> findAllWithoutArchiveOrderedByTitle(@Param("numero") int numeroGestionnaire, @Param("type") String type, @Param("solo") int solo);
  
  @NativeQuery("SELECT DISTINCT " 
              + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
              + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
              + "p.numero_production, "
              + "CAST(p.adresse_ip AS VARCHAR) AS adresse_ip, "
              + "p.type, "
              + "p.titre, "
              + "p.auteurs, "
              + "p.groupes, "
              + "p.plateforme, "
              + "p.commentaire, "
              + "p.informations_privees, "
              + "p.numero_participant AS numero_gestionnaire, "
              + "CONCAT(g.pseudonyme, ' = ', g.nom, ' ', g.prenom) AS nom_gestionnaire, "
              + "p.nom_archive, "
              + "p.vignette, "
              + "p.numero_version,"
              + "0 AS numero_categorie, "
              + "0 AS ordre_presentation, "
              + "0 AS etat_media "
              + "FROM vote.productions AS p "
              + "INNER JOIN vote.participants AS g ON p.numero_participant = g.numero_participant "
              + "WHERE p.flag_actif IS TRUE "
              + "  AND ((:numero = 0) OR (:numero = p.numero_participant)) "
              + "  AND ((:solo = 0) OR ((:solo = 1) AND p.numero_production NOT IN (SELECT DISTINCT s.numero_production FROM vote.presentations AS s))) "
              + "  AND ((:type IS NULL) OR (p.type = (:type)::vote.type_production)) "
              + "ORDER BY p.numero_production DESC ")
  List<AttachmentShort> findAllWithoutArchiveOrderedByInvertedId(@Param("numero") int numeroGestionnaire, @Param("type") String type, @Param("solo") int solo);
  
  @NativeQuery("SELECT DISTINCT " 
              + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
              + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
              + "p.numero_production, "
              + "CAST(p.adresse_ip AS VARCHAR) AS adresse_ip, "
              + "p.type, "
              + "p.titre, "
              + "p.auteurs, "
              + "p.groupes, "
              + "p.plateforme, "
              + "p.commentaire, "
              + "p.informations_privees, "
              + "p.numero_participant AS numero_gestionnaire, "
              + "CONCAT(g.pseudonyme, ' = ', g.nom, ' ', g.prenom) AS nom_gestionnaire, "
              + "p.nom_archive, "
              + "p.vignette, "
              + "p.numero_version,"
              + "0 AS numero_categorie, "
              + "0 AS ordre_presentation, "
              + "0 AS etat_media "
              + "FROM vote.productions AS p "
              + "INNER JOIN vote.participants AS g ON p.numero_participant = g.numero_participant "
              + "WHERE p.numero_production = :numeroProduction "
              + "  AND p.flag_actif IS TRUE ")
  AttachmentShort findByIdWithoutArchive(@Param("numeroProduction") Integer numeroProduction);
  
  @NativeQuery("SELECT DISTINCT " 
              + "TO_CHAR(p.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
              + "TO_CHAR(p.date_modification, 'DD/MM/YYYY HH24:MI:SS') as date_modification, "
              + "p.numero_production, "
              + "CAST(p.adresse_ip AS VARCHAR) AS adresse_ip, "
              + "p.type, "
              + "p.titre, "
              + "p.auteurs, "
              + "p.groupes, "
              + "p.plateforme, "
              + "p.commentaire, "
              + "p.informations_privees, "
              + "p.numero_participant AS numero_gestionnaire, "
              + "CONCAT(g.pseudonyme, ' = ', g.nom, ' ', g.prenom) AS nom_gestionnaire, "
              + "p.nom_archive, "
              + "p.vignette, "
              + "p.numero_version,"
              + "s.numero_categorie, "
              + "((c.numero_ordre * 10000) + s.numero_ordre) AS ordre_presentation, "
              + "s.flag_media AS etat_media "
              + "FROM vote.productions AS p "
              + "INNER JOIN vote.participants AS g ON p.numero_participant = g.numero_participant "
              + "INNER JOIN vote.presentations AS s ON p.numero_production = s.numero_production "
              + "INNER JOIN vote.categories AS c ON s.numero_categorie = c.numero_categorie "
              + "WHERE p.flag_actif IS TRUE "
              + "ORDER BY ordre_presentation ASC, p.titre ASC ")
  List<AttachmentShort> findLinkedWithoutArchive();

  @NativeQuery("SELECT DISTINCT " 
      + "p.numero_production, "
      + "p.numero_participant AS numero_gestionnaire, "
      + "p.titre, "
      + "p.nom_archive, "
      + "'' AS archive "
      + "FROM vote.productions AS p "
      + "WHERE p.numero_production = :numeroProduction "
      + "  AND p.flag_actif IS TRUE ")
  ProductionFile findByIdForUpload(@Param("numeroProduction") Integer numeroProduction);
  
  @NativeQuery("SELECT DISTINCT " 
      + "p.numero_production, "
      + "p.type, "
      + "p.titre, "
      + "p.auteurs, "
      + "p.groupes, "
      + "p.plateforme, "
      + "s.numero_ordre "
      + "FROM vote.productions AS p "
      + "INNER JOIN vote.presentations AS s ON p.numero_production = s.numero_production "
      + "WHERE s.numero_categorie = :numero "
      + "  AND p.flag_actif IS TRUE "
      + "ORDER BY s.numero_ordre ASC, p.titre ASC ")
  List<ProductionItem> findLinked(@Param("numero") int numeroCategorie);
 
  @NativeQuery("SELECT DISTINCT " 
      + "p.numero_production, "
      + "p.type, "
      + "p.titre, "
      + "p.auteurs, "
      + "p.groupes, "
      + "p.plateforme, "
      + "0 AS numero_ordre "
      + "FROM vote.productions AS p "
      + "WHERE p.numero_production IN (SELECT p.numero_production FROM vote.productions AS p WHERE p.flag_actif IS TRUE EXCEPT SELECT s.numero_production FROM vote.presentations AS s) "
      + "  AND p.flag_actif IS TRUE "
      + "ORDER BY p.titre ASC ")
  List<ProductionItem> findUnlinked();
  
  
  @NativeQuery("SELECT DISTINCT " 
      + "p.numero_production, "
      + "p.type, "
      + "p.titre, "
      + "p.auteurs, "
      + "p.groupes, "
      + "p.plateforme, "
      + "s.numero_ordre, "
      + "p.vignette "
      + "FROM vote.productions AS p "
      + "INNER JOIN vote.presentations AS s ON p.numero_production = s.numero_production "
      + "WHERE s.numero_categorie = :numero "
      + "  AND p.flag_actif IS TRUE "
      + "ORDER BY s.numero_ordre ASC, p.titre ASC ")
  List<ProductionChoice> findProposed(@Param("numero") int numeroCategorie);

  @NativeQuery("SELECT DISTINCT " 
      + "p.numero_production, "
      + "p.type, "
      + "p.titre, "
      + "p.auteurs, "
      + "p.groupes, "
      + "p.plateforme, "
      + "s.numero_ordre, "
      + "p.vignette "
      + "FROM vote.productions AS p "
      + "INNER JOIN vote.presentations AS s ON p.numero_production = s.numero_production "
      + "WHERE s.numero_production = :production AND s.numero_categorie = :categorie "
      + "  AND p.flag_actif IS TRUE ")
  ProductionChoice findChosen(@Param("categorie") int numeroCategorie, @Param("production") int numeroProduction);
  
  
  @Override
  void delete(Attachment production);
  
}
