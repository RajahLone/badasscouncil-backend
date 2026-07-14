package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.dto.MessageShort;
import fr.triplea.badasscouncil.model.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> 
{

  @NativeQuery("SELECT DISTINCT "
             + "  TO_CHAR(m.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
             + "  m.numero_message, "
             + "  p.pseudonyme, "
             + "  m.ligne, "
             + "  CASE WHEN m.numero_destinataire IS NULL THEN 0 ELSE m.numero_destinataire END AS numero_destinataire, "
             + "  CASE WHEN m.numero_destinataire IS NULL THEN '' ELSE d.pseudonyme END AS pseudo_destinataire "
             + "FROM vote.messages AS m "
             + "INNER JOIN vote.participants AS p ON m.numero_participant = p.numero_participant "
             + "LEFT JOIN vote.participants AS d ON m.numero_destinataire = d.numero_participant "
             + "WHERE "
             + "     (m.numero_message > :last) "
             + " AND ((m.numero_destinataire = :participant) OR (m.numero_participant = :participant) OR (m.numero_destinataire IS NULL)) "
             + "ORDER BY m.numero_message DESC ")
  List<MessageShort> findNew(@Param("participant") int participant, @Param("last") int last);

}
