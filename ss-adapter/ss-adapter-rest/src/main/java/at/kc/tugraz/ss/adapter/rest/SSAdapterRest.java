package at.kc.tugraz.ss.adapter.rest;
import at.kc.tugraz.socialserver.utils.SSFileU;
import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.socialserver.utils.SSMimeTypeU;
import at.kc.tugraz.socialserver.utils.SSSocketU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.socialserver.utils.SSVarU;
import at.kc.tugraz.ss.adapter.rest.conf.SSAdapterRestConf;
import at.kc.tugraz.ss.adapter.socket.datatypes.SSSocketCon;
import at.kc.tugraz.ss.conf.conf.SSConf;
import at.kc.tugraz.ss.serv.err.reg.SSErrForClient;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import at.kc.tugraz.ss.serv.jsonld.util.SSJSONLDU;
import at.kc.tugraz.ss.service.user.api.SSUserGlobals;
import com.sun.jersey.multipart.FormDataParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.ArrayUtils;

@Path(value = "/SSAdapterRest") public class SSAdapterRest {
  private SSSocketCon sSCon = null;

  private int read = -1;

  private SSConf conf = null;

  public SSAdapterRest() throws Exception {
    SSAdapterRestConf.instSet(SSFileU.dirCatalinaBase() + SSFileU.folderConf + 
<<<<<<< /usr/src/app/output/learning-layers/socialsemanticserver/52a878a7af5ab841fc42d9973ca8cdd3bd375b20/ss-adapter/ss-adapter-rest/src/main/java/at/kc/tugraz/ss/adapter/rest/SSAdapterRest.java/left.java
    "ss-adapter-rest-conf-knowbrain2.0.yaml"
=======
    "ss-adapter-rest-conf.yaml"
>>>>>>> /usr/src/app/output/learning-layers/socialsemanticserver/52a878a7af5ab841fc42d9973ca8cdd3bd375b20/ss-adapter/ss-adapter-rest/src/main/java/at/kc/tugraz/ss/adapter/rest/SSAdapterRest.java/right.java
    );
    SSMimeTypeU.init();
    SSJSONLDU.init(SSAdapterRestConf.instGet().getJsonLDConf().uri);
    conf = SSAdapterRestConf.instGet().getSsConf();
  }

