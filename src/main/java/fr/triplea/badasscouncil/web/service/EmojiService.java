package fr.triplea.badasscouncil.web.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import fr.triplea.badasscouncil.web.controller.AccountController;


@Service
public class EmojiService 
{

  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Value("classpath:emoji-ordering.txt")
  private Resource resource;

  private static ArrayList<String> emojis_short = null;
  private static ArrayList<String> emojis_total = null;
  
  public void load()
  {
    emojis_short = new ArrayList<String>();
    emojis_total = new ArrayList<String>();
    
    try 
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()), 4096);
      
      String line = null; 
      int pos = 0;
      
      while ((line = br.readLine()) != null) 
      {
        if (!line.startsWith("#"))
        {
          if (line.startsWith("U+"))
          {
            pos = line.indexOf(" ;");
            
            if (pos > 0)
            {
              line = line.substring(0, pos);
              
              line = line.replace("U+", "&#x");
              
              if (line.contains(" "))
              {
                emojis_total.add(line.replace(" ", "; ") + ";");
              }
              else 
              {
                emojis_total.add(line + ";");
                emojis_short.add(line + ";");
              }
            }
          }
        }
      }
      
      br.close();
    } 
    catch (Exception e) 
    {
      LOG.error(e.toString());
    }
  }
      
  public ArrayList<String> getShortList() { return emojis_short; }

  public ArrayList<String> getTotalList() { return emojis_total; }

}
