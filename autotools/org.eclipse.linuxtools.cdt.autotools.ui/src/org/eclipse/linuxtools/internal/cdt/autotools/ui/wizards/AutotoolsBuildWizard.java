/*******************************************************************************
 * Copyright (c) 2007, 2008, 2009 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Intel Corporation - initial API and implementation
 *     Red Hat Inc - modification for Autotools project
 *******************************************************************************/
package org.eclipse.linuxtools.internal.cdt.autotools.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedMap;

import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyManager;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyType;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildListComparator;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.AbstractCWizard;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSWizardHandler;
import org.eclipse.cdt.ui.newui.CDTPrefUtil;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.jface.wizard.IWizard;

/**
 *
 */
public class AutotoolsBuildWizard extends AbstractCWizard {
	public static final String OTHERS_LABEL = AutotoolsWizardMessages.getResourceString("AutotoolsBuildWizard.1"); //$NON-NLS-1$
	public static final String AUTOTOOLS_PROJECTTYPE_ID = "org.eclipse.linuxtools.cdt.autotools.core.projectType"; //$NON-NLS-1$
	
	/**
	 * @since 5.1
	 */
	public static final String EMPTY_PROJECT = AutotoolsWizardMessages.getResourceString("AutotoolsBuildWizard.2"); //$NON-NLS-1$
	/**
	 * Creates and returns an array of items to be displayed 
	 */
	public EntryDescriptor[] createItems(boolean supportedOnly, IWizard wizard) {
		IBuildPropertyManager bpm = ManagedBuildManager.getBuildPropertyManager();
		IBuildPropertyType bpt = bpm.getPropertyType(MBSWizardHandler.ARTIFACT);
		IBuildPropertyValue[] vs = bpt.getSupportedValues();
		Arrays.sort(vs, BuildListComparator.getInstance());
		ArrayList<EntryDescriptor> items = new ArrayList<EntryDescriptor>();
		
		// look for Autotools project type
		EntryDescriptor oldsRoot = null;
		SortedMap<String, IProjectType> sm = ManagedBuildManager.getExtensionProjectTypeMap();
		for (String s : sm.keySet()) {
			IProjectType pt = (IProjectType)sm.get(s);
			if (pt.getId().equals(AUTOTOOLS_PROJECTTYPE_ID)) {
				AutotoolsBuildWizardHandler h = new AutotoolsBuildWizardHandler(pt, parent, wizard);
				IToolChain[] tcs = ManagedBuildManager.getExtensionToolChains(pt);
				for(int i = 0; i < tcs.length; i++){
					IToolChain t = tcs[i];
					if(t.isSystemObject()) 
						continue;
					if (!isValid(t, supportedOnly, wizard))
						continue;

					h.addTc(t);
				}

				String pId = null;
				if (CDTPrefUtil.getBool(CDTPrefUtil.KEY_OTHERS)) {
					if (oldsRoot == null) {
						oldsRoot = new EntryDescriptor(OTHERS_LABEL, null, OTHERS_LABEL, true, null, null);
						items.add(oldsRoot);
					}
					pId = oldsRoot.getId();
				} else { // do not group to <Others>
					pId = null;
				}
				items.add(new EntryDescriptor(pt.getId(), pId, pt.getName(), true, h, null));
			}
		}
		return (EntryDescriptor[])items.toArray(new EntryDescriptor[items.size()]);
	}
}
