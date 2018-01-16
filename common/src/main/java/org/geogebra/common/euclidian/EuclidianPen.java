package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.algos.AlgoStrokeInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

/**
 * Handles pen and freehand tool
 *
 */
public class EuclidianPen implements GTimerListener {

	/**
	 * app
	 */
	protected App app;
	/**
	 * view
	 */
	protected EuclidianView view;

	/**
	 * minimum determinant for circles decrease to allow less "round" circles
	 */
	public double CIRCLE_MIN_DET = 0.95;

	/**
	 * increase to allow uglier circles
	 */
	public double CIRCLE_MAX_SCORE = 0.10;

	/**
	 * maximum deviation between the segment lengths increase to allow less
	 * beautiful rectangles
	 */
	public double RECTANGLE_LINEAR_TOLERANCE = 0.20;

	/**
	 * maximum deviation between the segment lengths increase to allow less
	 * beautiful polygons
	 */
	public double POLYGON_LINEAR_TOLERANCE = 0.20;

	/**
	 * maximum deviation between the angles of a rectangle increase to allow
	 * less beautiful rectangles
	 */
	public double RECTANGLE_ANGLE_TOLERANCE = 15 * Math.PI / 180;

	/**
	 * maximum determinant for lines (e.g. sides of a polygon) decrease to allow
	 * lines that are not so straight
	 */
	public double LINE_MAX_DET = 0.015;
	/** Polyline that conects stylebar to pen settings */
	public final GeoPolyLine DEFAULT_PEN_LINE;

	private AlgoElement lastAlgo = null;
	/** points created by pen */
	protected ArrayList<GPoint> penPoints = new ArrayList<>();
	private ArrayList<GPoint> temp = null;
	protected int minX = Integer.MAX_VALUE;
	protected int maxX = Integer.MIN_VALUE;

	// segment

	private final static int PEN_SIZE_FACTOR = 2;
	/** skip intermediate points on segments longer than this */
	private static final double MAX_POINT_DIST = 30;
	/** ignore consecutive pen points closer than this */
	private static final double MIN_POINT_DIST = 3;

	private boolean startNewStroke = false;

	private int penSize;
	private int lineOpacity;
	private int lineThickness;
	private GColor lineDrawingColor;
	private int lineDrawingStyle;
	// true if we need repaint
	private boolean needsRepaint;

	/**
	 * start point of the gesture
	 */
	protected GeoPoint initialPoint = null;

	/**
	 * delete initialPoint if no shape is found
	 */
	protected boolean deleteInitialPoint = false;

	private GTimer timer = null;

	private int eraserSize;
	private int penLineStyle;
	private GColor penColor;

	// being used for Freehand Shape tool (not done yet)
	// private boolean recognizeShapes = false;

	/************************************************
	 * Construct EuclidianPen
	 *
	 * @param app
	 *            application
	 * @param view
	 *            view
	 */
	public EuclidianPen(App app, EuclidianView view) {
		this.view = view;
		this.app = app;

		timer = app.newTimer(this, 1500);

		setDefaults();

		DEFAULT_PEN_LINE = new GeoPolyLine(app.getKernel().getConstruction()) {
			@Override
			public void setObjColor(GColor color) {
				super.setObjColor(color);
				setPenColor(color);
			}

			@Override
			public void setLineThickness(int th) {
				super.setLineThickness(th);
				setPenSize(th);
			}

			@Override
			public void setLineType(int i) {
				super.setLineType(i);
				setPenLineStyle(i);
			}

			@Override
			public void setLineOpacity(int lineOpacity) {
				super.setLineOpacity(lineOpacity);
				setPenOpacity(lineOpacity);
			}
		};
		DEFAULT_PEN_LINE.setLineThickness(penSize);
		DEFAULT_PEN_LINE.setLineOpacity(lineOpacity);
		DEFAULT_PEN_LINE.setObjColor(penColor);
	}

