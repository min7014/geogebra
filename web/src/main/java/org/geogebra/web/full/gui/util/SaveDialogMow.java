package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         simplified save dialog with material design
 *
 */
public class SaveDialogMow extends DialogBoxW
		implements SetLabels, FastClickHandler {
	private FlowPanel dialogContent;
	private FlowPanel inputPanel;
	private FormLabel titleLbl;
	private InputPanelW titleField;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton saveBtn;

	/**
	 * @param app see {@link AppW}
	 */
	public SaveDialogMow(AppW app) {
		super(app.getPanel(), app);
		this.addStyleName("saveDialogMow");
		initGUI();
		initActions();
	}

	private void initGUI() {
		dialogContent = new FlowPanel();
		inputPanel = new FlowPanel();
		inputPanel.setStyleName("mowMediaDialogContent");
		inputPanel.addStyleName("emptyState");
		titleField = new InputPanelW("", app, 1, 25, false);
		titleLbl = new FormLabel().setFor(titleField.getTextComponent());
		titleLbl.addStyleName("inputLabel");
		titleField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", app.getLocalization().getMenu("Untitled"));
		titleField.addStyleName("inputText");
		inputPanel.add(titleLbl);
		inputPanel.add(titleField);
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = new StandardButton("", app);
		cancelBtn.addFastClickHandler(this);
		saveBtn = new StandardButton("", app);
		saveBtn.addFastClickHandler(this);
		buttonPanel.add(cancelBtn);
		buttonPanel.add(saveBtn);
		dialogContent.add(inputPanel);
		dialogContent.add(buttonPanel);
		setLabels();
		this.add(dialogContent);
	}

	private void initActions() {
		// set focus to input field!
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getInputField().getTextComponent().setFocus(true);
			}
		});
		addFocusBlurHandlers();
		addHoverHandlers();
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		titleField.getTextComponent().getTextBox()
				.addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						getInputPanel().addStyleName("hoverState");
					}
				});
		titleField.getTextComponent().getTextBox()
				.addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						getInputPanel().removeStyleName("hoverState");
					}
				});
	}

	private void addFocusBlurHandlers() {
		titleField.getTextComponent().getTextBox()
				.addFocusHandler(new FocusHandler() {

					@Override
					public void onFocus(FocusEvent event) {
						setFocusState();
					}
				});
		titleField.getTextComponent().getTextBox()
				.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						resetInputField();
					}
				});
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		getInputPanel().setStyleName("mowMediaDialogContent");
		getInputPanel().addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	protected void resetInputField() {
		getInputPanel().removeStyleName("focusState");
		getInputPanel().addStyleName("emptyState");
	}

	/**
	 * Input changed (paste or key event happened)
	 */
	protected void onInput() {
		getInputPanel().addStyleName("focusState");
		getInputPanel().removeStyleName("emptyState");
	}

	/**
	 * @return panel holding input with label and error label
	 */
	public FlowPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * @return input text field
	 */
	public InputPanelW getInputField() {
		return titleField;
	}

	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == saveBtn) {
			hide();
		}
	}

	public void setLabels() {
		getCaption().setText(app.getLocalization().getMenu("Save"));
		titleLbl.setText(app.getLocalization().getMenu("Title"));
		saveBtn.setLabel(app.getLocalization().getMenu("Save"));
		cancelBtn.setLabel(app.getLocalization().getMenu("Cancel"));
	}

	@Override
	public void show() {
		super.show();
		center();
	}
}