package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

public class GeneralPath extends geogebra.common.awt.GeneralPath implements
        Shape {
	
	private geogebra.web.kernel.gawt.GeneralPath impl = new geogebra.web.kernel.gawt.GeneralPath();

	public GeneralPath() {
		impl = new geogebra.web.kernel.gawt.GeneralPath();
	}
	
	public GeneralPath(geogebra.web.kernel.gawt.GeneralPath g) {
		impl = g;
	}
	
	
	public boolean intersects(int rx, int ry, int rw, int rh) {
		return impl.intersects(rx, ry, rw, rh);
	}

	
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	
	public Rectangle getBounds() {
		return new geogebra.web.awt.Rectangle(impl.getBounds());
	}

	
	public Rectangle2D getBounds2D() {
		return new geogebra.web.awt.Rectangle2D(impl.getBounds2D());
	}

	
	public boolean contains(Rectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	
	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return (PathIterator) impl.getPathIterator(geogebra.web.awt.AffineTransform.getWebTransform(affineTransform));
	}

	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return (PathIterator) impl.getPathIterator(geogebra.web.awt.AffineTransform.getWebTransform(at), flatness);
	}

	
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public geogebra.web.kernel.gawt.Shape getWebShape() {
		return impl;
	}

	
	@Override
    public void moveTo(float x, float y) {
		impl.moveTo(x, y);
	}

	
	@Override
    public void reset() {
		impl.reset();
	}

	
	@Override
    public void lineTo(float x, float y) {
		impl.lineTo(x, y);
	}

	
	@Override
    public void closePath() {
		impl.closePath();
	}

	
	@Override
    public geogebra.common.awt.Shape createTransformedShape(
	        AffineTransform affineTransform) {
		return (geogebra.common.awt.Shape) impl.createTransformedShape(geogebra.web.awt.AffineTransform.getWebTransform(affineTransform));
	}

	
	@Override
    public Point2D getCurrentPoint() {
		return new geogebra.web.awt.Point2D(impl.getCurrentPoint().getX(),impl.getCurrentPoint().getY());
	}

	@Override
    public boolean contains(Rectangle2D p) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public boolean contains(Point2D p) {
	    // TODO Auto-generated method stub
	    return false;
    }

}
