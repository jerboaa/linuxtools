/*******************************************************************************
 * Copyright (c) 2007 Alphonse Van Assche.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alphonse Van Assche - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.rpm.rpmlint.resolutions;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.linuxtools.rpm.rpmlint.RpmlintLog;
import org.eclipse.linuxtools.rpm.ui.editor.SpecfileEditor;

abstract public class AReplaceTextResolution extends ARpmlintResolution {
	
	/**
	 * Returns the original string
	 * 
	 * @return the original string
	 */
	abstract public String getOriginalString();
	
	/**
	 * Returns the string to replace in the <code>Document</code>
	 * 
	 * @return the string to replace
	 * 
	 */
	abstract public String getReplaceString();
	
	public void run(IMarker marker) {
	
		SpecfileEditor editor = getEditor(marker); 
		if (editor == null) {
			return;
		}
		IDocument doc = editor.getSpecfileSourceViewer().getDocument(); 

		try {
			int lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, 0);
			int index = doc.getLineOffset(lineNumber);
			String line = editor.getSpecfile().getLine(lineNumber);
			int rowIndex = line.indexOf(getOriginalString());
			if (rowIndex > -1){
				doc.replace(index+rowIndex, getOriginalString().length(), getReplaceString()); 
			}
		} catch (BadLocationException e) {
			RpmlintLog.logError(e);
		}
	}
	

}
