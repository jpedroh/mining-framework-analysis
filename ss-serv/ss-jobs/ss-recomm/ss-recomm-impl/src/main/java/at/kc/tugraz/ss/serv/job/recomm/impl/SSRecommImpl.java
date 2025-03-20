package at.kc.tugraz.ss.serv.job.recomm.impl;
import at.kc.tugraz.socialserver.utils.SSFileExtU;
import at.kc.tugraz.socialserver.utils.SSLogU;
import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.ss.adapter.socket.datatypes.SSSocketCon;
import at.kc.tugraz.ss.serv.serv.api.SSServConfA;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import at.kc.tugraz.ss.serv.job.recomm.api.SSRecommClientI;
import at.kc.tugraz.ss.serv.job.recomm.api.SSRecommServerI;
import at.kc.tugraz.ss.serv.job.recomm.conf.SSRecommConf;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdatePar;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsLanguageModelUpdateBasedOnUserEntityTagPar;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampPar;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsLanguageModelBasedOnUserEntityTagPar;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryPar;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampPar;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdatePar;
import at.kc.tugraz.ss.serv.job.recomm.datatypes.par.SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryUpdatePar;
import at.kc.tugraz.ss.serv.job.recomm.impl.engine.BaseLevelLearningEngine;
import at.kc.tugraz.ss.serv.job.recomm.impl.engine.LanguageModelEngine;
import at.kc.tugraz.ss.serv.job.recomm.impl.engine.ThreeLayersEngine;
import at.kc.tugraz.ss.serv.job.recomm.impl.fct.misc.SSRecommFct;
import at.kc.tugraz.ss.serv.serv.api.SSServImplMiscA;
import at.kc.tugraz.ss.service.tag.datatypes.SSTag;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SSRecommImpl extends SSServImplMiscA implements SSRecommClientI, SSRecommServerI {
  private static final BaseLevelLearningEngine recommenderTagBaseLevelLearningBasedOnUserEntityTagTimestamp = new BaseLevelLearningEngine();

  private static final LanguageModelEngine recommenderTagLanguageModelBasedOnUserEntityTag = new LanguageModelEngine();

  private static final ThreeLayersEngine recommenderTagThreeLayersBasedOnUserEntityTagCategory = new ThreeLayersEngine();

  private static final ThreeLayersEngine recommenderTagThreeLayersBasedOnUserEntityTagCategoryTimestamp = new ThreeLayersEngine();

  public SSRecommImpl(final SSServConfA conf) throws Exception {
    super(conf);
  }

  @Override public void recommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdate(final SSServPar parA) throws Exception {
    final SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdatePar par = new SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampUpdatePar(parA);
    final SSRecommConf recommConf = (SSRecommConf) conf;
    try {
      if (!SSRecommFct.exportEntityTagTimestampCombinationsForAllUsers(recommConf.fileNameForOpRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestamp)) {
        return;
      }
      try {
        recommenderTagBaseLevelLearningBasedOnUserEntityTagTimestamp.loadFile(SSStrU.removeTrailingString(recommConf.fileNameForOpRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestamp, SSStrU.dot + SSFileExtU.txt));
      } catch (FileNotFoundException errFileNotFound) {
        SSLogU.warn("file not found for recommBaseLevelLearningWithContextUpdate");
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override public List<SSTag> recommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestamp(final SSServPar parA) throws Exception {
    final SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampPar par = new SSRecommTagsBaseLevelLearningWithContextBasedOnUserEntityTagTimestampPar(parA);
    try {
      final List<SSTag> tags = new ArrayList<SSTag>();
      tags.addAll(SSTag.getDistinct(recommenderTagBaseLevelLearningBasedOnUserEntityTagTimestamp.getTags(SSUri.toStr(par.forUser), SSUri.toStr(par.entityUri), par.maxTags)));
      SSLogU.info("base level learning with context recommends tags: " + tags.toString());
      return tags;
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
      return null;
    }
  }

  @Override public void recommTagsLanguageModelBasedOnUserEntityTagUpdate(final SSServPar parA) throws Exception {
    final SSRecommTagsLanguageModelUpdateBasedOnUserEntityTagPar par = new SSRecommTagsLanguageModelUpdateBasedOnUserEntityTagPar(parA);
    final SSRecommConf recommConf = (SSRecommConf) conf;
    try {
      if (!SSRecommFct.exportEntityTagCombinationsForAllUsers(recommConf.fileNameForOpRecommTagsLanguageModelBasedOnUserEntityTag)) {
        return;
      }
      try {
        recommenderTagLanguageModelBasedOnUserEntityTag.loadFile(SSStrU.removeTrailingString(recommConf.fileNameForOpRecommTagsLanguageModelBasedOnUserEntityTag, SSStrU.dot + SSFileExtU.txt));
      } catch (FileNotFoundException errFileNotFound) {
        SSLogU.warn("file not found for recommLanguageModelUpdate");
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override public List<SSTag> recommTagsLanguageModelBasedOnUserEntityTag(final SSServPar parA) throws Exception {
    final SSRecommTagsLanguageModelBasedOnUserEntityTagPar par = new SSRecommTagsLanguageModelBasedOnUserEntityTagPar(parA);
    try {
      final List<SSTag> tags = new ArrayList<SSTag>();
      tags.addAll(SSTag.getDistinct(recommenderTagLanguageModelBasedOnUserEntityTag.getTags(SSUri.toStr(par.forUser), SSUri.toStr(par.entityUri), par.maxTags)));
      SSLogU.info("language model recommends tags: " + tags.toString());
      return tags;
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
      return null;
    }
  }

  @Override public void recommTagsThreeLayersBasedOnUserEntityTagCategoryUpdate(final SSServPar parA) throws Exception {
    final SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryUpdatePar par = new SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryUpdatePar(parA);
    final SSRecommConf recommConf = (SSRecommConf) conf;
    try {
      if (!SSRecommFct.exportEntityTagCategoryCombinationsForAllUsers(recommConf.fileNameForOpRecommTagsThreeLayersBasedOnUserEntityTagCategory)) {
        return;
      }
      try {
        recommenderTagThreeLayersBasedOnUserEntityTagCategory.loadFile(SSStrU.removeTrailingString(recommConf.fileNameForOpRecommTagsThreeLayersBasedOnUserEntityTagCategory, SSStrU.dot + SSFileExtU.txt));
      } catch (FileNotFoundException errFileNotFound) {
        SSLogU.warn("file not found for recommThreeLayersUpdate");
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override public List<SSTag> recommTagsThreeLayersBasedOnUserEntityTagCategory(final SSServPar parA) throws Exception {
    final SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryPar par = new SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryPar(parA);
    try {
      final List<SSTag> tags = new ArrayList<SSTag>();
      tags.addAll(SSTag.getDistinct(recommenderTagThreeLayersBasedOnUserEntityTagCategory.getTags(SSUri.toStr(par.forUser), SSUri.toStr(par.entityUri), par.categories, par.maxTags, false)));
      SSLogU.info("three layers recommends tags: " + tags.toString());
      return tags;
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
      return null;
    }
  }

  @Override public void recommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdate(final SSServPar parA) throws Exception {
    final SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdatePar par = new SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampUpdatePar(parA);
    final SSRecommConf recommConf = (SSRecommConf) conf;
    try {
      if (!SSRecommFct.exportEntityTagCategoryTimestampCombinationsForAllUsers(recommConf.fileNameForOpRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestamp)) {
        return;
      }
      try {
        recommenderTagThreeLayersBasedOnUserEntityTagCategoryTimestamp.loadFile(SSStrU.removeTrailingString(recommConf.fileNameForOpRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestamp, SSStrU.dot + SSFileExtU.txt));
      } catch (FileNotFoundException errFileNotFound) {
        SSLogU.warn("file not found for recommThreeLayersUpdate");
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override public List<SSTag> recommTagsThreeLayersBasedOnUserEntityTagCategoryTimestamp(final SSServPar parA) throws Exception {
    final SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampPar par = new SSRecommTagsThreeLayersBasedOnUserEntityTagCategoryTimestampPar(parA);
    try {
      final List<SSTag> tags = new ArrayList<SSTag>();
      tags.addAll(SSTag.getDistinct(recommenderTagThreeLayersBasedOnUserEntityTagCategoryTimestamp.getTags(SSUri.toStr(par.forUser), SSUri.toStr(par.entityUri), par.categories, par.maxTags, true)));
      SSLogU.info("three layers with time recommends tags: " + tags.toString());
      return tags;
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
      return null;
    }
  }
}