/*
 * Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 * 
 * This file is part of OpenPnP.
 * 
 * OpenPnP is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * OpenPnP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenPnP. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * For more information about OpenPnP visit http://openpnp.org
 *
 * Changelog: 03/10/2012 Ami: Add rotate using center point
 */

package org.openpnp.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.openpnp.model.Board.Side;
import org.openpnp.model.BoardLocation;
import org.openpnp.model.Length;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.model.Placement;
import org.openpnp.model.Point;
import org.pmw.tinylog.Logger;


public class Utils2D {
    public static Point rotateTranslateScalePoint(Point point, double c, double x, double y,
            double scaleX, double scaleY) {
        point = rotatePoint(point, c);
        point = translatePoint(point, x, y);
        point = scalePoint(point, scaleX, scaleY);
        return point;
    }

    public static Point rotateTranslateCenterPoint(Point point, double c, double x, double y,
            Point center) {
        point = translatePoint(point, center.getX() * -1, center.getY() * -1);
        point = rotatePoint(point, c);
        point = translatePoint(point, center.getX(), center.getY());
        point = translatePoint(point, x, y);

        return point;
    }

    public static Point translatePoint(Point point, double x, double y) {
        return new Point(point.getX() + x, point.getY() + y);
    }

    /**
     * Rotation is counter-clockwise for positive angles.
     * 
     * @param point
     * @param c
     * @return
     */
    public static Point rotatePoint(Point point, double c) {
        double x = point.getX();
        double y = point.getY();

        // convert degrees to radians
        c = Math.toRadians(c);

        // rotate the points
        double xn = x * Math.cos(c) - y * Math.sin(c);
        double yn = x * Math.sin(c) + y * Math.cos(c);

        x = xn;
        y = yn;

        return new Point(x, y);
    }

    public static Point scalePoint(Point point, double scaleX, double scaleY) {
        return new Point(point.getX() * scaleX, point.getY() * scaleY);
    }

    public static Location calculateBoardPlacementLocation(BoardLocation bl,
            Location placementLocation) {
        if (bl.getPlacementTransform() != null) {
            AffineTransform tx = bl.getPlacementTransform();
            // The affine calculations are always done in millimeters, so we convert everything
            // before we start calculating and then we'll convert it back to the original
            // units at the end.
            LengthUnit placementUnits = placementLocation.getUnits();
            Location boardLocation = bl.getLocation().convertToUnits(LengthUnit.Millimeters);
            placementLocation = placementLocation.convertToUnits(LengthUnit.Millimeters);

            if (bl.getSide() == Side.Bottom) {
                placementLocation = placementLocation.invert(true, false, false, false);
            }
            
            // Calculate the apparent angle from the transform. We need this because when we
            // created the transform we captured the apparent angle and that is used to position
            // in X, Y, but we also need the actual value to add to the placement rotation so that
            // the nozzle is rotated to the correct angle as well.
            // Note, there is probably a better way to do this. If you know how, please let me know!
            Point2D.Double a = new Point2D.Double(0, 0);
            Point2D.Double b = new Point2D.Double(1, 1);
            Point2D.Double c = new Point2D.Double(0, 0);
            Point2D.Double d = new Point2D.Double(1, 1);
            c = (Point2D.Double) tx.transform(c, null);
            d = (Point2D.Double) tx.transform(d, null);
            double angle = Math.toDegrees(Math.atan2(d.y - c.y, d.x - c.x) - Math.atan2(b.y - a.y, b.x - a.x));
            
            Point2D p = new Point2D.Double(placementLocation.getX(), placementLocation.getY());
            p = tx.transform(p, null);
            
            // The final result is the transformed X,Y, the BoardLocation's Z, and the
            // transform angle + placement angle.
            Location l = new Location(LengthUnit.Millimeters, 
                    p.getX(), 
                    p.getY(), 
                    boardLocation.getZ(), 
                    angle + placementLocation.getRotation());
            l = l.convertToUnits(placementUnits);
            return l;
        }
        else {
            return calculateBoardPlacementLocation(bl.getLocation(), bl.getSide(),
                    bl.getBoard().getDimensions().getX(), placementLocation);
        }
    }