	// ===========================================
	// Getters/Setters
	// ===========================================

	/**
	 * Set default pen color, line style, thickness, eraser size
	 */
	public void setDefaults() {
		penSize = 3;
		eraserSize = EuclidianConstants.DEFAULT_ERASER_SIZE;
		penLineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;
		penColor = GColor.BLACK;
		lineOpacity = 10;
	}

	/**
	 * @return pen size
	 */
	public int getPenSize() {
		return penSize;
	}

	/**
	 * @param lineOpacity
	 *            Opacity
	 */
	public void setPenOpacity(int lineOpacity) {
		if (this.lineOpacity != lineOpacity) {
			startNewStroke = true;
		}
		this.lineOpacity = lineOpacity;
		setPenColor(penColor.deriveWithAlpha(lineOpacity));
	}

	/**
	 * @return pen size + 1
	 */
	public int getLineThickness() {
		return lineThickness + 1;
	}

	/**
	 * @param penSize
	 *            pen size
	 */
	public void setPenSize(int penSize) {
		if (this.penSize != penSize) {
			startNewStroke = true;
		}
		this.penSize = penSize;
		lineThickness = penSize;
	}

	/**
	 * @return pen line style
	 */
	public int getPenLineStyle() {
		return penLineStyle;
	}

	/**
	 * @param penLineStyle
	 *            pen line style
	 */
	public void setPenLineStyle(int penLineStyle) {
		if (this.penLineStyle != penLineStyle) {
			startNewStroke = true;
		}
		this.penLineStyle = penLineStyle;
		lineDrawingStyle = penLineStyle;
	}

	/**
	 * @return pen color
	 */
	public GColor getPenColor() {
		return penColor;
	}

	/**
	 * @return true if we need to repaint the preview line
	 */
	public boolean needsRepaint() {
		return needsRepaint;
	}

	/**
	 * use one point as first point of the created shape
	 *
	 * @param point
	 *            start point
	 * @param deletePoint
	 *            delete the point if no shape is found
	 */
	public void setInitialPoint(GeoPoint point, boolean deletePoint) {
		this.initialPoint = point;
		this.deleteInitialPoint = deletePoint;
	}

	/**
	 *
	 * @param e
	 *            event
	 * @return Is this MouseEvent an erasing Event.
	 */
	public boolean isErasingEvent(AbstractEvent e) {
		return app.isRightClick(e) && !isFreehand();
	}

	/**
	 * Update the info about last geo so that we can continue a polyline
	 *
	 * @param penGeo
	 *            last object created with pen
	 */
	public void setPenGeo(GeoElement penGeo) {

		if (penGeo == null) {
			lastAlgo = null;
		} else if (penGeo.getParentAlgorithm() instanceof AlgoStrokeInterface) {
			lastAlgo = penGeo.getParentAlgorithm();
		}
	}

	/**
	 * Make sure we start using a new polyline
	 */
	public void resetPenOffsets() {
		lastAlgo = null;
	}

	// ===========================================
	// Mouse Event Handlers
	// ===========================================

	/**
	 * Mouse dragged while in pen mode, decide whether erasing or new points.
	 *
	 * @param e
	 *            mouse event
	 */
	public void handleMouseDraggedForPenMode(AbstractEvent e) {
		view.setCursor(EuclidianCursor.TRANSPARENT);
		if (isErasingEvent(e)) {
			view.getEuclidianController().getDeleteMode()
					.handleMouseDraggedForDelete(e, eraserSize, true);
			app.getKernel().notifyRepaint();
		} else {
			// drawing in progress, so we need repaint
			needsRepaint = true;
			addPointPenMode(e);
		}
	}

	/**
	 * @param e
	 *            event
	 * @param hits
	 *            hits
	 */
	public void handleMousePressedForPenMode(AbstractEvent e, Hits hits) {
		if (!isErasingEvent(e)) {

			timer.stop();

			penPoints.clear();
			addPointPenMode(e);
			// we need single point only for pen tool
			// prevent creating points with freehand tool
			if (!isFreehand()) {
				// will create the single point for pen tool
				addPointsToPolyLine(penPoints);
			}
			view.cacheLayers(app.getMaxLayerUsed());
		}
	}

