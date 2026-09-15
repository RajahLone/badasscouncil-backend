package fr.triplea.badasscouncil.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.triplea.badasscouncil.model.Image;


public interface ImageRepository extends JpaRepository<Image, Integer> 
{
  
  
}
