package fr.triplea.badasscouncil.web.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
import fr.triplea.badasscouncil.dto.HomeInformationTransfer;
import fr.triplea.badasscouncil.dto.Pagination;
import fr.triplea.badasscouncil.dto.AttachmentFile;
import fr.triplea.badasscouncil.dto.AttachmentShort;
import fr.triplea.badasscouncil.dto.AttachmentTransfer;
import fr.triplea.badasscouncil.dto.AttachmentUpdate;
import fr.triplea.badasscouncil.model.Attachment;
import fr.triplea.badasscouncil.model.Preference;
import fr.triplea.badasscouncil.model.User;
import fr.triplea.badasscouncil.web.service.PreferenceService;
import fr.triplea.badasscouncil.web.service.UserService;
import fr.triplea.badasscouncil.web.service.VariableService;
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
  private PreferenceService preferenceService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private VariableService variableService;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;

 

  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<Attachment> getList(
      @RequestParam("name") String nameFilter, 
      @RequestParam("status") String statusFilter, 
      @RequestParam(name="sort", defaultValue="0") Integer sortType, 
      @RequestParam(name="page", defaultValue="0") int current, 
      @RequestParam(name="size", defaultValue="0") Integer length, 
      final Authentication authentication
      ) 
  { 
    if (authentication == null) { return new ArrayList<Attachment>(); }

    if (nameFilter != null) { if (nameFilter.isBlank()) { nameFilter = null; } else { nameFilter = nameFilter.trim().toUpperCase(); } }
    if (statusFilter != null) { if (statusFilter.isBlank()) { statusFilter = null; } else { statusFilter = statusFilter.trim().toUpperCase(); } }
   
    StringBuffer sb = new StringBuffer();
    
    if (nameFilter != null) { sb.append("&name="); sb.append(nameFilter); }
    if (statusFilter != null) { sb.append("&status="); sb.append(statusFilter); }
    if (sortType.intValue() > -1) { sb.append("&sort="); sb.append(sortType.toString()); }
    
    preferenceService.set(Preference.FILES_FILTERS, sb.toString(), authentication);

    length = preferenceService.getInteger(Preference.FILES_PER_MEMBER, authentication);
    
    if (length == null) 
    {
      length = 50;
      current = 0;
      
      preferenceService.set(Preference.USERS_PAGE_SIZE, "50", authentication);
    }
    
    int offset = 0;
    
    if (current > 0) { offset = ((current * length.intValue()) + 1); }
    
    List<AttachmentShort> files = null; 
     
    if (sortType != null)
    {
      switch (sortType.intValue()) 
      { 
        case 1: 
          files = attachmentRepository.findByOwnerMostRecent(userService.getUserId(authentication), nameFilter, statusFilter, offset, length); 
          break;
        default: 
          files = attachmentRepository.findByOwnerSortedByName(userService.getUserId(authentication), nameFilter, statusFilter, offset, length); 
          break;
      }
    }
    
    List<Attachment> ret = new ArrayList<Attachment>();
    
    if (files != null) { if (files.size() > 0) { for (AttachmentShort file: files) { ret.add(file.toAttachment(false)); } } }
    
    return ret;  
  }

  @GetMapping(value = "/pagination")
  @PreAuthorize("hasRole('USER')")
  public Pagination getCount(
      @RequestParam("name") String nameFilter, 
      @RequestParam("status") String statusFilter, 
      @RequestParam(name="page", defaultValue="0") int current, 
      final Authentication authentication
      ) 
  { 
    if (nameFilter != null) { if (nameFilter.isBlank()) { nameFilter = null; } else { nameFilter = nameFilter.trim().toUpperCase(); } }
    if (statusFilter != null) { if (statusFilter.isBlank()) { statusFilter = null; } else { statusFilter = statusFilter.trim().toUpperCase(); } }

    Integer size = preferenceService.getInteger(Preference.FILES_PER_MEMBER, authentication);
    
    if (size == null) 
    {
      size = 50;
      current = 0;
      
      preferenceService.set(Preference.FILES_PER_MEMBER, "50", authentication);
    }

    int items = attachmentRepository.countForEveryoneWithFilters(userService.getUserId(authentication), nameFilter, statusFilter);
     
    int pages = 0;
    
    int count = items; while (count > 0) { pages++; count -= size.intValue(); }
    
    current = Math.max(0, Math.min(current, pages - 1));
    
    return new Pagination(items, size.intValue(), pages, current);
  }

  @GetMapping(value = "/file/{id}")
  @PreAuthorize("hasRole('USER')")
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable("id") int fileId, final Authentication authentication) 
  {
    Attachment p = attachmentRepository.findById(fileId);
           
    if (p != null) 
    { 
      int userId = userService.getUserId(authentication);
            
      if ((userId == 0) || (p.getOwnerId() == userId) || p.isShared())
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
      int userId = userService.getUserId(authentication);

      if ((userId == 0) || (p.ownerId() == userId))
      {
        return ResponseEntity.ok(p.toAttachment(true)); 
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
      int userId = userService.getUserId(authentication);

      if ((userId == 0) || (p.ownerId() == userId))
      {
        return ResponseEntity.ok(p); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Integer> create(@RequestBody(required = true) AttachmentTransfer file, final Authentication authentication, HttpServletRequest request) 
  { 
    long cur = attachmentRepository.countForOwnerOnly(userService.getUserId(authentication));
    
    long max = variableService.getLong("Quota", "FILES_PER_MEMBER", 16);

    User user = null;

    if (max > cur) { user = userRepository.findById(file.ownerId()); }
    
    if (user != null) 
    {
      Attachment fresh = new Attachment();
            
      fresh.setFileId(null);
      fresh.setUser(user);
      fresh.setIpAddress(new Inet(this.getClientIP(request)));
            
      fresh.setCommentsPublic(file.commentsPublic());
      fresh.setCommentsPrivate(file.commentsPrivate());
      
      fresh.setArchiveName(null); 
      fresh.setLocalName(null); 
      fresh.setVersionNumber(0);
      
      fresh.setShared(file.shared());
      fresh.setLifeSpan(file.lifeSpan());
      
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
      int userId = userService.getUserId(authentication);

      if ((userId == 0) || ((file.ownerId() == userId) && found.getUser().getUserId() == userId))
      {
        User user = userRepository.findById(file.ownerId());
        User dest = userRepository.findById(file.destId());
        
        if (user != null)
        {
          found.setUser(user);
          found.setEnabled(true);
          
          found.setIpAddress(new Inet(this.getClientIP(request)));
          
          found.setCommentsPublic(file.commentsPublic());
          found.setCommentsPrivate(file.commentsPrivate());
          
          found.setRecipient(dest);
          
          found.setShared(file.shared());
          found.setLifeSpan(file.lifeSpan());
                
          attachmentRepository.saveAndFlush(found);
          
          HomeInformationTransfer mt = new HomeInformationTransfer();
          mt.setInfo(messageSource.getMessage("attachment.updated", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/claim/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> claim(@PathVariable("id") int fileId, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      int userId = userRepository.findByLoginName(authentication.getName()).getUserId();

      if (found.getRecipient().getUserId() == userId)
      {
        found.setEnabled(true);
        
        found.setUser(found.getRecipient());
        
        found.setRecipient(null);
        
        attachmentRepository.saveAndFlush(found);
        
        
        HomeInformationTransfer mt = new HomeInformationTransfer();
        mt.setInfo(messageSource.getMessage("attachment.claimed", null, locale));

        return ResponseEntity.ok(mt);        
      }
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/decline/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> decline(@PathVariable("id") int fileId, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      int userId = userRepository.findByLoginName(authentication.getName()).getUserId();

      if (found.getRecipient().getUserId() == userId)
      {
        found.setEnabled(true); 
        
        found.setRecipient(null);

        attachmentRepository.saveAndFlush(found);

        
        HomeInformationTransfer mt = new HomeInformationTransfer();
        mt.setInfo(messageSource.getMessage("attachment.declined", null, locale));

        return ResponseEntity.ok(mt);        
      }
    }      
    
    return ResponseEntity.notFound().build(); 
  }
  
  @PostMapping(value = "/upload-chunk/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> addFile_upload_chunk(
      @PathVariable("id") int fileId, 
      @RequestParam(name = "fileName") String name, 
      @RequestParam(name = "chunkIndex") int index, 
      @RequestParam(name = "chunkData") MultipartFile data, 
      final Authentication authentication, 
      HttpServletRequest request
      ) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      int userId = userService.getUserId(authentication);

      if ((userId == 0) || (found.getOwnerId() == userId))
      {
        HomeInformationTransfer mt = new HomeInformationTransfer();

        File dir = new File("../uploads-temp/" + fileId + "-" + name);
        
        if (!dir.exists()) { dir.mkdirs(); }

        File chunkFile = new File(dir, "chunk_" + index);
        
        if (chunkFile.exists()) { chunkFile.delete(); }
        
        boolean succes = false;

        try 
        { 
          FileOutputStream os = new FileOutputStream(chunkFile);
              
          os.write(data.getBytes());
          os.close();
          
          succes = true;
        }
        catch (Exception e) 
        { 
          LOG.error(e.toString());
          
          succes = false;
          
          chunkFile.delete(); 
        }
        
        long curSize = 0;
        long maxSize = variableService.getLong("Quota", "FILE_SIZE", 1000) * 1048576;
        
        File[] files = dir.listFiles();
        
        if (files != null) { if (files.length > 0) { for (int f = 0; f < files.length; f++) { curSize += files[f].length(); } } }
        
        if (curSize > maxSize) { chunkFile.delete(); succes = false; } else { if (chunkFile.exists()) { if (chunkFile.length() == data.getSize()) { succes = true;  } } }
        
        if (succes) { mt.setInfo(messageSource.getMessage("chunk.upload.success", new Object[] { index, name }, locale)); } 
               else { mt.setError(messageSource.getMessage("chunk.upload.failed", new Object[] { index, name }, locale)); }
        
        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build();
  }
  @PostMapping(value = "/merge-chunks/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> addFile_merge_chunks(
      @PathVariable("id") int fileId, 
      @RequestParam(name = "fileName") String name, 
      @RequestParam(name = "lastChunkIndex") int index, 
      @RequestParam(name = "checksum") String check, 
      final Authentication authentication, 
      HttpServletRequest request
      ) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      int userId = userService.getUserId(authentication);

      if ((userId == 0) || (found.getOwnerId() == userId))
      {
        HomeInformationTransfer mt = new HomeInformationTransfer();

        File dir = new File("../uploads-temp/" + fileId + "-" + name);

        long curSize = 0;
        long maxSize = variableService.getLong("Quota", "FILE_SIZE", 1000) * 1048576;
        
        File[] files = dir.listFiles();
        
        if (files != null) { if (files.length > 0) { for (int f = 0; f < files.length; f++) { curSize += files[f].length(); } } }

        if (curSize > maxSize)
        {
          for (int f = 0; f < files.length; f++) { files[f].delete(); }
          
          dir.delete();
          
          mt.setError(messageSource.getMessage("attachment.exceeds.maxsize", new Object[] { name }, locale));          
          
          return ResponseEntity.ok(mt);
        }
        else if (!userService.canUpload(authentication))
        {
         for (int f = 0; f < files.length; f++) { files[f].delete(); }
          
          dir.delete();
          
          mt.setError(messageSource.getMessage("storage.limit.reached", new Object[] { name }, locale));          
          
          return ResponseEntity.ok(mt);
        }
        
        String nomLocal = UUID.nameUUIDFromBytes(("" + fileId + "-" + name).getBytes()).toString() + ".zip";

        File fic = new File("../uploads/" + nomLocal);

        if (fic.exists()) { fic.delete(); }
        
        boolean succes = false;
        
        int num = dir.listFiles().length;
        
        if (num == index)
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
                
            if (check.equalsIgnoreCase(digestat)) 
            {
              found.setArchiveName(name);
              found.setLocalName(nomLocal);
              found.setVersionNumber(found.getVersionNumber() + 1);
                          
              succes = true;
            }
            else { LOG.error(messageSource.getMessage("chunk.checksum.failed", new Object[] { name, check, digestat }, locale)); }
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
          LOG.error(messageSource.getMessage("chunk.count.failed", new Object[] { name, index, dir.listFiles().length }, locale)); 
        }

        attachmentRepository.saveAndFlush(found);

        if (succes) { dir.delete(); }
        
        if (succes) { mt.setInfo(messageSource.getMessage("chunk.merged.success", new Object[] { name }, locale)); }
               else { mt.setError(messageSource.getMessage("chunk.merged.failed", new Object[] { name }, locale)); }
        
        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> disable(@PathVariable("id") int fileId, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Attachment found = attachmentRepository.findById(fileId);
    
    if (found != null)
    {
      int userId = userService.getUserId(authentication);

      if ((userId == 0) || (found.getUser().getUserId() == userId))
      {
        found.setEnabled(false); 
        
        attachmentRepository.saveAndFlush(found);
        
        HomeInformationTransfer mt = new HomeInformationTransfer();
        mt.setInfo(messageSource.getMessage("attachment.deleted", null, locale));

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

  
}
