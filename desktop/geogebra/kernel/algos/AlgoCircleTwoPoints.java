/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.kernelND.GeoQuadricND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoCircleTwoPoints extends AlgoSphereNDTwoPoints {

     public AlgoCircleTwoPoints(
        AbstractConstruction cons,
        GeoPoint M,
        GeoPoint P) {
        super(cons,M,P);
        setIncidence();
    }
    
    public AlgoCircleTwoPoints(
            AbstractConstruction cons,
            String label,
            GeoPoint M,
            GeoPoint P) {
         super(cons, label,M, P);
         setIncidence();
    }
    
    private void setIncidence() {
    	((GeoPoint) getP()).addIncidence(getCircle());
    }
    
    @Override
	protected GeoQuadricND createSphereND(AbstractConstruction cons){
    	GeoConic circle = new GeoConic(cons);
        circle.addPointOnConic((GeoPoint) getP()); //TODO do this in AlgoSphereNDTwoPoints
        return circle;
    }

    @Override
	public String getClassName() {
        return "AlgoCircleTwoPoints";
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_CIRCLE_TWO_POINTS;
    }
    
    
    public GeoConic getCircle() {
        return (GeoConic) getSphereND();
    }
    /*
    GeoPoint getM() {
        return M;
    }
    GeoPoint getP() {
        return P;
    }
    */

    // compute circle with midpoint M and radius r
    /*
    public final void compute() {
        circle.setCircle(M, P);
    }
    */

    @Override
	final public String toString() {

        return app.getPlain("CircleThroughAwithCenterB",
        		((GeoElement) getP()).getLabel(),
        		((GeoElement) getM()).getLabel());

    }
}
