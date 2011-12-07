/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoText;


/**
 * Take first n objects from a list
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoTakeString extends AlgoElement {

	private GeoText inputText; //input
	private GeoNumeric n, m; //input
    private GeoText outputText; //output	
    private int size;

    public AlgoTakeString(Construction cons, String label, GeoText inputText, GeoNumeric m, GeoNumeric n) {
        super(cons);
        this.inputText = inputText;
        this.m=m;
        this.n=n;

               
        outputText = new GeoText(cons);
        outputText.setIsTextCommand(true);

        setInputOutput();
        compute();
        outputText.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoTakeString";
    }

    @Override
	protected void setInputOutput(){
    	
    	if (n != null) {
	        input = new GeoElement[3];
	        input[0] = inputText;
	        input[1] = m;
	        input[2] = n;
    	}
    	else
    	{
            input = new GeoElement[1];
            input[0] = inputText;
        }    		

        super.setOutputLength(1);
        super.setOutput(0, outputText);
        setDependencies(); // done by AlgoElement
    }

    public GeoText getResult() {
        return outputText;
    }

    @Override
	public final void compute() {

    	if (!m.isDefined() || !n.isDefined()) {
    		outputText.setTextString("");
        	return;
    	}
    	
    	String str = inputText.getTextString();
    	
    	size = str.length();
    	int start=(int)m.getDouble();
    	double nVal = n.getDouble();
    	int end = (int)nVal;
    	
    	if (nVal == 0 && inputText.isDefined() && start > 0 && start <= size) {
    		outputText.setTextString("");
        	return;
    	}
    	
    	if (!inputText.isDefined() ||  size == 0 || start <= 0 || end > size || start > end) {
    		outputText.setUndefined();
    		return;
    	} 
    	
    	outputText.setTextString(str.substring(start - 1, end));
       
    	
   }

}
