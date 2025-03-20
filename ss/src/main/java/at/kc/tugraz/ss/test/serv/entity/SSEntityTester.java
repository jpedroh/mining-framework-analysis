package at.kc.tugraz.ss.test.serv.entity;
import at.kc.tugraz.ss.serv.serv.datatypes.entity.conf.SSEntityConf;
import at.kc.tugraz.ss.serv.datatypes.entity.serv.SSEntityServ;

public class SSEntityTester extends Thread {
  @Override public void run() {
    final SSEntityConf entityConf = (SSEntityConf) SSEntityServ.inst.servConf;
    if (!entityConf.executeOpAtStartUp) {
      return;
    }
    switch (entityConf.op) {
      case entityUserDirectlyAdjoinedEntitiesRemove:
      new Thread(new SSEntityUserDirectlyAdjoinedEntitiesRemoveTest(entityConf)).start();
      break;
      case entityDescGet:
      new Thread(new SSEntityDescGetTest(entityConf)).start();
      break;
      case entityCircleCreate:
      new Thread(new SSEntityCircleCreateTest(entityConf)).start();
      break;
      case entityEntitiesToCircleAdd:
      new Thread(new SSEntityEntitiesToCircleAddTest(entityConf)).start();
      break;
      case entityUserUsersToCircleAdd:
      new Thread(new SSEntityUserUsersToCircleAddTest(entityConf)).start();
      break;
    }
  }
}