	/**
	 * Method to repaint the whole preview line
	 * 
	 * @param g2D
	 *            graphics for pen
	 */
	public void doRepaintPreviewLine(GGraphics2D g2D) {
		if (penPoints.size() < 2) {
			return;
		}
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		g2D.setStroke(EuclidianStatic.getStroke(getLineThickness(),
				lineDrawingStyle, GBasicStroke.JOIN_ROUND));
		g2D.setColor(lineDrawingColor);
		gp.moveTo(penPoints.get(0).x, penPoints.get(0).y);
		for (int i = 1; i < penPoints.size() - 1; i++) {
			gp.lineTo(penPoints.get(i).x, penPoints.get(i).y);

		}
		g2D.draw(gp);
	}

	/**
	 * Method to repaint the whole preview line from (x, y) with a given width.
	 * 
	 * @param g2D
	 *            graphics for pen
	 * @param color
	 *            of the pen preview
	 * @param thickness
	 *            of the pen preview
	 * @param x
	 *            Start x coordinate
	 * @param y
	 *            Start y coordinate
	 * @param width
	 *            of the preview
	 */
	public void drawStylePreview(GGraphics2D g2D, GColor color, int thickness,
			int x, int y, int width) {
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		g2D.setStroke(EuclidianStatic.getStroke(thickness,
				EuclidianStyleConstants.LINE_TYPE_FULL));
		g2D.setColor(color);
		gp.reset();
		gp.moveTo(x, y);
		gp.lineTo(x + width, y);
		g2D.draw(gp);
	}

	/**
	 * add the saved points to the last stroke or create a new one
	 *
	 * @param e
	 *            event
	 */
	public void addPointPenMode(AbstractEvent e) {

		// if a PolyLine is selected, we can append to it.

		ArrayList<GeoElement> selGeos = app.getSelectionManager()
				.getSelectedGeos();

		if (selGeos.size() == 1 && selGeos.get(0) instanceof GeoPolyLine) {
			lastAlgo = selGeos.get(0).getParentAlgorithm();
		}

		view.setCursor(EuclidianCursor.TRANSPARENT);

		// if (g2D == null) g2D = penImage.createGraphics();

		GPoint newPoint = new GPoint(e.getX(), e.getY());

		if (minX > e.getX()) {
			minX = e.getX();
		}
		if (maxX < e.getX()) {
			maxX = e.getX();
		}

		if (penPoints.size() == 0) {
			if (initialPoint != null) {
				// also add the coordinates of the initialPoint to the penPoints
				Coords coords = initialPoint.getCoords();
				// calculate the screen coordinates
				int locationX = (int) (view.getXZero()
						+ (coords.getX() / view.getInvXscale()));
				int locationY = (int) (view.getYZero()
						- (coords.getY() / view.getInvYscale()));

				GPoint p = new GPoint(locationX, locationY);
				penPoints.add(p);
				needsRepaint = true;
				view.repaintView();
				// draw a line between the initalPoint and the first point
				// drawPenPreviewLine(g2D, newPoint, p);
			}
			penPoints.add(newPoint);
		} else {
			GPoint p1 = penPoints.get(penPoints.size() - 1);
			GPoint p2 = penPoints.size() >= 2
					? penPoints.get(penPoints.size() - 2) : null;
			GPoint p3 = penPoints.size() >= 3
					? penPoints.get(penPoints.size() - 3) : null;


			// drawPenPreviewLine(g2D, newPoint, lastPoint);
			double dist = p1.distance(newPoint);

			if (dist > MAX_POINT_DIST
					|| (dist > MIN_POINT_DIST
							&& (angle(newPoint, p1, p2) > Math.PI / 18
							|| angle(p1, p2, p3) > Math.PI / 18
									|| (p3 != null && p3.distance(newPoint) > 3
									* MAX_POINT_DIST)))) {
				penPoints.add(newPoint);
				addPointRepaint();
			} else if (dist > MIN_POINT_DIST) {
				Log.debug("Merge:" + p1 + "," + newPoint);
				p2.x = (p1.x + p2.x) / 2;
				p2.y = (p1.y + p2.y) / 2;
				p1.x = newPoint.x;
				p1.y = newPoint.y;
				// penPoints.add(newPoint);
				addPointRepaint();
			}
		}
	}

