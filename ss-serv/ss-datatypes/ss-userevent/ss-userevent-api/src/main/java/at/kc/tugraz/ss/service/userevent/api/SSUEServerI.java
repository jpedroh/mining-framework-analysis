package at.kc.tugraz.ss.service.userevent.api;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.service.userevent.datatypes.SSUE;
import java.util.List;

public interface SSUEServerI {
  public SSUE uEGet(SSServPar parA) throws Exception;

  public List<SSUE> uEsGet(SSServPar parA) throws Exception;

  public Boolean uEAdd(SSServPar parA) throws Exception;

  public Boolean uEAddAtCreationTime(SSServPar parA) throws Exception;
}