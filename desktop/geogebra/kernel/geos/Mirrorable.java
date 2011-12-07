package geogebra.kernel.geos;

import geogebra.common.kernel.geos.GeoElement;


/**
 * Represents geos that can be mirrored atline or point
 * 
 */
public interface Mirrorable {
	/**
	 * Miror at point
	 * @param Q mirror
	 */
	public void mirror(GeoPoint Q);
	/**
	 * Miror at line
	 * @param g mirror
	 */
	public void mirror(GeoLine g);
	/**
	 * Returns resulting element
	 * @return resulting element
	 */
	public GeoElement toGeoElement();
}