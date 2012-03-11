/*******************************************************************************
 * Copyright (c) 2004-2009 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.rpm.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.linuxtools.rpm.core.RPMProject;
import org.eclipse.linuxtools.rpm.core.RPMProjectLayout;
import org.eclipse.ui.PlatformUI;

/**
 * Import Operation Class for RPM plug-in. This allows us to abstract the operations
 *  to  a utility class which also inherits IRunnableWithProgress that allows use of
 * progress bar
 */

public class SRPMImportOperation implements IRunnableWithProgress {
	private IProject project;
	private File sourceRPM;
	private URL remoteSRPM;
	private RPMProjectLayout projectLayout;

	// Progressmonitor
	private IProgressMonitor monitor;

	private List<Exception> rpm_errorTable;

	/**
	 * Method SRPMImportOperation.
	 * @param project The project to import into.
	 * @param sourceRPM The source rpm to import.
	 * @param rpmProjectLayout The required layout of the project.
	 */
	public SRPMImportOperation(IProject project, File sourceRPM, RPMProjectLayout rpmProjectLayout) {
		this.project = project;
		this.sourceRPM = sourceRPM;
		this.projectLayout = rpmProjectLayout;
	}
	
	/**
	 * @param project The project to import to.
	 * @param sourceRPM The remote SRPM file.
	 * @param rpmProjectLayout The desired project layout of the project.
	 */
	public SRPMImportOperation(IProject project, URL sourceRPM, RPMProjectLayout rpmProjectLayout) {
		this.remoteSRPM = sourceRPM;
		this.project = project;
		this.projectLayout = rpmProjectLayout;
	}
	

	/**
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
	 *
	 * Perform the import of  SRPM import. Call the build class incrementally
	 */
	public void run(IProgressMonitor progressMonitor)
		throws InvocationTargetException {
		// Total number of work steps needed
		int totalWork = 3;

		monitor = progressMonitor;
		rpm_errorTable = new ArrayList<Exception>();

		monitor.beginTask(Messages.getString("SRPMImportOperation.Starting"), //$NON-NLS-1$
		totalWork);

		// Try to create an instance of the build class. 
		try {
			RPMProject rpmProject = new RPMProject(project, projectLayout);
			monitor.worked(1);
			monitor.setTaskName(Messages.getString("SRPMImportOperation.Importing_SRPM")); //$NON-NLS-1$
			if (sourceRPM != null) {
				rpmProject.importSourceRPM(sourceRPM);
				monitor.worked(2);
			} else if (remoteSRPM != null) {
				SubProgressMonitor submonitor = new SubProgressMonitor(monitor, 1);
				rpmProject.importSourceRPM(remoteSRPM, submonitor);
				monitor.worked(2);
			} else {
				throw new IllegalStateException();
			}
		} catch (Exception e) {
			e.printStackTrace();
			rpm_errorTable.add(e);
			return;
		}
		monitor.worked(2);
	}


	/**
	 * @return The result of the operation.
	 */
	public MultiStatus getStatus() {
	IStatus[] errors = new IStatus[rpm_errorTable.size()];
	Iterator<Exception> count = rpm_errorTable.iterator();
	int iCount = 0;
	String error_message=Messages.getString("SRPMImportOperation.0"); //$NON-NLS-1$
	while (count.hasNext()) {

		Object anonErrorObject = count.next();
		if (anonErrorObject instanceof Throwable) {
			Throwable errorObject = (Throwable)  anonErrorObject;
			error_message=errorObject.getMessage();
			if (error_message == null)
				error_message=Messages.getString("SRPMImportOperation.1"); //$NON-NLS-1$
				
		}
		else
			if (anonErrorObject instanceof Status)
			{
				Status errorObject = (Status) anonErrorObject;
				error_message=errorObject.getMessage();
				if (error_message == null)
					error_message=Messages.getString("SRPMImportOperation.2"); //$NON-NLS-1$
			}
		IStatus error =
			new Status(
				IStatus.ERROR,
				"RPM Plugin",IStatus.OK, //$NON-NLS-1$
				error_message,
				null);
		errors[iCount] = error;
		iCount++;
	}

	return new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK, errors, Messages.getString("SRPMImportOperation.3"), //$NON-NLS-1$
	null);
}
	
}
