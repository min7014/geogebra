package org.geogebra.web.geogebra3D.web.gui.dialog.options;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.web.geogebra3D.web.gui.images.StyleBar3DResources;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabBar;
import org.geogebra.web.web.gui.dialog.options.BasicTab;
import org.geogebra.web.web.gui.dialog.options.OptionsEuclidianW;
import org.geogebra.web.web.gui.util.MyToggleButtonW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Properties for 3D view (web)
 * 
 * @author mathieu
 *
 */
@SuppressWarnings({ "javadoc", "synthetic-access" })
public class OptionsEuclidian3DW extends OptionsEuclidianW {

	private AxisTab zAxisTab;

	private ProjectionTab projectionTab;

	/**
	 * basic tab for 3D
	 * 
	 * @author mathieu
	 *
	 */
	protected class BasicTab3D extends BasicTab {

		private CheckBox cbYAxisVertical;
		private CheckBox cbUseClipping, cbShowClipping;
		private FlowPanel clippingOptionsPanel, boxSizePanel;
		private Label clippingOptionsTitle, boxSizeTitle;
		private RadioButton radioClippingSmall, radioClippingMedium,
				radioClippingLarge;
		private CheckBox cbUseLight;

		/**
		 * constructor
		 */
		public BasicTab3D(OptionsEuclidianW o) {
			super(o);

			addClippingOptionsPanel();

		}

		@Override
		protected void indentDimPanel() {
			// TODO remove this and implement stuff for 3D
		}

		@Override
		protected void addToDimPanel(Widget w) {
			// TODO remove this and implement stuff for 3D
		}

		@Override
		protected void fillMiscPanel() {
			miscPanel.add(LayoutUtilW.panelRow(backgroundColorLabel,
					btBackgroundColor));
			miscPanel.add(LayoutUtilW.panelRow(cbUseLight));
		}

