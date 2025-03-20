package at.kc.tugraz.ss.service.user.api;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import java.util.List;

public interface SSUserServerI {
  public Boolean userExists(final SSServPar parA) throws Exception;

  public SSUri userLogin(final SSServPar parA) throws Exception;

  public void userAdd(final SSServPar parA) throws Exception;

  public List<SSUri> userAll(final SSServPar parA) throws Exception;

  public SSUri userSystemGet(final SSServPar parA) throws Exception;
}