    public static Location calculateBoardPlacementLocation(Location boardLocation, Side side,
            double offset, Location placementLocation) {
            // The Z value of the placementLocation is always ignored, so zero it out to make sure.
            placementLocation = placementLocation.derive(null, null, 0D, null);


            // We will work in the units of the placementLocation, so convert
            // anything that isn't in those units to it.
        boardLocation = boardLocation.convertToUnits(placementLocation.getUnits());

            // If we are placing the bottom of the board we need to invert
            // the placement location.
        if (side == Side.Bottom) {
                placementLocation = placementLocation.invert(true, false, false, false)
                    .add(new Location(placementLocation.getUnits(), offset, 0.0, 0.0, 0.0));
            }

            // Rotate and translate the point into the same coordinate space
            // as the board
            placementLocation = placementLocation.rotateXy(boardLocation.getRotation())
                    .addWithRotation(boardLocation);
            return placementLocation;
        }


    public static Location calculateBoardPlacementLocationInverse(BoardLocation bl,
            Location placementLocation) {
        if (bl.getPlacementTransform() != null) {
            AffineTransform tx = bl.getPlacementTransform();
            
            try {
                tx = tx.createInverse();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            // The affine calculations are always done in millimeters, so we convert everything
            // before we start calculating and then we'll convert it back to the original
            // units at the end.
            LengthUnit placementUnits = placementLocation.getUnits();
            Location boardLocation = bl.getLocation().convertToUnits(LengthUnit.Millimeters);
            placementLocation = placementLocation.convertToUnits(LengthUnit.Millimeters);

            if (bl.getSide() == Side.Bottom) {
                placementLocation = placementLocation.invert(true, false, false, false);
            }
            
            // Calculate the apparent angle from the transform. We need this because when we
            // created the transform we captured the apparent angle and that is used to position
            // in X, Y, but we also need the actual value to add to the placement rotation so that
            // the nozzle is rotated to the correct angle as well.
            // Note, there is probably a better way to do this. If you know how, please let me know!
            Point2D.Double a = new Point2D.Double(0, 0);
            Point2D.Double b = new Point2D.Double(1, 1);
            Point2D.Double c = new Point2D.Double(0, 0);
            Point2D.Double d = new Point2D.Double(1, 1);
            c = (Point2D.Double) tx.transform(c, null);
            d = (Point2D.Double) tx.transform(d, null);
            double angle = Math.toDegrees(Math.atan2(d.y - c.y, d.x - c.x) - Math.atan2(b.y - a.y, b.x - a.x));
            
            Point2D p = new Point2D.Double(placementLocation.getX(), placementLocation.getY());
            p = tx.transform(p, null);
            
            // The final result is the transformed X,Y, the BoardLocation's Z, and the
            // transform angle + placement angle.
            Location l = new Location(LengthUnit.Millimeters, 
                    p.getX(), 
                    p.getY(), 
                    boardLocation.getZ(), 
                    angle + placementLocation.getRotation());
            l = l.convertToUnits(placementUnits);
            return l;
        }
        return calculateBoardPlacementLocationInverse(bl.getLocation(),
                bl.getSide(), bl.getBoard().getDimensions().getX(),
                placementLocation);
    }

    public static Location calculateBoardPlacementLocationInverse(Location boardLocation, Side side,
            double offset, Location placementLocation) {
        // inverse steps of calculateBoardPlacementLocation
        boardLocation = boardLocation.convertToUnits(placementLocation.getUnits());
        placementLocation = placementLocation.subtractWithRotation(boardLocation)
                .rotateXy(-boardLocation.getRotation());
        if (side == Side.Bottom) {
            placementLocation = placementLocation.invert(true, false, false, false)
                    .add(new Location(placementLocation.getUnits(), offset, 0.0, 0.0, 0.0));
        }
        // The Z value of the placementLocation is always ignored, so zero it out to make sure.
        placementLocation = placementLocation.derive(null, null, 0D, null);
        return placementLocation;
    }

    /**
     * Given an existing BoardLocation, two Placements and the observed location of those
     * two Placements, calculate the actual Location of the BoardLocation. Note that the
     * BoardLocation is only used to determine which side of the board the Placements
     * are on - it's existing Location is not considered. The returned Location is the
     * absolute Location of the board, including it's angle, with the Z value set to the
     * Z value in the input BoardLocation.
     * 
     * TODO Replace this with deriveAffineTransform. This is essentially a two fiducial check.
     * 
     * @param boardLocation
     * @param placementA
     * @param placementB
     * @param observedLocationA
     * @param observedLocationB
     * @return
     */
    public static Location calculateBoardLocation(BoardLocation boardLocation, Placement placementA,
            Placement placementB, Location observedLocationA, Location observedLocationB) {
        // Create a new BoardLocation based on the input except with a zeroed
        // Location. This will be used to calculate our ideal placement locations.
        BoardLocation bl = new BoardLocation(boardLocation.getBoard());
        bl.setLocation(new Location(boardLocation.getLocation().getUnits()));
        bl.setSide(boardLocation.getSide());

        // Calculate the ideal placement locations. This is where we would expect the
        // placements to be if the board was at 0,0,0,0.
        Location idealA = calculateBoardPlacementLocation(bl, placementA.getLocation());
        Location idealB = calculateBoardPlacementLocation(bl, placementB.getLocation());

        // Just rename a couple variables to make the code easier to read.
        Location actualA = observedLocationA;
        Location actualB = observedLocationB;

        // Make sure all locations are using the same units.
        idealA = idealA.convertToUnits(boardLocation.getLocation().getUnits());
        idealB = idealB.convertToUnits(boardLocation.getLocation().getUnits());
        actualA = actualA.convertToUnits(boardLocation.getLocation().getUnits());
        actualB = actualB.convertToUnits(boardLocation.getLocation().getUnits());

        // Calculate the angle that we expect to see between the two placements
        double idealAngle = Math.toDegrees(
                Math.atan2(idealB.getY() - idealA.getY(), idealB.getX() - idealA.getX()));
        // Now calculate the angle that we observed between the two placements
        double actualAngle = Math.toDegrees(
                Math.atan2(actualB.getY() - actualA.getY(), actualB.getX() - actualA.getX()));

        // The difference in angles is the angle of the board
        double angle = actualAngle - idealAngle;

        // Now we rotate the first placement by the angle, which gives us the location
        // that the placement would be had the board been rotated by that angle.
        Location idealARotated = idealA.rotateXy(angle);

        // And now we subtract that rotated location from the observed location to get
        // the real offset of the board.
        Location location = actualA.subtract(idealARotated);

        // And set the calculated angle and original Z
        location = location.derive(null, null,
                boardLocation.getLocation().convertToUnits(location.getUnits()).getZ(), angle);

        return location;
    }


    public static double normalizeAngle(double angle) {
        while (angle > 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }
    
    /**
     * Calculate the Location along the line formed by a and b with distance from a.
     * @param a
     * @param b
     * @param distance
     * @return
     */
    static public Location getPointAlongLine(Location a, Location b, Length distance) {
        b = b.convertToUnits(a.getUnits());
        distance = distance.convertToUnits(a.getUnits());
        
        Point vab = b.subtract(a).getXyPoint();
        double lab = a.getLinearDistanceTo(b);
        Point vu = new Point(vab.x / lab, vab.y / lab);
        vu = new Point(vu.x * distance.getValue(), vu.y * distance.getValue());
        return a.add(new Location(a.getUnits(), vu.x, vu.y, 0, 0));
    }

    static public double getAngleFromPoint(Location firstPoint, Location secondPoint) {
        secondPoint = secondPoint.convertToUnits(firstPoint.getUnits());
        
        double angle = 0.0;
        // above 0 to 180 degrees
        if ((secondPoint.getX() > firstPoint.getX())) {
            angle = (Math.atan2((secondPoint.getX() - firstPoint.getX()),
                    (firstPoint.getY() - secondPoint.getY())) * 180 / Math.PI);
        }
        // above 180 degrees to 360/0
        else if ((secondPoint.getX() <= firstPoint.getX())) {
            angle = 360 - (Math.atan2((firstPoint.getX() - secondPoint.getX()),
                    (firstPoint.getY() - secondPoint.getY())) * 180 / Math.PI);
        }
        return angle;
    }
    
    public static double distance(Point2D.Double a, Point2D.Double b) {
        return (Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2)));
    }

