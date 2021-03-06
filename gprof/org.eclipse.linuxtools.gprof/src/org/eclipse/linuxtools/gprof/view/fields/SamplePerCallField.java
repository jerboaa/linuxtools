/*******************************************************************************
 * Copyright (c) 2009 STMicroelectronics.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Xavier Raynaud <xavier.raynaud@st.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.gprof.view.fields;

import org.eclipse.linuxtools.dataviewers.abstractviewers.AbstractSTTreeViewer;
import org.eclipse.linuxtools.gprof.parser.GmonDecoder;
import org.eclipse.linuxtools.gprof.view.GmonView;
import org.eclipse.linuxtools.gprof.view.histogram.TreeElement;
import org.eclipse.swt.graphics.Color;


/**
 * Column "Samples" of displayed elements
 *
 * @author Xavier Raynaud <xavier.raynaud@st.com>
 */
public class SamplePerCallField extends SampleProfField {

	/**
	 * Constructor
	 * @param viewer the gmon viewer
	 */
	public SamplePerCallField(AbstractSTTreeViewer viewer) {
		super(viewer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.gprof.view.fields.SampleProfField#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object obj1, Object obj2) {
		TreeElement e1 = (TreeElement) obj1;
		TreeElement e2 = (TreeElement) obj2;
		int c1 = e1.getCalls();
		int c2 = e2.getCalls();
		if ((c1 == 0 || c1 == -1) && (c2 == 0 || c2 == -1)) return 0;
		if (c1 == 0 || c1 == -1) return -1;
		if (c2 == 0 || c2 == -1) return 1;
		float f1 = (float)e1.getSamples()/(float)c1;
		float f2 = (float)e2.getSamples()/(float)c2;
		return Float.compare(f1, f2);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.gprof.view.fields.SampleProfField#getColumnHeaderText()
	 */
	public String getColumnHeaderText() {
		String prefix = "";
		Object o = viewer.getInput();
		if (o instanceof GmonDecoder) {
			GmonDecoder decoder = (GmonDecoder) o;
			if (decoder.isICache()) {
				prefix = "ICACHE ";
			} else if (decoder.isDCache()) {
				prefix = "DCACHE ";
			}
		}
		return prefix + "Time/Call";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.gprof.view.fields.SampleProfField#getColumnHeaderTooltip()
	 */
	public String getColumnHeaderTooltip() {
		Object o = viewer.getInput();
		if (o instanceof GmonDecoder) {
			GmonDecoder decoder = (GmonDecoder) o;
			if (decoder.isICache()) {
				return "Time/Call spent by function accessing instruction cache";
			} else if (decoder.isDCache()) {
				return "Time/Call spent by function accessing data cache";
			}else return "Time/Call spent by function";
		}
		return null;
	}



	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.gprof.view.fields.SampleProfField#getValue(java.lang.Object)
	 */
	public String getValue(Object obj) {
		TreeElement e = (TreeElement) obj;
		int i = e.getSamples();
		int j = e.getCalls();
		if (i == -1 || j <= 0) return "";
		float k = (float)i/(float)j;

		double prof_rate = getProfRate();
		if(prof_rate != 0){
			return getValue(k, prof_rate);
		}else return "";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.gprof.view.fields.SampleProfField#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) {
		return GmonView.getBackground(element);
	}

	public Number getNumber(Object obj) {
		TreeElement e = (TreeElement) obj;
		int i = e.getSamples();
		int j = e.getCalls();
		if (i == -1 || j <= 0) return 0L;
		float k = (float)i/(float)j;

		double prof_rate = getProfRate();
		if(prof_rate != 0){
			return k/prof_rate;
		}else return 0L;
	}
	

}
