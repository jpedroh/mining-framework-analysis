/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest.encoder.metadata;

import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import java.math.BigDecimal;

import org.jdom.Element;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
public class GSDimensionInfoEncoder extends XmlElement{
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/left.java
	final boolean enabled;
||||||| /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/base.java
	final boolean enabled;
=======
	private boolean enabled;
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/right.java
	
	/**
	 * Enum for presentation mode
	 */
	
	public enum Presentation{
		LIST,
		CONTINUOUS_INTERVAL
	}
	
	/**
	 * Enum for presentation mode which needs arguments
	 */
	
	public enum PresentationDiscrete{
		DISCRETE_INTERVAL
	}
	
	/**
	 * Build a dimension
	 * @param enabled enable dimension if true
	 * @note a enabled dimension also need a presentation mode set.
	 */
	
	public GSDimensionInfoEncoder(final boolean enabled) {
		super(DIMENSIONINFO);
		add("enabled", (enabled)?"true":"false");
		this.enabled=enabled;
	}
	
	/**
	 * build an not enabled dimension
	 */
	
	public GSDimensionInfoEncoder() {
		super(DIMENSIONINFO);
		add("enabled", "false");
		this.enabled=Boolean.FALSE;
	}
	
	public final static String DIMENSIONINFO="dimensionInfo";
	
	public final static String RESOLUTION="resolution";
	
	public final static String PRESENTATION="presentation";
	
	/**
	 * Enum for presentation mode
	 */
	
	public void setEnabled(final boolean enabled){
		set("enabled", "true");
		this.enabled=Boolean.TRUE;
	}
	
	/**
	 * @deprecated will be set to protected in the next release {@link setPresentation(final Presentation pres)}
	 * @param pres
	 */
	public void addPresentation(final Presentation pres){
		if (enabled){
			add(PRESENTATION,pres.toString());
		}
	}
	
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/left.java
	public void addPresentation(final PresentationDiscrete pres){
||||||| /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/base.java
	public void addPresentation(final DiscretePresentation pres){
=======
	public void addPresentation(final Presentation pres){
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/right.java
		if (enabled){
			set(PRESENTATION,pres.toString());
			remove(RESOLUTION);
		}
	}
	
	/**
	 * @param pres
	 * @param interval
	 * @deprecated will be set to protected in the next release {@link setPresentation(final PresentationDiscrete pres, final BigDecimal interval)}
	 */
	public void addPresentation(final PresentationDiscrete pres, final BigDecimal interval){
		if (enabled){
			add(PRESENTATION,pres.toString());
			add(RESOLUTION,String.valueOf(interval));	
		}
	}
	
	public void setPresentation(final PresentationDiscrete pres, final BigDecimal interval){
		if (enabled){
			set(PRESENTATION,pres.toString());
			set(RESOLUTION,String.valueOf(interval));	
		}
	}
	
    
    public void add(String nodename, String nodetext) {
    	final Element el=new Element(nodename);
    	el.setText(nodetext);
    	this.addContent(el);
    }
	
	
}