    public static void testComputeScalingRotationAndTranslation() {
        for (int t=0; t<10; t++) {
            double eps = 0.0;
            int nDims = 2;
            int nPoints = 3; //(int) Math.round(1.0+10.0*Math.random());
            RealMatrix source = MatrixUtils.createRealMatrix(nDims,nPoints);
            RealMatrix noise = MatrixUtils.createRealMatrix(nDims,nPoints);
            //RealMatrix destination = MatrixUtils.createRealMatrix(nDims,nPoints);
            RealMatrix scaling = MatrixUtils.createRealIdentityMatrix(nDims);
            RealMatrix rotation = MatrixUtils.createRealMatrix(nDims,nDims);
            RealMatrix translation = MatrixUtils.createRealMatrix(nDims,1);
            for (int i=0; i<nDims; i++) {
                double so = 500.0*(Math.random()-0.5);
                for (int j=0; j<nPoints; j++) {
                    source.setEntry(i, j, 500.0*(Math.random()-0.5)+so);
                    noise.setEntry(i, j, eps*(Math.random()-0.5));
                }
                scaling.setEntry(i, i, 1.0+0.01*(Math.random()-0.5));
                translation.setEntry(i, 0, 150.0*(Math.random()-0.5));
            }

            double theta = Math.PI * Math.random();
            rotation.setColumn(0, new double[] {Math.cos(theta), Math.sin(theta)});
            rotation.setColumn(1, new double[] {-Math.sin(theta), Math.cos(theta)});
            
            RealMatrix destination = rotation.multiply(scaling).multiply(source.add(noise));
            for (int j=0; j<nPoints; j++) {
                destination.setColumnMatrix(j, destination.getColumnMatrix(j).add(translation));
            }

            RealMatrix sc = MatrixUtils.createRealMatrix(nDims,nDims);
            RealMatrix rot = MatrixUtils.createRealMatrix(nDims,nDims);
            RealMatrix tr = MatrixUtils.createRealMatrix(nDims,1);
            
            computeScalingRotationAndTranslation(source, destination, sc, rot, tr);
            
            RealMatrix test = rot.multiply(sc).multiply(source);
            for (int i=0; i<nPoints; i++) {
                test.setColumnMatrix(i, test.getColumnMatrix(i).add(tr));
            }
            test = test.subtract(destination);
            double rmsError = Math.sqrt(test.multiply(test.transpose()).getTrace() / nPoints);
            Logger.trace("RMS Error = " + rmsError);
            
            Logger.trace("scaling error = " + sc.subtract(scaling) );
            Logger.trace("rotation error = " + rot.subtract(rotation) );
            Logger.trace("translation error = " + tr.subtract(translation) );
            
            

        }
    }
    
