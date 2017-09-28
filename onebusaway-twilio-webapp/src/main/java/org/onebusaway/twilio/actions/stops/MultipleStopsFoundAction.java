package org.onebusaway.twilio.actions.stops;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.SessionAware;
import org.onebusaway.presentation.services.text.TextModification;
import org.onebusaway.transit_data.model.StopBean;
import org.onebusaway.transit_data.model.StopsWithArrivalsAndDeparturesBean;
/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.onebusaway.twilio.actions.Messages;
import org.onebusaway.twilio.actions.TwilioSupport;
import org.onebusaway.twilio.impl.PhoneArrivalsAndDeparturesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;


@Results({
	  @Result(name="arrivals", location="arrivals-and-departures-for-stop-id", type="chain"),
	  @Result(name = "repeat", location = "multiple-stops-found", type = "chain")
})
public class MultipleStopsFoundAction extends TwilioSupport implements SessionAware {
  
	
  private TextModification _destinationPronunciation;

  private List<String> _stopIds;

  public List<String> getStopIds() {
    return _stopIds;
  }
 
  @Autowired
  public void setDestinationPronunciation(
      @Qualifier("destinationPronunciation") TextModification destinationPronunciation) {
    _destinationPronunciation = destinationPronunciation;
  }


  @Override
  public String execute() throws Exception {
	  
	  Integer navState = (Integer) sessionMap.get("navState");
	    if (navState == null) {
	      navState = DISPLAY_DATA;
	    }

	    if (navState == DISPLAY_DATA) {
	      return displayData();
	    }
	    // navigation options after rendering a stop
	    return doRouting();  
	  
	
  }

private String displayData() {
    
	/*ActionContext context = ActionContext.getContext();
    ValueStack vs = context.getValueStack();
    List<StopBean> stops = (List<StopBean>) vs.findValue("stops");*/
    
    List<StopBean> stops = (List<StopBean>)sessionMap.get("stops");
    
    int index = 1;
    
    addMessage(Messages.MULTIPLE_STOPS_WERE_FOUND);
    
    for( StopBean stop : stops) {
      
      addMessage(Messages.FOR);
      
      String destination = _destinationPronunciation.modify(stop.getName());
      destination = destination.replaceAll("\\&", "and");      
      addText(destination);
      addText(", ");
      
      addMessage(Messages.PLEASE_PRESS);
      
      String key = Integer.toString(index++);
      addText(key);
      addText(". ");
   }

    //addMessage(Messages.HOW_TO_GO_BACK);
    //addAction("\\*", "/back");

    addMessage(Messages.TO_REPEAT);
    
    sessionMap.put("navState", new Integer(DO_ROUTING));
    
    return SUCCESS;
  }

  private String doRouting() {
    sessionMap.put("navState", new Integer(DISPLAY_DATA));
   
    /*ActionContext context = ActionContext.getContext();
    ValueStack vs = context.getValueStack();
    List<StopBean> stops = (List<StopBean>) vs.findValue("stops");*/
    
    List<StopBean> stops = (List<StopBean>)sessionMap.get("stops");
    
    for(int i = 0; i < stops.size(); i++){
    
	    if (String.valueOf(i + 1).equals(getInput())) {
	    	StopBean stop = stops.get(i);
	    	_stopIds = Arrays.asList(stop.getId());
	    	return "arrivals";
	    } else if (REPEAT_MENU_ITEM.equals(getInput())) {
	      return "repeat";
	    } 
    }
   
    return INPUT;
  }
}
