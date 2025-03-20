package at.kc.tugraz.ss.serv.dataimport.impl;
import at.kc.tugraz.socialserver.utils.SSFileU;
import at.kc.tugraz.socialserver.utils.SSLogU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.datatypes.datatypes.SSSpaceEnum;
import at.kc.tugraz.ss.serv.serv.api.SSServConfA;
import at.kc.tugraz.ss.serv.db.api.SSDBGraphI;
import at.kc.tugraz.ss.serv.db.api.SSDBSQLI;
import at.kc.tugraz.ss.datatypes.datatypes.SSLabelStr;
import at.kc.tugraz.ss.serv.dataimport.api.SSDataImportClientI;
import at.kc.tugraz.ss.serv.dataimport.api.SSDataImportServerI;
import at.kc.tugraz.ss.serv.dataimport.conf.SSDataImportConf;
import at.kc.tugraz.ss.serv.dataimport.datatypes.pars.SSDataImportUserResourceTagFromWikipediaPar;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.datatypes.datatypes.SSTagLabel;
import at.kc.tugraz.ss.serv.dataimport.datatypes.pars.SSDataImportEvernotePar;
import at.kc.tugraz.ss.serv.dataimport.datatypes.pars.SSDataImportMediaWikiUserPar;
import at.kc.tugraz.ss.serv.dataimport.datatypes.pars.SSDataImportSSSUsersFromCSVFilePar;
import at.kc.tugraz.ss.serv.dataimport.impl.evernote.SSDataImportEvernoteHelper;
import at.kc.tugraz.ss.serv.dataimport.impl.fct.reader.SSDataImportReaderFct;
import at.kc.tugraz.ss.serv.dataimport.impl.fct.sql.SSDataImportSQLFct;
import at.kc.tugraz.ss.serv.db.datatypes.sql.err.SSSQLDeadLockErr;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import at.kc.tugraz.ss.serv.serv.api.SSServImplWithDBA;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSDataImportImpl extends SSServImplWithDBA implements SSDataImportClientI, SSDataImportServerI {
  private final SSDataImportEvernoteHelper dataImpEvernoteHelper;

  private final SSDataImportSQLFct sqlFct;

  public SSDataImportImpl(final SSServConfA conf, final SSDBGraphI dbGraph, final SSDBSQLI dbSQL) throws Exception {
    super(conf, dbGraph, dbSQL);
    this.dataImpEvernoteHelper = new SSDataImportEvernoteHelper();
    this.sqlFct = new SSDataImportSQLFct(dbSQL);
  }

  @Override public Map<String, String> dataImportSSSUsersFromCSVFile(final SSServPar parA) throws Exception {
    final SSDataImportSSSUsersFromCSVFilePar par = new SSDataImportSSSUsersFromCSVFilePar(parA);
    final List<String[]> lines;
    try {
      lines = SSDataImportReaderFct.readAllFromCSV(SSFileU.dirWorking(), par.fileName);
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
      return null;
    }
    final Map<String, String> passwordPerUser = new HashMap<String, String>();
    try {
      for (String[] line : lines) {
        try {
          passwordPerUser.put(line[0].trim(), line[1].trim());
        } catch (Exception error) {
        }
      }
      return passwordPerUser;
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
      return null;
    }
  }

  @Override public void dataImportEvernote(final SSServPar parA) throws Exception {
    final SSDataImportEvernotePar par = new SSDataImportEvernotePar(parA);
    try {
      dbSQL.startTrans(par.shouldCommit);
      dataImpEvernoteHelper.setBasicEvernoteInfo(par);
      dataImpEvernoteHelper.handleLinkedNotebooks();
      dataImpEvernoteHelper.handleSharedNotebooks();
      dataImpEvernoteHelper.handleNotebooks(par);
      dbSQL.commit(par.shouldCommit);
    } catch (SSSQLDeadLockErr deadLockErr) {
      if (dbSQL.rollBack(parA)) {
        dataImportEvernote(parA);
      } else {
        SSServErrReg.regErrThrow(deadLockErr);
      }
    } catch (Exception error) {
      dbSQL.rollBack(parA);
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override public void dataImportMediaWikiUser(final SSServPar parA) throws Exception {
    final SSDataImportMediaWikiUserPar par = new SSDataImportMediaWikiUserPar(parA);
    final List<String[]> lines;
    try {
      lines = SSDataImportReaderFct.readAllFromCSV(SSFileU.dirWorkingDataCsv(), ((SSDataImportConf) conf).fileName);
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
      return;
    }
    String firstName;
    String familyName;
    String password;
    String userName;
    try {
      dbSQL.startTrans(par.shouldCommit);
      for (String[] line : lines) {
        try {
          firstName = line[0].trim();
          familyName = line[1].trim();
          password = line[2].trim();
          userName = firstName + familyName;
          userName = userName.replaceAll("[^a-zA-Z0-9]+", SSStrU.empty);
          System.out.println(userName);
        } catch (Exception error) {
          continue;
        }
        sqlFct.addUserWithGroup(userName, password);
      }
      dbSQL.commit(par.shouldCommit);
    } catch (SSSQLDeadLockErr deadLockErr) {
      if (dbSQL.rollBack(parA)) {
        dataImportMediaWikiUser(parA);
      } else {
        SSServErrReg.regErrThrow(deadLockErr);
      }
    } catch (Exception error) {
      dbSQL.rollBack(parA);
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override public Boolean dataImportUserResourceTagFromWikipedia(final SSServPar parA) throws Exception {
    final SSDataImportUserResourceTagFromWikipediaPar par = new SSDataImportUserResourceTagFromWikipediaPar(parA);
    int counter = 1;
    int tagCounter = 0;
    BufferedReader lineReader = null;
    List<SSTagLabel> tagList;
    FileInputStream dataImportFileIn;
    String line;
    List<String> lineSplit;
    String userLabel;
    SSUri resource;
    SSUri user;
    String tags;
    String categories;
    Long timestamp;
    try {
      SSServCaller.tagsRemove(null, null, null, SSSpaceEnum.sharedSpace, par.shouldCommit);
      dataImportFileIn = SSFileU.openFileForRead(SSFileU.dirWorkingDataCsv() + ((SSDataImportConf) conf).fileName);
      lineReader = new BufferedReader(new InputStreamReader(dataImportFileIn));
      line = lineReader.readLine();
      while (line != null) {
        line = SSStrU.removeDoubleQuotes(line);
        line = SSStrU.replace(line, SSStrU.percent, SSStrU.empty);
        lineSplit = SSStrU.split(line, SSStrU.semiColon);
        if (lineSplit == null || lineSplit.size() < 5) {
          line = lineReader.readLine();
          continue;
        }
        categories = lineSplit.get(4);
        try {
          resource = SSUri.get(lineSplit.get(1));
        } catch (Exception error) {
          line = lineReader.readLine();
          continue;
        }
        userLabel = lineSplit.get(0);
        timestamp = Long.parseLong(lineSplit.get(2)) * 1000;
        tags = lineSplit.get(3);
        user = SSServCaller.logUserIn(SSLabelStr.get(userLabel), false);
        tagList = SSTagLabel.getDistinct(SSStrU.split(tags, SSStrU.comma));
        tagCounter += tagList.size();
        SSServCaller.addTagsAtCreationTime(user, resource, tagList, SSSpaceEnum.sharedSpace, timestamp, par.shouldCommit);
        SSLogU.info("line " + counter++ + " " + tagCounter + " time : " + new Date().getTime() + " user: " + user.toString() + " tags: " + tags);
        line = lineReader.readLine();
      }
      return true;
    } catch (SSSQLDeadLockErr deadLockErr) {
      if (dbSQL.rollBack(parA)) {
        return dataImportUserResourceTagFromWikipedia(parA);
      } else {
        SSServErrReg.regErrThrow(deadLockErr);
        return null;
      }
    } catch (Exception error) {
      dbSQL.rollBack(parA);
      SSServErrReg.regErrThrow(error);
      return null;
    } finally {
      if (lineReader != null) {
        lineReader.close();
      }
    }
  }
}