    public static void computeScalingRotationAndTranslation(RealMatrix source, RealMatrix destination, RealMatrix scaling, RealMatrix rotation, RealMatrix translation) {
        int nPoints = source.getColumnDimension();
        int nDims = source.getRowDimension();

        RealMatrix sMat = source.copy();
        RealMatrix dMat = destination.copy();

        RealMatrix sMean = MatrixUtils.createRealMatrix(nDims,1);
        RealMatrix dMean = MatrixUtils.createRealMatrix(nDims,1);

        for (int i=0; i<nPoints; i++) {
            sMean = sMean.add(sMat.getColumnMatrix(i));
            dMean = dMean.add(dMat.getColumnMatrix(i));
        }
        sMean = sMean.scalarMultiply(1.0/nPoints);
        dMean = dMean.scalarMultiply(1.0/nPoints);
        
        for (int i=0; i<nPoints; i++) {
            sMat.setColumnMatrix(i, sMat.getColumnMatrix(i).subtract(sMean));
            dMat.setColumnMatrix(i, dMat.getColumnMatrix(i).subtract(dMean));
        }        
        
        SingularValueDecomposition sSVD = new SingularValueDecomposition(sMat.multiply(sMat.transpose()));
        SingularValueDecomposition dSVD = new SingularValueDecomposition(dMat.multiply(dMat.transpose()));
        RealMatrix sS = sSVD.getS();
        RealMatrix dS = dSVD.getS();

        ArrayList<Integer> p = new ArrayList<>();
        for (int i=0; i<nDims; i++) {
            p.add(i);
        }
        ArrayList<ArrayList<Integer>> permutations = createPermutations(p);
        
        double bestRmsError = 1e99;
        
        for (ArrayList<Integer> perm : permutations) {
            RealMatrix sc = MatrixUtils.createRealIdentityMatrix(nDims);
    
            for (int i=0; i<nDims; i++) {
                if (sS.getEntry(i,i) > 1e-9) {
                    sc.setEntry(i, i, Math.sqrt(dS.getEntry(perm.get(i),perm.get(i))/sS.getEntry(i,i)));
                } 
            }
            sc = sc.multiply(sSVD.getVT()).preMultiply(sSVD.getV());
            
            sMat = sMat.preMultiply(sc);
    
//            SingularValueDecomposition newsSVD = new SingularValueDecomposition(sMat.multiply(sMat.transpose()));
            
            SingularValueDecomposition dstSVD = new SingularValueDecomposition(dMat.multiply(sMat.transpose()));
            
            RealMatrix rot = dstSVD.getU().multiply(dstSVD.getVT());
            if ((new LUDecomposition(rot).getDeterminant()) < 0) {
                RealMatrix s = MatrixUtils.createRealIdentityMatrix(nDims);
                s.multiplyEntry(nDims-1, nDims-1, -1);
                rot = dstSVD.getU().multiply(s).multiply(dstSVD.getVT());
            }
            
            RealMatrix tr = dMean.subtract(rot.multiply(sc).multiply(sMean));
     
            RealMatrix test = rot.multiply(sc).multiply(source);
            for (int i=0; i<nPoints; i++) {
                test.setColumnMatrix(i, test.getColumnMatrix(i).add(tr));
            }
            test = destination.subtract(test);
            double rmsError = Math.sqrt(test.multiply(test.transpose()).getTrace() / nPoints);
            
            if (rmsError < bestRmsError) {
                bestRmsError = rmsError;
                scaling.setSubMatrix(sc.getData(), 0, 0);
                rotation.setSubMatrix(rot.getData(), 0, 0);
                translation.setSubMatrix(tr.getData(), 0, 0);
            }
        }
    }