		@Override
		protected void addMiscPanel() {

			cbUseLight = new CheckBox();

			super.addMiscPanel();

			cbUseLight.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					get3dview().getSettings()
							.setUseLight(cbUseLight.getValue());
					repaintView();
				}
			});

		}

		@Override
		protected void applyBackgroundColor(GColor color) {
			model.applyBackgroundColor(3, color);
		}

		@Override
		protected void addAxesOptionsPanel() {

			cbYAxisVertical = new CheckBox();

			cbYAxisVertical.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().setYAxisVertical(cbYAxisVertical.getValue());
					repaintView();
				}
			});

			super.addAxesOptionsPanel();
		}

		@Override
		protected void fillAxesOptionsPanel() {
			axesOptionsPanel.add(LayoutUtilW.panelRow(cbShowAxes));
			axesOptionsPanel.add(LayoutUtilW.panelRow(cbYAxisVertical));
			axesOptionsPanel.add(LayoutUtilW.panelRow(lblAxisLabelStyle,
					cbAxisLabelSerif, cbAxisLabelBold, cbAxisLabelItalic));

		}

		private void addClippingOptionsPanel() {

			// clipping options panel
			clippingOptionsTitle = new Label();
			clippingOptionsTitle.setStyleName("panelTitle");
			clippingOptionsPanel = new FlowPanel();
			cbUseClipping = new CheckBox();
			cbUseClipping.setStyleName("checkBoxPanel");
			clippingOptionsPanel.add(cbUseClipping);
			// clippingOptionsPanel.add(Box.createRigidArea(new Dimension(10,
			// 0)));
			cbShowClipping = new CheckBox();
			cbShowClipping.setStyleName("checkBoxPanel");
			clippingOptionsPanel.add(cbShowClipping);

			add(clippingOptionsTitle);
			indent(clippingOptionsPanel);

			cbUseClipping.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().setUseClippingCube(cbUseClipping.getValue());
					repaintView();
				}
			});

			cbShowClipping.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().setShowClippingCube(cbShowClipping.getValue());
					repaintView();
				}
			});

			// clipping box size
			boxSizeTitle = new Label();
			boxSizeTitle.setStyleName("panelTitle");
			boxSizePanel = new FlowPanel();
			radioClippingSmall = new RadioButton("radioClipping");
			radioClippingMedium = new RadioButton("radioClipping");
			radioClippingLarge = new RadioButton("radioClipping");
			boxSizePanel.add(radioClippingSmall);
			boxSizePanel.add(radioClippingMedium);
			boxSizePanel.add(radioClippingLarge);

			add(boxSizeTitle);
			indent(boxSizePanel);

			radioClippingSmall.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().getSettings().setClippingReduction(
							GeoClippingCube3D.REDUCTION_SMALL);
					repaintView();
				}
			});

			radioClippingMedium.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().getSettings().setClippingReduction(
							GeoClippingCube3D.REDUCTION_MEDIUM);
					repaintView();
				}
			});

			radioClippingLarge.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().getSettings().setClippingReduction(
							GeoClippingCube3D.REDUCTION_LARGE);
					repaintView();
				}
			});

		}

		/**
		 * update clipping properties (use and size)
		 */
		public void update3DProperties() {

			cbYAxisVertical.setValue(get3dview().getYAxisVertical());

			cbUseLight.setValue(get3dview().getUseLight());

			cbUseClipping.setValue(get3dview().useClippingCube());
			cbShowClipping.setValue(get3dview().showClippingCube());

			int flag = get3dview().getClippingReduction();
			radioClippingSmall
					.setValue(flag == GeoClippingCube3D.REDUCTION_SMALL);
			radioClippingMedium
					.setValue(flag == GeoClippingCube3D.REDUCTION_MEDIUM);
			radioClippingLarge
					.setValue(flag == GeoClippingCube3D.REDUCTION_LARGE);

		}

		@Override
		public void setLabels() {
			super.setLabels();

			setText(cbYAxisVertical, "YAxisVertical");
			setText(cbUseLight, "UseLighting");
			setText(clippingOptionsTitle, "Clipping");
			setText(cbUseClipping, "UseClipping");
			setText(cbShowClipping, "ShowClipping");
			setText(boxSizeTitle, "BoxSize");
			setText(radioClippingSmall, "BoxSize.small");
			setText(radioClippingMedium, "BoxSize.medium");
			setText(radioClippingLarge, "BoxSize.large");
		}
	}

	@Override
	protected GridTab newGridTab() {
		return new GridTab3D();
	}

	public void setText(HasText cb, String string) {
		cb.setText(loc.getMenu(string));
	}

	class GridTab3D extends GridTab {

		@Override
		protected void addGridType(FlowPanel gridTickAnglePanel) {
			// TODO remove this when implemented
		}

		@Override
		protected void addOnlyFor2D(Widget w) {
			// TODO remove this when implemented
		}

		@Override
		protected void setGridTypeLabel() {
			lblGridType.setText(
					loc.getMenu("GridType") + " : " + loc.getMenu("Cartesian"));
		}
	}

	private class ProjectionTab extends EuclidianTab {

		private ProjectionButtons projectionButtons;

		private FlowPanel orthoPanel, perspPanel, obliquePanel, glassesPanel;
		private Label orthoTitle, perspTitle, obliqueTitle, glassesTitle;

		private AutoCompleteTextFieldW tfPersp, tfGlassesEyeSep, tfObliqueAngle,
				tfObliqueFactor;
		private FormLabel tfPerspLabel, tfGlassesLabel, tfObliqueAngleLabel,
				tfObliqueFactorLabel;
		private CheckBox cbGlassesGray, cbGlassesShutDownGreen;

		private class ProjectionButtons implements ClickHandler {

			private MyToggleButtonW[] buttons;
			private int buttonSelected;

			ProjectionButtons() {

				buttons = new MyToggleButtonW[4];

				buttons[EuclidianView3D.PROJECTION_ORTHOGRAPHIC] = new MyToggleButtonW(
						new Image(StyleBar3DResources.INSTANCE
								.viewOrthographic()));
				buttons[EuclidianView3D.PROJECTION_PERSPECTIVE] = new MyToggleButtonW(
						new Image(StyleBar3DResources.INSTANCE
								.viewPerspective()));
				buttons[EuclidianView3D.PROJECTION_GLASSES] = new MyToggleButtonW(
						new Image(StyleBar3DResources.INSTANCE.viewGlasses()));
				buttons[EuclidianView3D.PROJECTION_OBLIQUE] = new MyToggleButtonW(
						new Image(StyleBar3DResources.INSTANCE.viewOblique()));

				for (int i = 0; i < 4; i++) {
					buttons[i].addClickHandler(this);
				}

				buttonSelected = get3dview().getProjection();
				buttons[buttonSelected].setDown(true);
			}

			public ToggleButton getButton(int i) {
				return buttons[i];
			}

			@Override
			public void onClick(ClickEvent event) {
				MyToggleButtonW source = (MyToggleButtonW) event.getSource();

				if (source == buttons[get3dview().getProjection()]) {
					source.setDown(true);
					return;
				}

				for (int i = 0; i < buttons.length; i++) {
					if (buttons[i].equals(source)) {
						get3dview().getSettings().setProjection(i);
						repaintView();
						buttons[i].setDown(true);
					} else {
						buttons[i].setDown(false);
					}
				}
			}
		}

		public ProjectionTab() {
			super(app);

			projectionButtons = new ProjectionButtons();

			// orthographic projection
			orthoTitle = new Label("");
			orthoTitle.setStyleName("panelTitle");
			orthoPanel = new FlowPanel();
			orthoPanel.add(projectionButtons
					.getButton(EuclidianView3D.PROJECTION_ORTHOGRAPHIC));
			add(orthoTitle);
			indent(orthoPanel);

			// perspective projection
			perspTitle = new Label("");
			perspTitle.setStyleName("panelTitle");
			perspPanel = new FlowPanel();
			tfPersp = getTextField();
			tfPerspLabel = new FormLabel().setFor(tfPersp);
			tfPersp.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processPerspText();
					}
				}
			});

			tfPersp.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processPerspText();
				}
			});
			FlowPanel tfPerspPanel = new FlowPanel();
			tfPerspPanel.setStyleName("panelRowCell");
			tfPerspPanel.add(tfPerspLabel);
			tfPerspPanel.add(tfPersp);
			perspPanel.add(LayoutUtilW.panelRow(
					projectionButtons
							.getButton(EuclidianView3D.PROJECTION_PERSPECTIVE),
					tfPerspPanel));
			add(perspTitle);
			indent(perspPanel);

			// glasses projection (two images)
			glassesTitle = new Label("");
			glassesTitle.setStyleName("panelTitle");
			glassesPanel = new FlowPanel();

			tfGlassesEyeSep = getTextField();
			tfGlassesLabel = new FormLabel().setFor(tfGlassesEyeSep);
			tfGlassesEyeSep.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processGlassesEyeSepText();
					}
				}
			});
			tfGlassesEyeSep.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processGlassesEyeSepText();
				}
			});
			cbGlassesGray = new CheckBox(loc.getMenu("GrayScale"));
			cbGlassesGray.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().setGlassesGrayScaled(cbGlassesGray.getValue());
					repaintView();
				}
			});
			cbGlassesShutDownGreen = new CheckBox(loc.getMenu("ShutDownGreen"));
			cbGlassesShutDownGreen.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					get3dview().setGlassesShutDownGreen(
							cbGlassesShutDownGreen.getValue());
					repaintView();
				}
			});
			FlowPanel tfGlassesPanel = new FlowPanel();
			tfGlassesPanel.setStyleName("panelRowCell");
			tfGlassesPanel.add(tfGlassesLabel);
			tfGlassesPanel.add(tfGlassesEyeSep);
			tfGlassesPanel.add(cbGlassesGray);
			tfGlassesPanel.add(cbGlassesShutDownGreen);
			glassesPanel.add(LayoutUtilW.panelRow(
					projectionButtons
							.getButton(EuclidianView3D.PROJECTION_GLASSES),
					tfGlassesPanel));
			add(glassesTitle);
			indent(glassesPanel);

			// oblique projection
			obliqueTitle = new Label("");
			obliqueTitle.setStyleName("panelTitle");
			obliquePanel = new FlowPanel();

			tfObliqueAngle = getTextField();
			tfObliqueAngleLabel = new FormLabel().setFor(tfObliqueAngle);
			tfObliqueAngle.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processObliqueAngleText();
					}
				}
			});

			tfObliqueAngle.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processObliqueAngleText();
				}
			});

			tfObliqueFactor = getTextField();
			tfObliqueFactorLabel = new FormLabel().setFor(tfObliqueFactor);
			tfObliqueFactor.addKeyHandler(new KeyHandler() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						processObliqueFactorText();
					}
				}
			});

			tfObliqueFactor.addFocusListener(new FocusListenerW(this) {
				@Override
				protected void wrapFocusLost() {
					processObliqueFactorText();
				}
			});
			FlowPanel tfObliquePanel = new FlowPanel();
			tfObliquePanel.setStyleName("panelRowCell");
			tfObliquePanel.add(tfObliqueAngleLabel);
			tfObliquePanel.add(tfObliqueAngle);
			tfObliquePanel.add(tfObliqueFactorLabel);
			tfObliquePanel.add(tfObliqueFactor);
			obliquePanel.add(LayoutUtilW.panelRow(
					projectionButtons
							.getButton(EuclidianView3D.PROJECTION_OBLIQUE),
					tfObliquePanel));
			add(obliqueTitle);
			indent(obliquePanel);

		}

		protected void processPerspText() {
			try {
				int val = Integer.parseInt(tfPersp.getText());
				int min = 1;
				if (val < min) {
					val = min;
					tfPersp.setText("" + val);
				}
				get3dview().getSettings()
						.setProjectionPerspectiveEyeDistance(val);
				repaintView();
			} catch (NumberFormatException e) {
				tfPersp.setText("" + (int) get3dview()
						.getProjectionPerspectiveEyeDistance());
			}
		}

		protected void processGlassesEyeSepText() {
			try {
				int val = Integer.parseInt(tfGlassesEyeSep.getText());
				if (val < 0) {
					val = 0;
					tfGlassesEyeSep.setText("" + val);
				}
				get3dview().getSettings().setEyeSep(val);
				repaintView();
			} catch (NumberFormatException e) {
				tfGlassesEyeSep.setText("" + (int) get3dview().getEyeSep());
			}
		}

		protected void processObliqueAngleText() {
			try {
				double val = Double.parseDouble(tfObliqueAngle.getText());
				if (!Double.isNaN(val)) {

					get3dview().getSettings().setProjectionObliqueAngle(val);
					repaintView();
				}
			} catch (NumberFormatException e) {
				tfObliqueAngle
						.setText("" + get3dview().getProjectionObliqueAngle());
			}
		}

		protected void processObliqueFactorText() {
			try {
				double val = Double.parseDouble(tfObliqueFactor.getText());
				if (!Double.isNaN(val)) {
					if (val < 0) {
						val = 0;
						tfObliqueFactor.setText("" + val);
					}
					get3dview().setProjectionObliqueFactor(val);
					repaintView();
				}
			} catch (NumberFormatException e) {
				tfObliqueFactor
						.setText("" + get3dview().getProjectionObliqueFactor());
			}
		}

		protected void indent(FlowPanel panel) {
			FlowPanel indent = new FlowPanel();
			indent.setStyleName("panelIndent");
			indent.add(panel);
			add(indent);

		}

		@Override
		public void setLabels() {
			setText(orthoTitle,"Orthographic");
			setText(perspTitle,"Perspective");
			setTextColon(tfPerspLabel, "EyeDistance");
			setText(glassesTitle,"Glasses");
			setTextColon(tfGlassesLabel, "EyesSeparation");
			setText(cbGlassesGray,"GrayScale");
			setText(cbGlassesShutDownGreen,"ShutDownGreen");
			setText(obliqueTitle,"Oblique");
			setTextColon(tfObliqueAngleLabel, "Angle");
			setTextColon(tfObliqueFactorLabel, "Dilate.Factor");
		}

		/**
		 * update text values
		 */
		public void updateGUI() {
			tfPersp.setText(""
					+ (int) get3dview().getProjectionPerspectiveEyeDistance());
			tfGlassesEyeSep.setText("" + (int) get3dview().getEyeSep());
			cbGlassesGray.setValue(get3dview().isGlassesGrayScaled());
			cbGlassesShutDownGreen
					.setValue(get3dview().isGlassesShutDownGreen());
			tfObliqueAngle
					.setText("" + get3dview().getProjectionObliqueAngle());
			tfObliqueFactor
					.setText("" + get3dview().getProjectionObliqueFactor());

		}
	}

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            3D view
	 */
	public OptionsEuclidian3DW(AppW app, EuclidianViewInterfaceCommon view) {
		super(app, view);

	}

	public EuclidianView3D get3dview() {
		// TODO Auto-generated method stub
		return (EuclidianView3D) view;
	}

	@Override
	protected BasicTab newBasicTab() {
		return new BasicTab3D(this);
	}

	@Override
	public void updateGUI() {
		((BasicTab3D) basicTab).update3DProperties();
		projectionTab.updateGUI();
		super.updateGUI();
	}

	@Override
	public void setLabels() {
		MultiRowsTabBar tabBar = tabPanel.getTabBar();
		super.setLabels(tabBar, 4);
		tabBar.setTabText(3, loc.getMenu("zAxis"));
		zAxisTab.setLabels();
		tabBar.setTabText(5, loc.getMenu("Projection"));
		projectionTab.setLabels();
	}

	@Override
	protected void addAxesTabs() {
		super.addAxesTabs();
		addZAxisTab();
	}

	@Override
	protected AxisTab newAxisTab(int axis) {
		return new AxisTab(axis, true);
	}

	private void addZAxisTab() {
		zAxisTab = newAxisTab(EuclidianOptionsModel.Z_AXIS);
		tabPanel.add(zAxisTab, "z");
	}

	@Override
	protected void addTabs() {
		super.addTabs();
		addProjectionTab();
	}

	private void addProjectionTab() {
		projectionTab = new ProjectionTab();
		tabPanel.add(projectionTab, "projection");
	}

	protected void repaintView() {
		get3dview().repaintView();
	}

}
