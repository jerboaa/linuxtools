/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Jeff Briggs, Henry Hughes, Ryan Morse, Anithra P J
 *******************************************************************************/

package org.eclipse.linuxtools.systemtap.ui.consolelog.structures;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;

import org.eclipse.linuxtools.systemtap.ui.consolelog.actions.StopScriptAction;
import org.eclipse.linuxtools.systemtap.ui.consolelog.internal.Localization;
import org.eclipse.linuxtools.systemtap.ui.consolelog.views.ErrorView;
import org.eclipse.linuxtools.systemtap.ui.structures.IPasswordPrompt;
import org.eclipse.linuxtools.systemtap.ui.structures.runnable.LoggedCommand;
import org.eclipse.linuxtools.systemtap.ui.consolelog.Subscription;

/**
 * This class serves as a pain in the ConsoleView.  It is used to create a new Command that,
 * through ConsoleDaemons will print all the output the the console.  In order to stop the
 * running Command <code>StopScriptAction</code> should be used to stop this console from
 * running.
 * @author Ryan Morse
 */
public class ScriptConsole2 extends IOConsole {
	/**
	 * This method is used to get a reference to a <code>ScriptConsole</code>.  If there
	 * is already an console that has the same name as that provided it will be stopped, 
	 * cleared and returned to the caller to use.  If there is no console matching the 
	 * provided name then a new <code>ScriptConsole</code> will be created for use.
	 * @param name The name of the console that should be returned if available.
	 * @return The console with the provided name, or a new instance if none exist.
	 */
	public static ScriptConsole2 getInstance(String name) {
		ScriptConsole2 console = null;
		try {
			IConsole ic[] = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
	
			//Prevent running the same script twice
			if(null != ic) {
				ScriptConsole2 activeConsole;
				StopScriptAction ssa = new StopScriptAction();
				ssa.init(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				for(int i=0; i<ic.length; i++) {
					activeConsole = (ScriptConsole2)ic[i];
					if(activeConsole.getName().endsWith(name)) {
						//Stop any script currently running
						ssa.run(i);
						//Remove output from last run
						activeConsole.clearConsole();
						activeConsole.setName(name);
						console = activeConsole;
					}
				}
			}
			
			if(null == console) {
				console = new ScriptConsole2(name, null);
				ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});
			}
		} catch(NullPointerException npe) {
			console = null;
		}
		return console;
	}
	
	public static ScriptConsole2 getInstance(String name,Subscription sub) {
		ScriptConsole2 console = null;
		try {
			IConsole ic[] = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
	
			//Prevent running the same script twice
			if(null != ic) {
				ScriptConsole2 activeConsole;
				StopScriptAction ssa = new StopScriptAction();
				ssa.init(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				for(int i=0; i<ic.length; i++) {
					activeConsole = (ScriptConsole2)ic[i];
					if(activeConsole.getName().endsWith(name)) {
						//Stop any script currently running
						ssa.run(i);
			
						//Remove output from last run
						activeConsole.clearConsole();
						activeConsole.setName(name);
						console = activeConsole;
					}
				}
			}
			
			if(null == console) {
				console = new ScriptConsole2(name, null, sub);
				ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});
			}
		} catch(NullPointerException npe) {
			console = null;
		}
		return console;
	}
	private ScriptConsole2(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		cmd = null;
	}
	
	private ScriptConsole2(String name, ImageDescriptor imageDescriptor, Subscription sub) {
		super(name, imageDescriptor);
		this.subscription = sub;
		cmd = null;
	}
	
	/**
	 * Creates the <code>ConsoleStreamDaemon</code> for passing data from the 
	 * <code>LoggedCommand</code>'s InputStream to the Console.
	 */
	protected void createConsoleDaemon() {
		//consoleDaemon = new ConsoleStreamDaemon(this);
	}
	
	/**
	 * Creates the <code>ErrorStreamDaemon</code> for passing data from the 
	 * <code>LoggedCommand</code>'s ErrorStream to the Console and ErrorView.
	 */
	protected void createErrorDaemon(IErrorParser parser) {
		@SuppressWarnings("unused")
		ErrorView errorView = null;
		try {
			IViewPart ivp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ErrorView.ID);
			if(null != ivp && ivp instanceof ErrorView)
				errorView = ((ErrorView)ivp);
		} catch(Exception e) {}

	//	errorDaemon = new ErrorStreamDaemon(this, errorView, parser);
	}
	
	/**
	 * Runs the provied command in this ScriptConsole instance.
	 * @param command The command and arguments to run.
	 * @param envVars The environment variables to use while running
	 * @param prompt The prompt to get the users password if needed.
	 * @param errorParser The parser to handle error messages generated by the command
	 */
	public void run(String[] command, String[] envVars, IPasswordPrompt prompt, IErrorParser errorParser) {
		if(subscription.init())
		{
		createConsoleDaemon();
		createErrorDaemon(errorParser);
		subscription.addErrorStreamListener(errorDaemon);
		subscription.addInputStreamListener(consoleDaemon);
		
		if (!subscription.isRunning())
		{
			subscription.start();
		}
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(this);
		}
		else
		{
			setName(Localization.getString("ScriptConsole.Terminated") + super.getName());
			subscription.interrupt();
			subscription.delSubscription();
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {this});
		}
	} 
	
	public void run() {
		if(subscription.init())
		{
		createConsoleDaemon();
		subscription.addInputStreamListener(consoleDaemon);
		subscription.addErrorStreamListener(consoleDaemon);
		
		if (!subscription.isRunning())
		{
			subscription.start();
		}
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(this);
		//ConsolePlugin.getDefault().getConsoleManager().
		//activate();
		}
		else
		{
			setName(Localization.getString("ScriptConsole.Terminated") + super.getName());
			subscription.interrupt();
			subscription.delSubscription();
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {this});
		}
			
	}
	
	/**
	 * Check to see if the Command is still running
	 * @return boolean representing if the command is running
	 */
	public boolean isRunning() {
		return subscription.isRunning();
	}
	
	/**
	 * Check to see if this class has already been disposed.
	 * @return boolean represneting whether or not the class has been disposed.
	 */
	public boolean isDisposed() {
		return cmd.isDisposed();
	}
	
	/**
	 * Method to allow the user to save the Commands output to a file for use latter.
	 * @param file The new file to save the output to.
	 */
	public void saveStream(File file) {
		if(isRunning())
			if(!subscription.saveLog(file))
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Localization.getString("ScriptConsole.Problem"), Localization.getString("ScriptConsole.ErrorSavingLog"));
	}
	
	/**
	 * Gets the command that is running in this console, or null if there is no running command.
	 * @return The <code>LoggedCommand</code> that is running in this console.
	 */
	public LoggedCommand getCommand() {
		return cmd;
	}
	
	public String getOutput() {
		return subscription.getOutput();
	}
	
	/**
	 * Stops the running command and the associated listeners.
	 */
	public synchronized void stop() {
		if(isRunning()) {
			setName(Localization.getString("ScriptConsole.Terminated") + super.getName());
			subscription.interrupt();
			subscription.delSubscription();
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {this});
			
		}
	}
	
	/**
	 * Disposes of all internal references in the class. No method should be called after this.
	 */
	public void dispose() {
		if(!isDisposed()) {
			if(null != subscription)
				subscription.dispose();
			subscription = null;
			if(null != errorDaemon)
				errorDaemon.dispose();
			errorDaemon = null;
			if(null != consoleDaemon)
				consoleDaemon.dispose();
			consoleDaemon = null;
		}
	}

	/**
	 * Changes the name displayed on this console.
	 * @param name The new name to display on the console.
	 */
	public void setName(String name) {
		try {
			super.setName(name);
			if(null != ConsolePlugin.getDefault())
				ConsolePlugin.getDefault().getConsoleManager().refresh(this);
		} catch(Exception e) {}
	}
	
	private LoggedCommand cmd;
	
	private ErrorStreamDaemon errorDaemon;
	private ConsoleStreamDaemon consoleDaemon;
	private Subscription subscription;
}