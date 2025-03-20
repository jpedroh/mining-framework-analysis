package at.kc.tugraz.ss.service.coll.impl.fct.ue;
import at.kc.tugraz.socialserver.utils.SSLogU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.ss.datatypes.datatypes.SSUEEnum;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;
import at.kc.tugraz.ss.service.coll.datatypes.pars.SSCollUserEntryAddPar;
import at.kc.tugraz.ss.service.coll.datatypes.pars.SSCollUserEntryDeletePar;

public class SSCollUEFct {
  public static void collUserEntryDelete(final SSCollUserEntryDeletePar par) {
    if (!par.saveUE) {
      return;
    }
    try {
      SSServCaller.ueAdd(par.user, par.collEntry, SSUEEnum.removeCollectionItem, SSUri.toStr(par.coll), false);
      SSServCaller.ueAdd(par.user, par.coll, SSUEEnum.changeCollectionByRemoveCollectionItem, SSUri.toStr(par.collEntry), false);
    } catch (Exception error) {
      SSLogU.warn("storing ue failed");
    }
  }

  public static void collUserUnSubscribeColl(final SSCollUserEntryDeletePar par) {
    if (!par.saveUE) {
      return;
    }
    try {
      SSServCaller.ueAdd(par.user, par.collEntry, SSUEEnum.unSubscribeCollection, SSStrU.empty, false);
    } catch (Exception error) {
      SSLogU.warn("storing ue failed");
    }
  }

  public static void collUserDeleteColl(final SSCollUserEntryDeletePar par) {
    if (!par.saveUE) {
      return;
    }
    try {
      SSServCaller.ueAdd(par.user, par.collEntry, SSUEEnum.removeCollection, SSStrU.empty, false);
    } catch (Exception error) {
      SSLogU.warn("storing ue failed");
    }
  }

  public static void collUserEntryAdd(final SSCollUserEntryAddPar par) {
  }
}