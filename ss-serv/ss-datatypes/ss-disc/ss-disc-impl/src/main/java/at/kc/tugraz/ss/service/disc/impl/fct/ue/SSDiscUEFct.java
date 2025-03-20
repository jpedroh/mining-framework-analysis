package at.kc.tugraz.ss.service.disc.impl.fct.ue;
import at.kc.tugraz.socialserver.utils.SSLogU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.ss.datatypes.datatypes.SSUEEnum;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;
import at.kc.tugraz.ss.service.disc.datatypes.pars.SSDiscUserEntryAddPar;

public class SSDiscUEFct {
  public static void discCreate(final SSDiscUserEntryAddPar par, final SSUri discUri) {
    if (!par.saveUE) {
      return;
    }
    try {
      SSServCaller.ueAdd(par.user, par.target, SSUEEnum.discussEntity, SSUri.toStr(discUri), false);
      SSServCaller.ueAdd(par.user, discUri, SSUEEnum.newDiscussionByDiscussEntity, SSUri.toStr(par.target), false);
    } catch (Exception error) {
      SSLogU.warn("storing ue failed");
    }
  }

  public static void discEntryAdd(final SSDiscUserEntryAddPar par, final SSUri discEntryUri) {
    if (!par.saveUE) {
      return;
    }
    try {
      SSServCaller.ueAdd(par.user, par.disc, SSUEEnum.addDiscussionComment, SSStrU.empty, false);
    } catch (Exception error) {
      SSLogU.warn("storing ue failed");
    }
  }
}