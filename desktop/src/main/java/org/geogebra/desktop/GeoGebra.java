/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.desktop.main.GeoGebraPreferencesXML;

public class GeoGebra {

	public static Frame splashFrame = null;

	protected GeoGebra() {
	}

	public static void main(String[] cmdArgs) {
		(new GeoGebra()).doMain(cmdArgs);
	}

	protected void doMain(String[] cmdArgs) {
		CommandLineArguments args = new CommandLineArguments(cmdArgs);

		if (args.containsArg("screenDPI") && args.containsArg("screenX")
				&& args.containsArg("screenY")) {
			int screenDPI = Integer.parseInt(args.getStringValue("screenDPI"));
			int screenX = Integer.parseInt(args.getStringValue("screenX"));
			int screenY = Integer.parseInt(args.getStringValue("screenY"));

			GeoGebraPreferencesXML.setDefaults(screenDPI, screenX, screenY);
		}

		boolean showSplash = true;
		if (!args.getBooleanValue("showSplash", true)) {
			showSplash = false;
		}

		if (args.containsArg("help") || args.containsArg("proverhelp")
				|| args.containsArg("v") || args.containsArg("regressionFile")) {
			showSplash = false;
		}

		if (showSplash) {
			// Show splash screen
			URL imageURL = GeoGebra.class.getResource("/org/geogebra/desktop/"
					+ GeoGebraConstants.SPLASH_STRING);
			if (imageURL != null) {
				splashFrame = SplashWindow.splash(Toolkit.getDefaultToolkit()
						.createImage(imageURL));
			} else {
				System.err.println("Splash image not found");
			}
		}

		// Start GeoGebra
		try {
			startGeoGebra(args);
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.flush();
			System.exit(10);
		}

		// Hide splash screen
		if (splashFrame != null) {
			splashFrame.setVisible(false);
		}
	}

	protected void startGeoGebra(CommandLineArguments args) {
		// create and open first GeoGebra window
		org.geogebra.desktop.gui.app.GeoGebraFrame.main(args);
	}

}