    private static ArrayList<ArrayList<Integer>> createPermutations(ArrayList<Integer> d) {
        ArrayList<ArrayList<Integer>> p = new ArrayList<>();
        int n = d.size();
        if (n == 1) {
            p.add( d );
        } else if (n == 2) {
            p.add( d );
            ArrayList<Integer> dd = new ArrayList<>();
            dd.add(d.get(1));
            dd.add(d.get(0));
            p.add( dd );
        } else {
            for (int i=0; i<n; i++) {
                ArrayList<Integer> dr = (ArrayList<Integer>) d.clone();
                dr.remove(i);
                ArrayList<ArrayList<Integer>> pe = createPermutations(dr);
                
                for (int j=0; j<pe.size(); j++) {
                    ArrayList<Integer> q = new ArrayList<>();
                    q.add(d.get(i));
                    q.addAll(pe.get(j));
                    p.add(q);
                }
            }
        }
        return p;
    }
    
    public static AffineTransform deriveAffineTransform(RealMatrix scaling, RealMatrix rotation, RealMatrix translation ) {
        RealMatrix rs = rotation.multiply(scaling);
        
        double m00 = rs.getEntry(0, 0);
        double m01 = rs.getEntry(0, 1);
        double m02 = translation.getEntry(0, 0);
        double m10 = rs.getEntry(1, 0);
        double m11 = rs.getEntry(1, 1);
        double m12 = translation.getEntry(1, 0);

        return new AffineTransform(m00, m10, m01, m11, m02, m12);       
    }
    
    public static AffineTransform deriveAffineTransform(double[][] source, double[][] destination) {
        
        RealMatrix sourceMat = MatrixUtils.createRealMatrix(source);
        RealMatrix destMat = MatrixUtils.createRealMatrix(destination);
        
        RealMatrix scaling = MatrixUtils.createRealMatrix(2, 2);
        RealMatrix rotation = MatrixUtils.createRealMatrix(2, 2);
        RealMatrix translation = MatrixUtils.createRealMatrix(2, 1);
        
        computeScalingRotationAndTranslation(sourceMat, destMat, scaling, rotation, translation);

        return deriveAffineTransform(scaling, rotation, translation);       
    }
    
