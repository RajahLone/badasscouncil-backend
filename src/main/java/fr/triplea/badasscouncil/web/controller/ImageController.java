package fr.triplea.badasscouncil.web.controller;


import java.util.Arrays;
import java.util.Base64;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.badasscouncil.dao.ImageRepository;
import fr.triplea.badasscouncil.dto.ImageDownload;
import fr.triplea.badasscouncil.web.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/image")
public class ImageController 
{
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);
  
  @Autowired
  private ImageRepository imageRepository;


  @Autowired
  private UserService userService;

 
  @GetMapping(value = "/thumbnail/{msg}/{img}")
  @PreAuthorize("hasRole('USER')")
  @ResponseBody
  public String getThumbnail(@PathVariable("msg") int messageId, @PathVariable("img") int imageId, final Authentication authentication, HttpServletResponse response) 
  {
    response.setContentType("text/plain");
    
    ImageDownload found = imageRepository.findThumbnailById(messageId, imageId);
           
    if (found != null) 
    { 
      int userId = userService.getUserId(authentication);
            
      if ((found.destId() == 0) || (userId == 0) || (found.userId() == userId) || (found.destId() == userId))
      {
        userService.setLastActivityOn(authentication);
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("<img alt=\"");
        sb.append(messageId);
        sb.append("_");
        sb.append(imageId);
        sb.append("\" src=\"data:image/png;base64,");
        sb.append(Base64.getEncoder().encodeToString(found.data()));
        sb.append("\"/>");
        
        return sb.toString();
      }
    }
    
    return "";
  }

  
  @GetMapping(value = "/plain/{msg}/{img}")
  @PreAuthorize("hasRole('USER')")
  @ResponseBody
  public ResponseEntity<Resource> getPlain(@PathVariable("msg") int messageId, @PathVariable("img") int imageId, final Authentication authentication) 
  {
    ImageDownload found = imageRepository.findPlainById(messageId, imageId);
           
    if (found != null) 
    { 
      int userId = userService.getUserId(authentication);
            
      if ((found.destId() == 0) || (userId == 0) || (found.userId() == userId) || (found.destId() == userId))
      {
        userService.setLastActivityOn(authentication);        
       
        byte[] data = found.data();
        
        byte[] head = new byte[1024]; Arrays.fill(head, (byte) 0);
        
        System.arraycopy(data, 0, head, 0, Math.min(data.length, 1024));
        
        Tika tika = new Tika();
        
        String mime = tika.detect(head);
        
        if (mime == null) { mime = tika.detect(found.fileName()); }
        
        Resource r = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + found.fileName() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, "" + data.length)
                .header(HttpHeaders.CONTENT_TYPE, mime)
                .body(r); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  
}
