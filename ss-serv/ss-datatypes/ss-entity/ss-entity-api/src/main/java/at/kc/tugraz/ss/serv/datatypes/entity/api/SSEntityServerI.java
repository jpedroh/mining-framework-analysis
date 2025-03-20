package at.kc.tugraz.ss.serv.datatypes.entity.api;
import at.kc.tugraz.ss.datatypes.datatypes.SSEntityEnum;
import at.kc.tugraz.ss.datatypes.datatypes.SSLabelStr;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.datatypes.datatypes.SSEntityDescA;
import at.kc.tugraz.ss.serv.datatypes.entity.datatypes.SSCircle;
import at.kc.tugraz.ss.serv.datatypes.entity.datatypes.SSEntityCircleTypeE;
import java.util.List;

public interface SSEntityServerI {
  public SSEntityEnum entityTypeGet(final SSServPar parA) throws Exception;

  public SSLabelStr entityLabelGet(final SSServPar parA) throws Exception;

  public SSEntityDescA entityDescGet(final SSServPar parA) throws Exception;

  public SSUri entityAuthorGet(final SSServPar parA) throws Exception;

  public SSUri entityAdd(final SSServPar parA) throws Exception;

  public SSUri entityLabelSet(final SSServPar parA) throws Exception;

  public SSUri entityUserCircleCreate(final SSServPar parA) throws Exception;

  public SSUri entityUserUsersToCircleAdd(final SSServPar parA) throws Exception;

  public SSUri entityUserEntitiesToCircleAdd(final SSServPar parA) throws Exception;

  public Boolean entityUserAllowedIs(final SSServPar parA) throws Exception;

  public SSUri entityUserPublicSet(final SSServPar parA) throws Exception;

  public SSUri entityUserShare(final SSServPar parA) throws Exception;

  public List<SSCircle> entityUserCirclesGet(final SSServPar parA) throws Exception;

  public List<SSEntityCircleTypeE> entityUserEntityCircleTypesGet(final SSServPar parA) throws Exception;

  public List<SSCircle> entityUserEntityCirclesGet(final SSServPar parA) throws Exception;

  public SSUri entityCircleURIPublicGet(final SSServPar parA) throws Exception;

  public SSUri entityCircleCreate(final SSServPar parA) throws Exception;

  public SSUri entityCirclePublicAdd(final SSServPar parA) throws Exception;

  public SSUri entityEntitiesToCircleAdd(final SSServPar parA) throws Exception;

  public SSUri entityUsersToCircleAdd(final SSServPar parA) throws Exception;

  public SSEntityCircleTypeE entityMostOpenCircleTypeGet(final SSServPar parA) throws Exception;

  public SSUri entityAddAtCreationTime(final SSServPar parA) throws Exception;

  public Long entityCreationTimeGet(final SSServPar parA) throws Exception;

  public SSUri entityUserDirectlyAdjoinedEntitiesRemove(final SSServPar parA) throws Exception;

  public SSUri entityRemove(final SSServPar parA) throws Exception;
}