	private void addPointRepaint() {
		needsRepaint = true;
		view.repaintView();
	}

	private static double angle(GPoint a, GPoint b, GPoint c) {
		if (a == null || b == null || c == null) {
			return Math.PI / 2;
		}
		double dx1 = a.x - b.x;
		double dx2 = c.x - b.x;
		double dy1 = a.y - b.y;
		double dy2 = c.y - b.y;
		double ret = Math.PI - MyMath.angle(dx1, dy1, dx2, dy2);
		return Double.isNaN(ret) ? Math.PI / 2 : ret;
	}

	// private void drawPenPreviewLine(GGraphics2D g2D, GPoint point1,
	// GPoint point2) {
	// GLine2D line = AwtFactory.getPrototype().newLine2D();
	// line.setLine(point1.getX(), point1.getY(), point2.getX(),
	// point2.getY());
	// g2D.setStroke(EuclidianStatic.getStroke(getLineThickness(),
	// lineDrawingStyle));
	// g2D.setColor(lineDrawingColor);
	// g2D.fill(line);
	// g2D.draw(line);
	// }

	/**
	 * Clean up the pen mode stuff, add points.
	 *
	 * @param right
	 *            true for right click
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 *
	 * @return true if a GeoElement was created
	 *
	 */
	public boolean handleMouseReleasedForPenMode(boolean right, int x, int y) {
		if (right && !isFreehand()) {
			return false;
		}

		timer.start();

		app.setDefaultCursor();

		// if (!erasing && recognizeShapes) {
		// checkShapes(e);
		// }

		// if (lastPenImage != null) penImage = lastPenImage.getImage();
		// //app.getExternalImage(lastPenImage);

		// Application.debug(penPoints.size()+"");

		addPointsToPolyLine(penPoints);

		penPoints.clear();
		// drawing done, so no need for repaint
		needsRepaint = false;

		return true;
	}

	/**
	 * start timer to check if polyline is same stroke
	 */
	public void startTimer() {
		timer.start();
	}

	/**
	 * @param x
	 *            initial x
	 * @param y
	 *            initial y
	 */
	protected void initShapeRecognition(int x, int y) {
		penPoints.add(new GPoint(x, y));
	}

	/**
	 * Reset the first point
	 */
	protected void resetInitialPoint() {
		if (this.deleteInitialPoint && this.initialPoint != null) {
			this.initialPoint.remove();
		}
		this.initialPoint = null;
	}

