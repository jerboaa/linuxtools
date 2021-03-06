/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Jeff Briggs, Henry Hughes, Ryan Morse
 *******************************************************************************/

package org.eclipse.linuxtools.systemtap.ui.systemtapgui;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class Application implements IPlatformRunnable {
	
	/**
	 * Instantiates the workbench and creats a settings folder if it does not exist.
	 * 
	 * @param args not used
	 * 
	 * @return an IPlatformRunnable condition, either EXIT_RESTART or EXIT_OK
	 */
	@SuppressWarnings("deprecation")
	public Object run(Object args) throws Exception {
		Display display = PlatformUI.createDisplay();

		if(!SystemTapGUISettings.settingsFolder.exists())
			SystemTapGUISettings.settingsFolder.mkdir();

		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}
	}
}
