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
package org.eclipse.linuxtools.rpm.ui.editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.linuxtools.rpm.core.utils.BufferedProcessInputStream;
import org.eclipse.linuxtools.rpm.core.utils.Utils;
import org.eclipse.linuxtools.rpm.ui.editor.preferences.PreferenceConstants;
import org.eclipse.osgi.util.NLS;

public final class RpmPackageBuildProposalsJob extends Job {

	private RpmPackageBuildProposalsJob(String name) {
		super(name);
	}

	private static final String JOB_NAME = Messages.RpmPackageBuildProposalsJob_0;

	private static RpmPackageBuildProposalsJob job = null;

	private static final IEclipsePreferences PREFERENCES = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);

	protected static final IEclipsePreferences.IPreferenceChangeListener PROPERTY_LISTENER = new IEclipsePreferences.IPreferenceChangeListener() {
		public void preferenceChange(PreferenceChangeEvent event) {
			if (event.getKey().equals(PreferenceConstants.P_CURRENT_RPMTOOLS)) {
				update();
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return retrievePackageList(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#shouldSchedule()
	 */
	@Override
	public boolean shouldSchedule() {
		return equals(job);
	}

	/**
	 * Run the Job if it's needed according with the configuration set in the
	 * preference page.
	 */
	protected static void update() {
		boolean runJob = false;
		// Today's date
		Date today = new Date();
		if (PREFERENCES
				.getBoolean(PreferenceConstants.P_RPM_LIST_BACKGROUND_BUILD, PreferenceConstants.DP_RPM_LIST_BACKGROUND_BUILD)) {
			int period = PREFERENCES
					.getInt(PreferenceConstants.P_RPM_LIST_BUILD_PERIOD, PreferenceConstants.DP_RPM_LIST_BUILD_PERIOD);
			// each time that the plugin is loaded.
			if (period == 1) {
				runJob = true;
			} else {
				long lastBuildTime = PREFERENCES
						.getLong(PreferenceConstants.P_RPM_LIST_LAST_BUILD, PreferenceConstants.DP_RPM_LIST_LAST_BUILD);
				if (lastBuildTime == 0) {
					runJob = true;
				} else {
					long interval = (today.getTime() - lastBuildTime)
							/ (1000 * 60 * 60 * 24);
					// run the job once a week
					if (period == 2 && interval >= 7)
						runJob = true;
					// run the job once a month
					else if (period == 3 && interval >= 30)
						runJob = true;
				}
			}
			if (runJob) {
				if (job == null) {
					job = new RpmPackageBuildProposalsJob(JOB_NAME);
					job.schedule();
					PREFERENCES.putLong(PreferenceConstants.P_RPM_LIST_LAST_BUILD, today
							.getTime());
				} else {
					job.cancel();
					job.schedule();
					PREFERENCES.putLong(
							PreferenceConstants.P_RPM_LIST_LAST_BUILD, today
									.getTime());
				}
			}
		} else {
			if (job != null) {
				job.cancel();
				job = null;
			}
		}
	}

	/**
	 * Retrieve the package list
	 * 
	 * @param monitor
	 *            to update
	 * @return a <code>IStatus</code>
	 */
	private IStatus retrievePackageList(IProgressMonitor monitor) {
		String rpmListCmd = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_CURRENT_RPMTOOLS);
		String rpmListFilepath = Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_RPM_LIST_FILEPATH);
		File bkupFile = new File(rpmListFilepath + ".bkup"); //$NON-NLS-1$
		try {
			monitor.beginTask(Messages.RpmPackageBuildProposalsJob_1,
					IProgressMonitor.UNKNOWN);
			if (Utils.fileExist("/bin/sh")) { //$NON-NLS-1$
				BufferedProcessInputStream in = Utils.runCommandToInputStream(
						"/bin/sh", "-c", rpmListCmd); //$NON-NLS-1$ //$NON-NLS-2$
				// backup pkg list file
				File rpmListFile = new File(rpmListFilepath);
				if (rpmListFile.exists())
					Utils.copyFile(new File(rpmListFilepath), bkupFile);

				BufferedWriter out = new BufferedWriter(new FileWriter(
						rpmListFile, false));
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				monitor.subTask(Messages.RpmPackageBuildProposalsJob_2
						+ rpmListCmd + Messages.RpmPackageBuildProposalsJob_3);
				String line;
				while ((line = reader.readLine()) != null) {
					monitor.subTask(line);
					out.write(line + "\n"); //$NON-NLS-1$
					if (monitor.isCanceled()) {
						in.destroyProcess();
						in.close();
						out.close();
						// restore backup
						if (rpmListFile.exists() && bkupFile.exists()) {
							Utils.copyFile(bkupFile, rpmListFile);
							bkupFile.delete();
						}
						Activator.packagesList = new RpmPackageProposalsList();
						return Status.CANCEL_STATUS;
					}
				}
				in.close();
				out.close();
				bkupFile.delete();
				int processExitValue = 0;
				try {
					processExitValue = in.getExitValue();
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				}
				if (processExitValue != 0){
					SpecfileLog
							.log(IStatus.WARNING,
									processExitValue,
									NLS.bind(
											Messages.RpmPackageBuildProposalsJob_NonZeroReturn,
											processExitValue), null);
				}
			}
		} catch (IOException e) {
			SpecfileLog.logError(e);
			return null;
		} finally {
			monitor.done();
		}
		// Update package list
		Activator.packagesList = new RpmPackageProposalsList();
		return Status.OK_STATUS;
	}

	/**
	 * Enable and disable the property change listener.
	 * 
	 * @param activated
	 */
	protected static void setPropertyChangeListener(boolean activated) {
		if (activated) {
			PREFERENCES.addPreferenceChangeListener(PROPERTY_LISTENER);
		} else {
			PREFERENCES.removePreferenceChangeListener(PROPERTY_LISTENER);
		}
	}

}
