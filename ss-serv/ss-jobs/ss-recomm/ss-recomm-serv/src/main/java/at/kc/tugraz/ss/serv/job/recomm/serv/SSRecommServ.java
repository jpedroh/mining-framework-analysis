package at.kc.tugraz.ss.serv.job.recomm.serv;
import at.kc.tugraz.socialserver.utils.SSDateU;
import at.kc.tugraz.ss.serv.job.recomm.api.SSRecommClientI;
import at.kc.tugraz.ss.serv.job.recomm.api.SSRecommServerI;
import at.kc.tugraz.ss.serv.job.recomm.conf.SSRecommConf;
import at.kc.tugraz.ss.serv.job.recomm.impl.SSRecommImpl;
import at.kc.tugraz.ss.serv.job.recomm.serv.task.SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdateTask;
import at.kc.tugraz.ss.serv.job.recomm.serv.task.SSRecommTagsLanguageModelBasedOnUserEntityTagUpdateTask;
import at.kc.tugraz.ss.serv.job.recomm.serv.task.SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdateTask;
import at.kc.tugraz.ss.serv.job.recomm.serv.task.SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryUpdateTask;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;

public class SSRecommServ extends SSServA {
  public static final SSRecommServ inst = new SSRecommServ(SSRecommClientI.class, SSRecommServerI.class);

  protected SSRecommServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSRecommImpl(servConf);
  }

  public void schedule() throws Exception {
    if (!servConf.use) {
      return;
    }
    if (!((SSRecommConf) servConf).initAtStartUp) {
      SSServCaller.recommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdate();
      SSServCaller.recommTagsLanguageModelBasedOnUserEntityTagUpdate();
      SSServCaller.recommTagsThreeLayersBasedOnUserEntityTagCategoryUpdate();
      SSServCaller.recommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdate();
    }
    SSDateU.scheduleAtFixedRate(new SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdateTask(), SSDateU.getDateForTomorrowMorning(), SSDateU.dayInMilliSeconds);
    SSDateU.scheduleAtFixedRate(new SSRecommTagsLanguageModelBasedOnUserEntityTagUpdateTask(), SSDateU.getDateForTomorrowMorning(), SSDateU.dayInMilliSeconds);
    SSDateU.scheduleAtFixedRate(new SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryUpdateTask(), SSDateU.getDateForTomorrowMorning(), SSDateU.dayInMilliSeconds);
    SSDateU.scheduleAtFixedRate(new SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdateTask(), SSDateU.getDateForTomorrowMorning(), SSDateU.dayInMilliSeconds);
  }

  @Override protected void initServSpecificStuff() throws Exception {
    if (!servConf.use || !((SSRecommConf) servConf).initAtStartUp) {
      return;
    }
    SSServCaller.recommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdate();
    SSServCaller.recommTagsLanguageModelBasedOnUserEntityTagUpdate();
    SSServCaller.recommTagsThreeLayersBasedOnUserEntityTagCategoryUpdate();
    SSServCaller.recommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdate();
  }
}