    public static AffineTransform deriveAffineTransform(ArrayList<Point2D.Double> source, ArrayList<Point2D.Double> destination) {
        int nPoints = source.size();
        double[][] s = new double[2][nPoints];
        double[][] d = new double[2][nPoints];
        
        for (int i=0; i<nPoints; i++) {
            Point2D.Double p = source.get(i);
            s[0][i] = p.x;
            s[1][i] = p.y;
            
            p = destination.get(i);
            d[0][i] = p.x;
            d[1][i] = p.y;
        }

        return deriveAffineTransform(s, d);       
    }  
    
    
    // https://stackoverflow.com/questions/21270892/generate-affinetransform-from-3-points
    public static AffineTransform deriveAffineTransform(
            double sourceX1, double sourceY1,
            double sourceX2, double sourceY2,
            double sourceX3, double sourceY3,
            double destX1, double destY1,
            double destX2, double destY2,
            double destX3, double destY3) {
        RealMatrix source = MatrixUtils.createRealMatrix(new double[][] {
            {sourceX1, sourceX2, sourceX3}, 
            {sourceY1, sourceY2, sourceY3}, 
            {1, 1, 1}
        });

        RealMatrix dest = MatrixUtils.createRealMatrix(new double[][] { 
            {destX1, destX2, destX3}, 
            {destY1, destY2, destY3} 
        });

        RealMatrix inverse = new LUDecomposition(source).getSolver().getInverse();
        RealMatrix transform = dest.multiply(inverse);

        double m00 = transform.getEntry(0, 0);
        double m01 = transform.getEntry(0, 1);
        double m02 = transform.getEntry(0, 2);
        double m10 = transform.getEntry(1, 0);
        double m11 = transform.getEntry(1, 1);
        double m12 = transform.getEntry(1, 2);

        return new AffineTransform(m00, m10, m01, m11, m02, m12);       
    }  
    
    // Best keywords: transformation matrix between two line segments
    // https://stackoverflow.com/questions/42328398/transformation-matrix-between-two-line-segments
    public static AffineTransform deriveAffineTransform(
            double sourceX1, double sourceY1,
            double sourceX2, double sourceY2,
            double destX1, double destY1,
            double destX2, double destY2) {
        Point2D.Double a = new Point2D.Double(sourceX1, sourceY1);
        Point2D.Double b = new Point2D.Double(sourceX2, sourceY2);
        Point2D.Double c = new Point2D.Double(destX1, destY1);
        Point2D.Double d = new Point2D.Double(destX2, destY2);
        
        double len_ab = distance(a, b);
        double len_cd = distance(c, d);
        double scale = len_cd / len_ab;
        
        double r = Math.atan2(d.y - c.y, d.x - c.x) - Math.atan2(b.y - a.y, b.x - a.x);
        
        AffineTransform tx = new AffineTransform();
        tx.translate(c.x, c.y);
        tx.rotate(r);
        tx.scale(scale, scale);
        tx.translate(-a.x, -a.y);
        return tx;
    }  
    
    /**
     * Calculate the area of a triangle. Returns 0 if the triangle is degenerate.
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    public static double triangleArea(Placement p1, Placement p2, Placement p3) {
        double a = p1.getLocation().getLinearDistanceTo(p2.getLocation());
        double b = p2.getLocation().getLinearDistanceTo(p3.getLocation());
        double c = p3.getLocation().getLinearDistanceTo(p1.getLocation());
        double s = (a + b + c) / 2.;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
    
    public static List<Placement> mostDistantPair(List<Placement> points) {
        Placement maxA = null, maxB = null;
        double max = 0;
        for (Placement a : points) {
            for (Placement b : points) {
                if (a == b) {
                    continue;
                }
                double d = a.getLocation().getLinearDistanceTo(b.getLocation());
                if (d > max) {
                    maxA = a;
                    maxB = b;
                    max = d;
                }
            }
        }
        ArrayList<Placement> results = new ArrayList<>();
        results.add(maxA);
        results.add(maxB);
        return results;
    }
}