	private void addPointsToPolyLine(ArrayList<GPoint> penPoints2) {

		Construction cons = app.getKernel().getConstruction();
		// GeoList newPts;// = new GeoList(cons);
		List<MyPoint> newPts;// = new GeoList(cons);
		if (startNewStroke) {
			lastAlgo = null;
			startNewStroke = false;
		}
		int ptsLength = 0;
		if (lastAlgo == null) {
			// lastPolyLine = new GeoPolyLine(cons, "hello");
			newPts = new ArrayList<>(penPoints2.size());
			// newPts = new GeoList(cons);
		} else {
			// newPts = lastPolyLine.getPointsList();

			// force a gap
			// newPts.add(new GeoPoint2(cons, Double.NaN, Double.NaN, 1));
			AlgoStrokeInterface algo = getAlgoStrokeInterface(lastAlgo);

			if (app.has(Feature.MOW_PEN_SMOOTHING)
					&& algo instanceof AlgoLocusStroke) {
				ArrayList<MyPoint> pointsNoControl = ((AlgoLocusStroke) algo)
						.getPoints();
				ptsLength = pointsNoControl.size();

				newPts = new ArrayList<>(penPoints2.size() + 1 + ptsLength);

				for (int i = 0; i < ptsLength; i++) {
					newPts.add(pointsNoControl.get(i));
				}
			} else {
				ptsLength = algo.getPointsLength();

				newPts = new ArrayList<>(penPoints2.size() + 1 + ptsLength);

				for (int i = 0; i < ptsLength; i++) {
					newPts.add(algo.getPointCopy(i));
				}

			}
			newPts.add(new MyPoint(Double.NaN, Double.NaN));

		}

		Iterator<GPoint> it = penPoints2.iterator();
		while (it.hasNext()) {
			GPoint p = it.next();
			// newPts.add(new GeoPoint2(cons, view.toRealWorldCoordX(p.getX()),
			// view.toRealWorldCoordY(p.getY()), 1));
			newPts.add(new MyPoint(
					view.toRealWorldCoordX(p.getX()),
					view.toRealWorldCoordY(p.getY())));
		}

		AlgoElement algo;
		// don't set label
		if (lastAlgo instanceof AlgoLocusStroke) {
			((AlgoLocusStroke) lastAlgo).updatePointArray(newPts, ptsLength,
					view.getScale(0));
			lastAlgo.getOutput(0).updateRepaint();
			return;
		}
		AlgoElement newPolyLine = app.getKernel().getAlgoDispatcher()
				.getStrokeAlgo(newPts);
		// set label
		newPolyLine.getOutput(0).setLabel(null);
		algo = newPolyLine;

		algo.getOutput(0).setTooltipMode(GeoElement.TOOLTIP_OFF);

		if (lastAlgo != null) {
			try {
				cons.replace(lastAlgo.getOutput(0), algo.getOutput(0));
				// String label = lastPolyLine.getPoly().getLabelSimple();
				// lastPolyLine.getPoly().remove();
				// lastPolyLine.remove();
				// newPolyLine.getPoly().setLabel(label);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		lastAlgo = algo;

		GeoElement poly = algo.getOutput(0);

		poly.setLineThickness(penSize * PEN_SIZE_FACTOR);
		poly.setLineType(penLineStyle);
		poly.setLineOpacity(lineOpacity);
		poly.setObjColor(penColor);

		app.getSelectionManager().clearSelectedGeos(false);
		app.getSelectionManager().addSelectedGeo(poly);

		poly.setSelected(false);
		poly.updateRepaint();

		// app.storeUndoInfo() will be called from wrapMouseReleasedND
	}

	private static AlgoStrokeInterface getAlgoStrokeInterface(AlgoElement al) {
		if (al instanceof AlgoStrokeInterface) {
			return (AlgoStrokeInterface) al;
		}
		return (AlgoStrokeInterface) al.getInput()[0].getParentAlgorithm();
	}

	/**
	 * @param color
	 *            pen color
	 */
	public void setPenColor(GColor color) {
		if (!this.penColor.equals(color)) {
			startNewStroke = true;
		}
		this.penColor = color;
		lineDrawingColor = color;
	}

	/**
	 * used for subclasses to return the last shape that was created
	 *
	 * NOT USED IN THIS CLASS
	 *
	 * @return null
	 */
	public GeoElement getCreatedShape() {
		return null;
	}

	/**
	 * Update state of the pen after geo is removed
	 * 
	 * @param geo
	 *            removed element
	 */
	public void remove(GeoElement geo) {
		if (geo.getParentAlgorithm() == this.lastAlgo) {
			lastAlgo = null;
		}

	}

	@Override
	public void onRun() {
		startNewStroke = true;
	}

	public boolean isFreehand() {
		return false;
	}
}