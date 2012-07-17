/*
 	Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 	
 	This file is part of OpenPnP.
 	
	OpenPnP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenPnP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenPnP.  If not, see <http://www.gnu.org/licenses/>.
 	
 	For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.util;

import org.openpnp.model.Outline;
import org.openpnp.model.Point;


public class Utils2D {
	// TODO: Move these into their respective classes.
	public static Outline rotateTranslateScaleOutline(Outline outline, double c, double x, double y, double scale) {
		Outline newOutline = new Outline();
		newOutline.setUnits(outline.getUnits());
		for (int i = 0; i < outline.getPoints().size(); i++) {
			Point p = outline.getPoints().get(i);
			
			p = rotateTranslateScalePoint(p, c, x, y, scale);
			
			newOutline.addPoint(p.getX(), p.getY());
		}
		
		return newOutline;
	}
	
	public static Point rotateTranslateScalePoint(Point point, double c, double x, double y, double scale) {
		point = rotatePoint(point, c);
		point = translatePoint(point, x, y);
		point = scalePoint(point, scale);
		return point;
	}
	
	public static Point translatePoint(Point point, double x, double y) {
		return new Point(point.getX() + x, point.getY() + y);
	}
	
	public static Point rotatePoint(Point point, double c) {
		double x = point.getX();
		double y = point.getY();
		
		// convert degrees to radians
		c = c * Math.PI / 180.0;
		c *= -1;
		
		// rotate the points
		double xn = x * Math.cos(c) - y * Math.sin(c);
		double yn = x * Math.sin(c) + y * Math.cos(c);
		
		x = xn;
		y = yn;
		
		return new Point(x, y);
	}
	
	public static Point scalePoint(Point point, double scale) {
		return new Point(point.getX() * scale, point.getY() * scale);
	}
}
