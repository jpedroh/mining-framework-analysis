/**
* Code contributed to the Learning Layers project
* http://www.learning-layers.eu
* Development is partly funded by the FP7 Programme of the European Commission under
* Grant Agreement FP7-ICT-318209.
* Copyright (c) 2014, Graz University of Technology - KTI (Knowledge Technologies Institute).
* For a list of contributors see the AUTHORS file at the top-level directory of this distribution.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package at.kc.tugraz.ss.serv.auth.impl;

import at.kc.tugraz.socialserver.utils.SSLogU;
import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.ss.adapter.socket.datatypes.SSSocketCon;
import at.kc.tugraz.ss.datatypes.datatypes.entity.SSUri;
import at.kc.tugraz.ss.datatypes.datatypes.enums.SSEntityE;
import at.kc.tugraz.ss.datatypes.datatypes.label.SSLabel;
import at.kc.tugraz.ss.serv.auth.api.SSAuthClientI;
import at.kc.tugraz.ss.serv.auth.api.SSAuthServerI;
import at.kc.tugraz.ss.serv.auth.conf.SSAuthConf;
import at.kc.tugraz.ss.serv.auth.impl.fct.csv.SSAuthMiscFct;
import at.kc.tugraz.ss.serv.auth.impl.fct.sql.SSAuthSQLFct;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.serv.db.api.SSDBGraphI;
import at.kc.tugraz.ss.serv.db.api.SSDBSQLI;
import at.kc.tugraz.ss.serv.db.datatypes.sql.err.SSNoResultFoundErr;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import at.kc.tugraz.ss.serv.serv.api.SSServImplWithDBA;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;
import at.kc.tugraz.ss.serv.serv.datatypes.err.SSServerServNotAvailableErr;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.pars.SSAuthCheckCredPar;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.pars.SSAuthLoadKeysPar;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.pars.SSAuthRegisterUserPar;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.pars.SSAuthUsersFromCSVFileAddPar;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.ret.SSAuthCheckCredRet;
import at.kc.tugraz.ss.service.user.api.SSUserGlobals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SSAuthImpl extends SSServImplWithDBA implements SSAuthClientI , SSAuthServerI {
  private static final List<String>          keys      = new ArrayList<String>();

  private static final String                noAuthKey = "FischersFritzFischtFrischeFische";

  private        final SSAuthSQLFct          sqlFct;

  public SSAuthImpl(final SSAuthConf conf, final SSDBGraphI dbGraph, final SSDBSQLI dbSQL) throws Exception {
    super(conf, dbGraph, dbSQL);
    this.sqlFct = new SSAuthSQLFct(dbSQL);
    keys.add(noAuthKey);
    // wikiauth  = new SSAuthWiki();
  }

  /* SSAuthServClientI */

  @Override
  public void authCheckCred(SSSocketCon sSCon, SSServPar par) throws Exception {
    sSCon.writeRetFullToClient(authCheckCred(par));
  }

  /* SSAuthServServerI */

  //TODO dtheiler: create transactions here as well

  @Override
  public void authUsersFromCSVFileAdd(final SSServPar parA) throws Exception {
    
    try{
      final SSAuthUsersFromCSVFileAddPar par                          = new SSAuthUsersFromCSVFileAddPar(parA);
      final Map<String, String>          passwordsForUsersFromCSVFile = new HashMap<String, String>();
        
      try{
        passwordsForUsersFromCSVFile.putAll(SSServCaller.dataImportSSSUsersFromCSVFile(((SSAuthConf)conf).fileName));
      }catch(SSServerServNotAvailableErr error){
        SSLogU.warn("dataImportSSSUsersFromCSVFile failed | service down");
      }
      
      dbSQL.startTrans(par.shouldCommit);
      
      for(Map.Entry<String, String> passwordForUser : passwordsForUsersFromCSVFile.entrySet()){

        SSServCaller.authRegisterUser(
          SSUserGlobals.systemUser,
          SSLabel.get(passwordForUser.getKey()),
          passwordForUser.getValue(),
          false);
      }
      
      dbSQL.commit(par.shouldCommit);
      
    }catch(Exception error){
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override
  public SSUri authRegisterUser(final SSServPar parA) throws Exception {
    try {
      final SSAuthRegisterUserPar par = new SSAuthRegisterUserPar(parA);
      final SSUri userUri;
      dbSQL.startTrans(par.shouldCommit);
      if (SSServCaller.entityExists(SSEntityE.user, par.label)) {
        userUri = SSServCaller.entityGet(SSEntityE.user, par.label).id;
      } else {
        userUri = SSServCaller.vocURICreate();
        SSServCaller.entityAdd(par.user, userUri, par.label, SSEntityE.user, false);
        SSServCaller.entityEntitiesToCircleAdd(par.user, SSServCaller.entityCircleURIPublicGet(), userUri, false);
      }
      if (!sqlFct.hasKey(userUri)) {
        keys.add(sqlFct.addKey(userUri, SSAuthMiscFct.genKey(SSLabel.toStr(par.label) + par.password)));
      }
      try {
        SSServCaller.collUserRootAdd(userUri, false);
      } catch (SSServerServNotAvailableErr error) {
        SSLogU.warn("collUserRootAdd failed | service down");
      }
      dbSQL.commit(par.shouldCommit);
      return userUri;
    } catch (java.lang.Exception error) {
      SSServErrReg.regErrThrow(error);
      return null;
    }
  }

  @Override
  public SSAuthCheckCredRet authCheckCred(final SSServPar parA) throws Exception {
    final SSAuthCheckCredPar par = new SSAuthCheckCredPar(parA);
    final SSUri userUri;
    switch (((SSAuthConf) (conf)).authType) {
      case noAuth :
        {
          userUri = SSServCaller.authRegisterUser(SSUserGlobals.systemUser, par.label, par.password, true);
          return SSAuthCheckCredRet.get(noAuthKey, userUri, SSMethU.authCheckCred);
        }
      case csvFileAuth :
        {
          try {
            userUri = SSServCaller.entityGet(SSEntityE.user, par.label).id;
          } catch (SSNoResultFoundErr error) {
            throw new Exception("user not registered");
          }
          if (!sqlFct.hasKey(userUri)) {
            throw new Exception("user not registered");
          }
          return SSAuthCheckCredRet.get(SSAuthMiscFct.checkAndGetKey(sqlFct, userUri, par.label, par.password), userUri, SSMethU.authCheckCred);
        }
      default :
        throw new UnsupportedOperationException();
        // case wikiAuth:{
        // TODO get SSAuthWikiConf
        // boolean authUser = wikiauth.authUser(par.user, par.pass, new SSAuthWikiConf());
        // 
        // if (authUser) {
        // 
        // if (SSStrU.containsNot(keylist, alternateKeys[0])) {
        // keylist.add(alternateKeys[0]);
        // }
        // 
        // return alternateKeys[0];
        // }else{
        // Exception ile = new Exception();
        // 
        // throw ile;
        // }
        // }
    }
  }

  @Override
  public void authLoadKeys(final SSServPar parA) throws Exception {
    
    try{
      final SSAuthLoadKeysPar par = new SSAuthLoadKeysPar(parA);
      
      keys.addAll(sqlFct.getKeys());
      
    }catch(Exception error){
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override
  public void authCheckKey(final SSServPar parA) throws Exception {
    
    try{
      
      switch(((SSAuthConf)conf).authType){
        
        case noAuth:{
          
          if(!keys.contains(parA.key)){
            throw new Exception("login key wrong");
          }
          
          break;
        }
          
        case csvFileAuth:{
          
          if(
            parA.key.equals(noAuthKey) ||
            !keys.contains(parA.key)){
            throw new Exception("login key wrong");
          }
        }
      }
    }catch(Exception error){
      SSServErrReg.regErrThrow(error);
    }
  }
}