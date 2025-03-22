package com.salesmanager.shop.store.api.v1.content;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.content.ContentFile;
import com.salesmanager.shop.model.content.ContentFolder;
import com.salesmanager.shop.model.content.ContentName;
import com.salesmanager.shop.model.content.PersistableContentEntity;
import com.salesmanager.shop.model.content.PersistableContent;
import com.salesmanager.shop.model.content.ReadableContentBox;
import com.salesmanager.shop.model.content.ReadableContentEntity;
import com.salesmanager.shop.model.content.ReadableContentFull;
import com.salesmanager.shop.model.content.ReadableContentPage;
import com.salesmanager.shop.store.api.exception.RestApiException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.content.facade.ContentFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

@RestController @RequestMapping(value = "/api/v1") @Api(tags = { "Content management resource (Content Management Api)" }) @SwaggerDefinition(tags = { @Tag(name = "Content management resource", description = "Add pages, content boxes, manage images and files") }) public class ContentApi {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContentApi.class);

  @Inject private ContentFacade contentFacade;

  /**
   * List all pages
   * @param merchantStore
   * @param language
   * @return
   */
  @GetMapping(value = "/content/pages", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get page names created for a given MerchantStore", notes = "", produces = "application/json", response = List.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public List<ReadableContentPage> getContentPages(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    return contentFacade.getContentPage(merchantStore, language);
  }

  @GetMapping(value = "/content/summary", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get pages summary created for a given MerchantStore. Content summary is a content bux having code summary.", notes = "", produces = "application/json", response = List.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public List<ReadableContentBox> pagesSummary(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    return contentFacade.getContentBoxes(ContentType.BOX, "summary_", merchantStore, language);
  }

  /**
   * List all boxes
   * @param merchantStore
   * @param language
   * @return
   */
  @GetMapping(value = "/content/boxes", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get pages summary created for a given MerchantStore", notes = "", produces = "application/json", response = List.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public List<ReadableContentBox> boxes(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    return contentFacade.getContentBoxes(ContentType.BOX, "summary_", merchantStore, language);
  }

  @GetMapping(value = "/content/pages/{code}", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get page content by code for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public ReadableContentPage page(@PathVariable(value = "code") String code, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    try {
      page = contentFacade.getContentPage(code, merchantStore, language);
    } catch (ResourceNotFoundException e) {
      LOGGER.debug("Resource not found [" + code + "] for store [" + merchantStore + "]");
    }
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java

    return contentFacade.getContentPage(code, merchantStore, language);
  }

  @GetMapping(value = "/content/pages/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get page content by code for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public ReadableContentPage pageByName(@PathVariable(value = "name") String name, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    return contentFacade.getContentPageByName(name, merchantStore, language);
  }


<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  @GetMapping(value = "/private/content/any/{code}", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get page content by code for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public ReadableContentFull content(@PathVariable(value = "code") String code, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    return contentFacade.getContent(code, merchantStore, language);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @GetMapping(value = "/private/contents/any", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get contents (page and boc) for a given MerchantStore", notes = "", produces = "application/json", response = ReadableContentPage.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public List<ReadableContentEntity> contents(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    Optional<String> op = Optional.empty();
    return contentFacade.getContents(op, merchantStore, language);
  }

  @GetMapping(value = "/content/boxes/{code}", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get box content by code for a code and a given MerchantStore", notes = "", produces = "application/json", response = List.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public ReadableContentBox getBoxByCode(@PathVariable(value = "code") String code, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    return contentFacade.getContentBox(code, merchantStore, language);
  }

  @GetMapping(value = "/content/folder", produces = MediaType.APPLICATION_JSON_VALUE) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public ContentFolder folder(@RequestParam(value = "path", required = false) String path, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) throws Exception {
    String decodedPath = decodeContentPath(path);
    return contentFacade.getContentFolder(decodedPath, merchantStore);
  }

  /**
   * @param code
   * @param path
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @GetMapping(value = "/content/images", produces = MediaType.APPLICATION_JSON_VALUE) @ApiOperation(httpMethod = "GET", value = "Get store content images", notes = "", response = ContentFolder.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public ContentFolder images(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language, @RequestParam(value = "path", required = false) String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
    String decodedPath = decodeContentPath(path);
    ContentFolder folder = contentFacade.getContentFolder(decodedPath, merchantStore);
    return folder;
  }

  private String decodeContentPath(String path) throws UnsupportedEncodingException {
    try {
      return StringUtils.isBlank(path) ? path : URLDecoder.decode(path, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RestApiException(e);
    }
  }

  /**
   * Need type, name and entity
   *
   * @param file
   */
  @PostMapping(value = "/private/file") @ResponseStatus(value = HttpStatus.CREATED) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public void upload(@RequestParam(value = "file") MultipartFile file, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    ContentFile f = new ContentFile();
    f.setContentType(file.getContentType());
    f.setName(file.getOriginalFilename());
    try {
      f.setFile(file.getBytes());
    } catch (IOException e) {
      throw new ServiceRuntimeException("Error while getting file bytes");
    }
    contentFacade.addContentFile(f, merchantStore.getCode());
  }

  @PostMapping(value = "/private/files", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }) @ResponseStatus(value = HttpStatus.CREATED) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public void uploadMultipleFiles(@RequestParam(value = "file[]", required = true) MultipartFile[] files, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    for (MultipartFile f : files) {
      ContentFile cf = new ContentFile();
      cf.setContentType(f.getContentType());
      cf.setName(f.getName());
      try {
        cf.setFile(f.getBytes());
      } catch (IOException e) {
        throw new ServiceRuntimeException("Error while getting file bytes");
      }
    }
  }

  /**
   * Create content page
   * @param page
   * @param merchantStore
   * @param language
   * @param pageCode
   */
  @PostMapping(value = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  "/private/content"
=======
  "/private/content/page"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
  ) @ResponseStatus(value = HttpStatus.OK) @ApiOperation(httpMethod = "POST", value = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  "Create content (page or box)"
=======
  "Create content page"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
  , notes = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  "content type is by default BOX, when creating a page specify contentType:PAGE"
=======
  "In order to create a page other than store default language, append ?lang=<LANGUAGE CODE> to post request"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
  , response = Void.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public void savePage(@RequestBody @Valid 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  PersistableContentEntity
=======
  PersistableContent
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
   page, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    contentFacade.saveContentPage(page, merchantStore, language);
  }

  @PutMapping(value = "/private/content/{id}") @ResponseStatus(value = HttpStatus.OK) @ApiOperation(httpMethod = "PUT", value = "Update content page", notes = "Updates a content page", response = Void.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public void updatePage(@PathVariable Long id, @RequestBody @Valid PersistableContentEntity page, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    page.setId(id);
    contentFacade.saveContentPage(page, merchantStore, language);
  }

  /**
   * Deletes a content from CMS
   *
   * @param name
   */
  @DeleteMapping(value = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  "/private/content/{id}"
=======
  "/private/content/page/{id}"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
  ) @ApiOperation(httpMethod = "DETETE", value = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  "Deletes a conyent from CMS"
=======
  "Deletes a file from CMS"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
  , notes = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
  "Delete a content box or page"
=======
  "Delete a file from server"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
  , response = Void.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT") }) public void deleteContent(Long id, @ApiIgnore MerchantStore merchantStore) {
    contentFacade.
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/left.java
    delete(merchantStore, id)
=======
    deletePage(merchantStore, id)
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/content/ContentApi.java/right.java
    ;
  }

  /**
   * Deletes a file from CMS
   *
   * @param name
   */
  @DeleteMapping(value = "/private/content/") @ApiOperation(httpMethod = "DETETE", value = "Deletes a file from CMS", notes = "Delete a file from server", response = Void.class) @ApiImplicitParams(value = { @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"), @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") }) public void deleteFile(@Valid ContentName name, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
    contentFacade.delete(merchantStore, name.getName(), name.getContentType());
  }
}