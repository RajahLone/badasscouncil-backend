package fr.triplea.badasscouncil.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.badasscouncil.model.Quote;


public interface QuoteRepository extends JpaRepository<Quote, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT q.* FROM badasscouncil.quotes AS q ORDER BY q.family ASC, q.code ASC ")
  List<Quote> findAll();
  
  @NativeQuery("SELECT DISTINCT q.* FROM badasscouncil.quotes AS q WHERE q.quote_id = :id ")
  Quote findById(@Param("id") int id);
  
  @NativeQuery("SELECT q.* FROM badasscouncil.quotes AS q ORDER BY RANDOM() LIMIT 1")
  Quote getRandom();
  
}
