package fr.triplea.badasscouncil.web.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.badasscouncil.dao.UserRepository;
import fr.triplea.badasscouncil.dao.AttachmentRepository;
import fr.triplea.badasscouncil.dto.MessagesTransfer;
import fr.triplea.badasscouncil.dto.AttachmentFile;
import fr.triplea.badasscouncil.dto.AttachmentShort;
import fr.triplea.badasscouncil.dto.AttachmentTransfer;
import fr.triplea.badasscouncil.dto.AttachmentUpdate;
import fr.triplea.badasscouncil.model.Attachment;
import fr.triplea.badasscouncil.model.User;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;

@RestController
@RequestMapping("/attachment")
public class AttachmentController 
{
  //@SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AttachmentController.class);
  
  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private UserRepository participantRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;

 
  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<Attachment> getList(@RequestParam(name="sort", defaultValue="0", required = false) int s, final Authentication authentication) 
  { 
    List<AttachmentShort> files = null;
    
    files = attachmentRepository.findByOwner(this.getUserId(authentication));
     
    List<Attachment> ret = new ArrayList<Attachment>();
    
    if (files != null) { if (files.size() > 0) { for (AttachmentShort file: files) { ret.add(file.toAttachment()); } } }
    
    return ret;  
  }

  @GetMapping(value = "/file/{id}")
  @PreAuthorize("hasRole('USER')")
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable("id") int fileId, final Authentication authentication) 
  {
    Attachment p = attachmentRepository.findById(fileId);
           
    if (p != null) 
    { 
      int userId = this.getUserId(authentication);
            
      if ((userId == 0) || (p.getOwnerId() == userId))
      {
        byte[] data = null;
        
        File f = new File("../uploads", p.getLocalName());
        
        try 
        {
          data = new byte[(int) Math.min(f.length(), Integer.MAX_VALUE)]; //  limitation : 2 Go

          FileInputStream fis = new FileInputStream(f);
          
          fis.read(data);
          fis.close();
        } 
        catch (Exception e) { data = new byte[]{}; }
        
        
        Resource r = new ByteArrayResource(data);
        
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getArchiveName() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, "" + data.length)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_ZIP.toString())
                .body(r); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Attachment> getForm(@PathVariable("id") int fileId, final Authentication authentication)
  { 
    AttachmentShort p = attachmentRepository.searchById(fileId);
    
    if (p != null) 
    {
      int userId = this.getUserId(authentication);

      if ((userId == 0) || (p.ownerId() == userId))
      {
        return ResponseEntity.ok(p.toAttachment()); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/formfile/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<AttachmentFile> getFormFile(@PathVariable("id") int fileId, final Authentication authentication)
  { 
    AttachmentFile p = attachmentRepository.findByIdForUpload(fileId);
    
    if (p != null) 
    { 
      int userId = this.getUserId(authentication);

      if ((userId == 0) || (p.ownerId() == userId))
      {
        return ResponseEntity.ok(p); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Integer> create(@RequestBody(required = true) AttachmentTransfer file, HttpServletRequest request) 
  { 
    User user = participantRepository.findById(file.ownerId());

    if (user != null) 
    {
      Attachment fresh = new Attachment();
            
      fresh.setFileId(null);
      fresh.setIpAddress(new Inet(this.getClientIP(request)));
        
      fresh.setCommentsPublic(file.commentsPublic());
      fresh.setCommentsPrivate(file.commentsPrivate());
      
      fresh.setArchiveName(null); 
      fresh.setLocalName(null); 
      fresh.setVersionNumber(0);
      
      attachmentRepository.saveAndFlush(fresh);

      return ResponseEntity.ok(Integer.valueOf(fresh.getFileId()));
    }

    return ResponseEntity.notFound().build(); 
  }
 
  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> update(@PathVariable("id") int fileId, @RequestBody(required = true) AttachmentUpdate file, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      int userId = this.getUserId(authentication);

      if ((userId == 0) || (file.ownerId() == userId))
      {
        User user = participantRepository.findById(file.ownerId());
        
        if (user != null)
        {
          found.setUser(user);
          found.setEnabled(true);
          
          found.setIpAddress(new Inet(this.getClientIP(request)));
          
          found.setCommentsPublic(file.commentsPublic());
          found.setCommentsPrivate(file.commentsPrivate());
                
          attachmentRepository.saveAndFlush(found);
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.updated", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/upload-chunk/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> upload_chunk(@PathVariable("id") int fileId, @RequestParam String fileName, @RequestParam int chunkIndex, @RequestParam MultipartFile chunkData, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      int userId = this.getUserId(authentication);

      if ((userId == 0) || (found.getOwnerId() == userId))
      {
        MessagesTransfer mt = new MessagesTransfer();

        File dir = new File("../uploads-temp/" + fileId + "-" + fileName);
        
        if (!dir.exists()) { dir.mkdirs(); }

        File chunkFile = new File(dir, "chunk_" + chunkIndex);
        
        if (chunkFile.exists()) { chunkFile.delete(); }
        
        boolean succes = false;

        try 
        { 
          FileOutputStream os = new FileOutputStream(chunkFile);
              
          os.write(chunkData.getBytes());
          os.close();
          
          succes = true;
        }
        catch (Exception e) 
        { 
          LOG.error(e.toString());
          
          succes = false;
          
          chunkFile.delete(); 
        }
        
        if (chunkFile.exists()) { if (chunkFile.length() == chunkData.getSize()) { succes = true;  } } 
        
        if (succes) { mt.setInformation(messageSource.getMessage("chunk.upload.success", new Object[] { chunkIndex, fileName }, locale)); } 
               else { mt.setError(messageSource.getMessage("chunk.upload.failed", new Object[] { chunkIndex, fileName }, locale)); }
        
        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build();
  }
  @PostMapping(value = "/merge-chunks/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> merge_chunks(@PathVariable("id") int fileId, @RequestParam String fileName, @RequestParam int lastChunkIndex, @RequestParam String checksum, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      int userId = this.getUserId(authentication);

      if ((userId == 0) || (found.getOwnerId() == userId))
      {
        MessagesTransfer mt = new MessagesTransfer();

        File dir = new File("../uploads-temp/" + fileId + "-" + fileName);
        
        String nomLocal = UUID.nameUUIDFromBytes(("" + fileId + "-" + fileName).getBytes()).toString() + ".zip";

        File fic = new File("../uploads/" + nomLocal);

        if (fic.exists()) { fic.delete(); }
        
        boolean succes = false;
        
        int num = dir.listFiles().length;
        
        if (num == lastChunkIndex)
        {
          FileOutputStream os = null;
          
          try 
          {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            
            os = new FileOutputStream(fic);

            for (int i = 0; i < num; i++)
            {
              File chk = new File(dir, "chunk_" + i);
              
              byte[] bin = FileUtils.readFileToByteArray(chk);
                            
              md.update(bin);
              
              FileOutputStream fos = new FileOutputStream(fic, true);
              fos.write(bin);
              fos.flush();
              fos.close();
              
              chk.delete();
            }

            os.close();
           
            String digestat = DatatypeConverter.printHexBinary(md.digest());
                
            if (checksum.equalsIgnoreCase(digestat)) 
            {
              found.setArchiveName(fileName);
              found.setLocalName(nomLocal);
              found.setVersionNumber(found.getVersionNumber() + 1);
                          
              succes = true;
            }
            else { LOG.error(messageSource.getMessage("chunk.checksum.failed", new Object[] { fileName, checksum, digestat }, locale)); }
          }
          catch(Exception e) 
          { 
            LOG.error(e.toString());
            
            succes = false; 
            
            found.setArchiveName(null); 
            found.setLocalName(null); 
          }
          finally { try { os.close(); } catch(Exception e) { } }
        }
        else 
        { 
          LOG.error(messageSource.getMessage("chunk.count.failed", new Object[] { fileName, lastChunkIndex, dir.listFiles().length }, locale)); 
        }

        attachmentRepository.saveAndFlush(found);

        if (succes) { dir.delete(); }
        
        if (succes) { mt.setInformation(messageSource.getMessage("chunk.merged.success", new Object[] { fileName }, locale)); }
               else { mt.setError(messageSource.getMessage("chunk.merged.failed", new Object[] { fileName }, locale)); }
        
        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> disableProduction(@PathVariable("id") int fileId, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      int userId = this.getUserId(authentication);

      if ((userId == 0) || (found.getUser().getUserId() == userId))
      {
        found.setEnabled(true); 
        
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("production.indelible", null, locale));

        return ResponseEntity.ok(mt);        
      }
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  
  private final String getClientIP(HttpServletRequest request) 
  {
    final String h = request.getHeader("X-Forwarded-For");
    
    if (h != null) { if (!(h.isBlank())) { if (!(h.contains(request.getRemoteAddr()))) { return h.split(",")[0]; } } } 
    
    return request.getRemoteAddr();
  }

  /** returns 0 if ROLE_ADMIN, else if USER id */
  private final int getUserId(Authentication auth)
  {
    int numeroParticipant = -1; // -1 = not found
    
    if (auth != null)
    {
      User found = participantRepository.findByLoginName(auth.getName());
      
      if (found != null)
      {
        numeroParticipant = found.getUserId();
        
        List<String> roles = auth.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) { numeroParticipant = 0; }
      }
    }
    
    return numeroParticipant;
  }
  
}
