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

package geogebra3D.kernel3D;

import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.kernel.algos.AlgoSphereNDTwoPoints;
import geogebra.kernel.kernelND.GeoQuadricND;


/**
 *
 * @author  Matthieu
 * @version 
 */
public class AlgoSphereTwoPoints extends AlgoSphereNDTwoPoints {

     public AlgoSphereTwoPoints(
        AbstractConstruction cons,
        GeoPointND M,
        GeoPointND P) {
        super(cons,M,P);
    }
    
    public AlgoSphereTwoPoints(
            AbstractConstruction cons,
            String label,
            GeoPointND M,
            GeoPointND P) {
         super(cons, label,M, P);
    }
    
    protected GeoQuadricND createSphereND(AbstractConstruction cons){
    	GeoQuadric3D sphere = new GeoQuadric3D(cons);
        //circle.addPointOnConic((GeoPoint) getP()); //TODO do this in AlgoSphereNDTwoPoints
        return sphere;
    }

    public String getClassName() {
        return "AlgoSphereTwoPoints";
    }



    public GeoQuadric3D getSphere() {
        return (GeoQuadric3D) getSphereND();
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("SphereThroughAwithCenterB",
        		((GeoElement) getP()).getLabel(),
        		((GeoElement) getM()).getLabel());

    }
}
