package at.kc.tugraz.ss.serv.serv.api;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.datatypes.datatypes.SSEntityDescA;
import at.kc.tugraz.ss.datatypes.datatypes.SSEntityEnum;
import at.kc.tugraz.ss.datatypes.datatypes.SSLabelStr;
import at.kc.tugraz.ss.serv.datatypes.entity.datatypes.par.SSEntityUserDirectlyAdjoinedEntitiesRemovePar;
import at.kc.tugraz.ss.service.rating.datatypes.SSRatingOverall;
import at.kc.tugraz.ss.service.tag.datatypes.SSTag;
import java.util.List;

public interface SSEntityHandlerImplI {
  public SSEntityDescA getDescForEntity(final SSEntityEnum entityType, final SSUri userUri, final SSUri entityUri, final SSLabelStr label, final Long creationTime, final List<SSTag> tags, final SSRatingOverall overallRating, final List<SSUri> discUris, final SSUri author) throws Exception;

  public void removeDirectlyAdjoinedEntitiesForUser(final SSEntityEnum entityType, final SSEntityUserDirectlyAdjoinedEntitiesRemovePar par) throws Exception;

  public Boolean setUserEntityPublic(final SSUri userUri, final SSUri entityUri, final SSEntityEnum entityType, final SSUri publicCircleUri) throws Exception;

  public Boolean shareUserEntity(final SSUri userUri, final List<SSUri> userUrisToShareWith, final SSUri entityUri, final SSUri circleUri, final SSEntityEnum entityType) throws Exception;

  public Boolean addEntityToCircle(final SSUri userUri, final SSUri circleUri, final SSUri entityUri, final SSEntityEnum entityType) throws Exception;
}