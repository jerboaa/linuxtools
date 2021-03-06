/*******************************************************************************
 * Copyright (c) 2009 STMicroelectronics.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Xavier Raynaud <xavier.raynaud@st.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.gcov.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllGcovTests {

	public static Test suite() {
		TestSuite ats = new TestSuite("Tests for org.eclipse.linuxtools.gcov.test");
		//$JUnit-BEGIN$
		ats.addTest(GcovGCDARetrieverTest.suite());
		ats.addTest(GcovParserTest.suite());
// Comment out until we fix bug #299995
//		ats.addTest(GcovViewTest.suite());
		//$JUnit-END$
		return ats;		
	}

}