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
package at.kc.tugraz.ss.service.disc.datatypes.pars;

import at.kc.tugraz.socialserver.utils.SSVarU;
import at.kc.tugraz.ss.datatypes.datatypes.SSTextComment;
import at.kc.tugraz.ss.datatypes.datatypes.entity.SSUri;
import at.kc.tugraz.ss.datatypes.datatypes.enums.SSEntityE;
import at.kc.tugraz.ss.datatypes.datatypes.label.SSLabel;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;


public class SSDiscUserEntryAddPar extends SSServPar {
  public SSUri disc = null;

  public SSUri entity = null;

  public SSTextComment entry = null;

  public Boolean             addNewDisc     = null;

  public SSEntityE type = null;

  public SSLabel label = null;

  public SSTextComment       explanation    = null;

  public SSDiscUserEntryAddPar(SSServPar par) throws Exception {
    super(par);
    try {
      if (pars != null) {
        disc = ((SSUri) (pars.get(SSVarU.disc)));
        entity = ((SSUri) (pars.get(SSVarU.entity)));
        entry = ((SSTextComment) (pars.get(SSVarU.entry)));
        addNewDisc = ((Boolean) (pars.get(SSVarU.addNewDisc)));
        type = ((SSEntityE) (pars.get(SSVarU.type)));
        label = ((SSLabel) (pars.get(SSVarU.label)));
        explanation = ((SSTextComment) (pars.get(SSVarU.explanation)));
      }
      if (clientPars != null) {
        try {
          entity = SSUri.get(clientPars.get(SSVarU.entity));
        } catch (java.lang.Exception error) {
        }
        try {
          disc = SSUri.get(clientPars.get(SSVarU.disc));
        } catch (java.lang.Exception error) {
        }
        try {
          addNewDisc = Boolean.valueOf(clientPars.get(SSVarU.addNewDisc));
        } catch (java.lang.Exception error) {
        }
        try {
          entry = SSTextComment.get(clientPars.get(SSVarU.entry));
        } catch (java.lang.Exception error) {
        }
        try {
          type = SSEntityE.get(clientPars.get(SSVarU.type));
        } catch (java.lang.Exception error) {
        }
        try {
          label = SSLabel.get(clientPars.get(SSVarU.label));
        } catch (java.lang.Exception error) {
        }
        try {
          explanation = SSTextComment.get(clientPars.get(SSVarU.explanation));
        } catch (java.lang.Exception error) {
        }
      }
    } catch (java.lang.Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }
}