  @GET @Consumes(value = MediaType.TEXT_HTML) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "jsonLD" + SSStrU.slash + SSStrU.curlyBracketOpen + SSVarU.entityType + SSStrU.curlyBracketClose) public String jsonLD(@PathParam(value = SSVarU.entityType) String entityType) {
    String jsonRequ = "{\"op\":\"" + SSMethU.jsonLD + "\",\"user\":\"" + SSUserGlobals.systemUserURI + "/\",\"entityType\":\"" + entityType + "\",\"key\":\"681V454J1P3H4W3B367BB79615U184N22356I3E\"}";
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.jsonLD);
  }

  @GET @Consumes(value = MediaType.TEXT_HTML) @Produces(value = SSMimeTypeU.imagePng) @Path(value = SSStrU.slash + "fileThumbGet" + SSStrU.slash + SSStrU.curlyBracketOpen + SSVarU.id + SSStrU.curlyBracketClose) public Response fileThumbGet(@PathParam(value = SSVarU.id) String fileID) {
    String jsonRequ = "{\"op\":\"" + SSMethU.fileThumbGet + "\",\"user\":\"http://eval.bp/user/dt/\",\"fileId\":\"" + fileID + "\",\"key\":\"681V454J1P3H4W3B367BB79615U184N22356I3E\"}";
    List<Byte> bytesFromSS = new ArrayList<Byte>();
    String imageString = null;
    byte[] bytes;
    Byte[] nonPrimBytes;
    try {
      sSCon = new SSSocketCon(conf.host, conf.port, jsonRequ);
      sSCon.writeRequFullToSS();
      sSCon.readMsgFullFromSS();
      sSCon.writeRequFullToSS();
      while ((bytes = sSCon.readFileChunkFromSS()).length > 0) {
        for (int counter = 0; counter < bytes.length; counter++) {
          bytesFromSS.add(bytes[counter]);
        }
        sSCon.writeRequFullToSS();
      }
      nonPrimBytes = bytesFromSS.toArray(new Byte[bytesFromSS.size()]);
      imageString = "data:image/png;base64," + DatatypeConverter.printBase64Binary(ArrayUtils.toPrimitive(nonPrimBytes));
    } catch (Exception error) {
      try {
        return Response.serverError().build();
      } catch (Exception error1) {
        SSServErrReg.regErr(error1, "writing error to client didnt work");
      }
    } finally {
      sSCon.closeCon();
    }
    return Response.ok(imageString).build();
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "authCheckCred") public String authCheckCred(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.authCheckCred);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityUserPublicSet") public String entityUserPublicSet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityUserPublicSet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityUserCircleCreate") public String entityUserCircleCreate(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityUserCircleCreate);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityUserUsersToCircleAdd") public String entityUserUsersToCircleAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityUserUsersToCircleAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityUserEntitiesToCircleAdd") public String entityUserEntitiesToCircleAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityUserEntitiesToCircleAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityUserCirclesGet") public String entityUserCirclesGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityUserCirclesGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collsUserEntityIsInGet") public String collsUserEntityIsInGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collsUserEntityIsInGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collsUserCouldSubscribeGet") public String collsUserCouldSubscribeGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collsUserCouldSubscribeGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserRootGet") public String collUserRootGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserRootGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserParentGet") public String collUserParentGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserParentGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserEntryAdd") public String collUserEntryAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserEntryAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserEntriesAdd") public String collUserEntriesAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserEntriesAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserEntryChangePos") public String collUserEntryChangePos(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserEntryChangePos);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserEntryDelete") public String collUserEntryDelete(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserEntryDelete);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserEntriesDelete") public String collUserEntriesDelete(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserEntriesDelete);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityUserShare") public String entityUserShare(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityUserShare);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserWithEntries") public String collUserWithEntries(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserWithEntries);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collsUserWithEntries") public String collsUserWithEntries(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collsUserWithEntries);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserHierarchyGet") public String collUserHierarchyGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserHierarchyGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "collUserCumulatedTagsGet") public String collUserCumulatedTagsGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.collUserCumulatedTagsGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "discUserEntryAdd") public String discUserEntryAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.discUserEntryAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "discUserWithEntriesGet") public String discUserWithEntriesGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.discUserWithEntriesGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "discsUserAllGet") public String discsUserAllGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.discsUserAllGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityTypeGet") public String entityTypeGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityTypeGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityUserDirectlyAdjoinedEntitiesRemove") public String entityUserDirectlyAdjoinedEntitiesRemove(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityUserDirectlyAdjoinedEntitiesRemove);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityDescGet") public String entityDescGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityDescGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityLabelSet") public String entityLabelSet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityLabelSet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "entityLabelGet") public String entityLabelGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.entityLabelGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "fileExtGet") public String fileExtGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.fileExtGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "fileCanWrite") public String fileCanWrite(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.fileCanWrite);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "fileSetReaderOrWriter") public String fileSetReaderOrWriter(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.fileSetReaderOrWriter);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "fileUserFileWrites") public String fileUserFileWrites(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.fileUserFileWrites);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "fileWritingMinutesLeft") public String fileWritingMinutesLeft(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.fileWritingMinutesLeft);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpsGet") public String learnEpsGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpsGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionsGet") public String learnEpVersionsGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionsGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionGet") public String learnEpVersionGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionCurrentGet") public String learnEpVersionCurrentGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionCurrentGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionCurrentSet") public String learnEpVersionCurrentSet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionCurrentSet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionCreate") public String learnEpVersionCreate(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionCreate);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionAddCircle") public String learnEpVersionAddCircle(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionAddCircle);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionAddEntity") public String learnEpVersionAddEntity(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionAddEntity);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpCreate") public String learnEpCreate(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpCreate);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionUpdateCircle") public String learnEpVersionUpdateCircle(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionUpdateCircle);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionUpdateEntity") public String learnEpVersionUpdateEntity(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionUpdateEntity);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionSetTimelineState") public String learnEpVersionSetTimelineState(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionSetTimelineState);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionGetTimelineState") public String learnEpVersionGetTimelineState(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionGetTimelineState);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionRemoveCircle") public String learnEpVersionRemoveCircle(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionRemoveCircle);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "learnEpVersionRemoveEntity") public String learnEpVersionRemoveEntity(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.learnEpVersionRemoveEntity);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "locationAdd") public String locationAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.locationAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "locationsGet") public String locationsGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.locationsGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "modelUEResourceDetails") public String modelUEResourceDetails(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.modelUEResourceDetails);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "ratingOverallGet") public String ratingOverallGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.ratingOverallGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "ratingUserSet") public String ratingUserSet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.ratingUserSet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "scaffRecommTagsBasedOnUserEntityTag") public String scaffRecommTagsBasedOnUserEntityTag(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.scaffRecommTagsBasedOnUserEntityTag);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "scaffRecommTagsBasedOnUserEntityTagTime") public String scaffRecommTagsBasedOnUserEntityTagTime(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.scaffRecommTagsBasedOnUserEntityTagTime);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "scaffRecommTagsBasedOnUserEntityTagCategory") public String scaffRecommTagsBasedOnUserEntityTagCategory(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.scaffRecommTagsBasedOnUserEntityTagCategory);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "scaffRecommTagsBasedOnUserEntityTagCategoryTime") public String scaffRecommTagsBasedOnUserEntityTagCategoryTime(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.scaffRecommTagsBasedOnUserEntityTagCategoryTime);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "searchMIs") public String searchMIs(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.searchMIs);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "searchSolr") public String searchSolr(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.searchSolr);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "searchTags") public String searchTags(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.searchTags);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "tagAdd") public String tagAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.tagAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "tagUserFrequsGet") public String tagUserFrequsGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.tagUserFrequsGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "tagsUserRemove") public String tagsUserRemove(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.tagsUserRemove);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "userLogin") public String userLogin(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.userLogin);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "userAll") public String userAll(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.userAll);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "uEAdd") public String uEAdd(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.uEAdd);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "uEsGet") public String uEsGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.uEsGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "uEGet") public String uEGet(String jsonRequ) {
    return handleStandardJSONRESTCall(jsonRequ, SSMethU.uEGet);
  }

  @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_OCTET_STREAM) @Path(value = SSStrU.slash + "fileDownload") public Response fileDownload(String jsonRequ) {
    StreamingOutput stream = null;
    try {
      sSCon = new SSSocketCon(conf.host, conf.port, jsonRequ);
      sSCon.writeRequFullToSS();
      sSCon.readMsgFullFromSS();
      sSCon.writeRequFullToSS();
      stream = new StreamingOutput() {
        @Override public void write(OutputStream out) throws IOException {
          byte[] bytes;
          while ((bytes = sSCon.readFileChunkFromSS()).length > 0) {
            out.write(bytes);
            out.flush();
          }
          out.close();
        }
      };
    } catch (Exception error) {
      try {
        return Response.serverError().build();
      } catch (Exception error1) {
        SSServErrReg.regErr(error1, "writing error to client didnt work");
      }
    } finally {
    }
    return Response.ok(stream).build();
  }

  @POST @Consumes(value = MediaType.MULTIPART_FORM_DATA) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "fileReplace") public Response fileReplace(@FormDataParam(value = SSVarU.jsonRequ) String jsonRequ, @FormDataParam(value = SSVarU.file) InputStream file) {
    Response result = null;
    byte[] bytes = new byte[SSSocketU.socketTranmissionSize];
    String returnMsg;
    try {
      sSCon = new SSSocketCon(conf.host, conf.port, jsonRequ);
      sSCon.writeRequFullToSS();
      sSCon.readMsgFullFromSS();
      while ((read = file.read(bytes)) != -1) {
        sSCon.writeFileChunkToSS(bytes, read);
      }
      sSCon.writeFileChunkToSS(new byte[0], -1);
      returnMsg = sSCon.readMsgFullFromSS();
      return Response.status(200).entity(returnMsg).build();
    } catch (Exception error) {
      try {
        return Response.serverError().build();
      } catch (Exception error1) {
        SSServErrReg.regErr(error1, "writing error to client didnt work");
      }
    } finally {
      sSCon.closeCon();
    }
    return result;
  }

  @POST @Consumes(value = MediaType.MULTIPART_FORM_DATA) @Produces(value = MediaType.APPLICATION_JSON) @Path(value = SSStrU.slash + "fileUpload") public Response fileUpload(@FormDataParam(value = SSVarU.jsonRequ) String jsonRequ, @FormDataParam(value = SSVarU.file) InputStream file) {
    Response result = null;
    byte[] bytes = new byte[SSSocketU.socketTranmissionSize];
    String resultMsg;
    try {
      sSCon = new SSSocketCon(conf.host, conf.port, jsonRequ);
      sSCon.writeRequFullToSS();
      sSCon.readMsgFullFromSS();
      while ((read = file.read(bytes)) != -1) {
        sSCon.writeFileChunkToSS(bytes, read);
      }
      sSCon.writeFileChunkToSS(new byte[0], -1);
      resultMsg = sSCon.readMsgFullFromSS();
      sSCon.closeCon();
      return Response.status(200).entity(resultMsg).build();
    } catch (Exception error) {
      try {
        return Response.serverError().build();
      } catch (Exception error1) {
        SSServErrReg.regErr(error1, "writing error to client didnt work");
      }
    } finally {
      sSCon.closeCon();
    }
    return result;
  }

  private String handleStandardJSONRESTCall(String jsonRequ, SSMethU op) {
    String readMsgFullFromSS;
    try {
      sSCon = new SSSocketCon(conf.host, conf.port, jsonRequ);
      sSCon.writeRequFullToSS();
      readMsgFullFromSS = sSCon.readMsgFullFromSS();
      return readMsgFullFromSS;
    } catch (Exception error) {
      final List<SSErrForClient> errors = new ArrayList<SSErrForClient>();
      try {
        errors.add(SSErrForClient.get(error));
        return sSCon.prepErrorToClient(errors, op);
      } catch (Exception error1) {
        SSServErrReg.regErr(error1, "writing error to client didnt work");
      }
    } finally {
      sSCon.closeCon();
    }
    return null;
  }
}