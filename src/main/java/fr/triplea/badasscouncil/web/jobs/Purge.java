package fr.triplea.badasscouncil.web.jobs;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.triplea.badasscouncil.dao.AttachmentRepository;
import fr.triplea.badasscouncil.model.Attachment;

@Component
public class Purge 
{
  // TODO: purge for users
  // TODO: purge for messages and rooms
  
  //@SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(Purge.class);
  
  @Autowired
  private AttachmentRepository attachmentRepository;

  
  @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS) // every minutes
  public void removeDisabledAttachments() 
  {
    try 
    {
      Attachment a = null;
      
      List<Integer> ids = attachmentRepository.findDisabled();
      
      if (ids != null)
      {
        if (ids.size() > 0)
        {
          for (int i = 0; i < ids.size(); i++)
          {
            a = attachmentRepository.findById(ids.get(i).intValue());
            
            if (a != null)
            {
              File f = new File("../uploads/" + a.getLocalName());
              
              if (f.exists() && f.isFile()) { f.delete(); }

              attachmentRepository.delete(a);
            }
          }
        }
      }
    }
    catch (Exception e) { LOG.error(e.toString()); }
  }
  

  @Scheduled(cron = "0 0 0 * * ?") // at midnight
  public void disableAttachments() 
  {
    try 
    {
      Attachment a = null;
      
      List<Integer> ids = attachmentRepository.findLifeSpanFinished();
      
      if (ids != null)
      {
        if (ids.size() > 0)
        {
          for (int i = 0; i < ids.size(); i++)
          {
            a = attachmentRepository.findById(ids.get(i).intValue());
            
            if (a != null)
            {
              a.setEnabled(false);

              attachmentRepository.saveAndFlush(a);
            }
          }
        }
      }
    }
    catch (Exception e) { LOG.error(e.toString()); }
  }
  
}
