package org.eclipse.ui.externaltools.internal.ant.dialog;

/**********************************************************************
Copyright (c) 2002 IBM Corp. and others. All rights reserved.
This file is made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
 
Contributors:
**********************************************************************/

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.internal.core.AntCorePreferences;
import org.eclipse.ant.internal.core.Task;
import org.eclipse.ant.internal.core.Type;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.externaltools.internal.model.ExternalToolsPlugin;
import org.eclipse.ui.externaltools.internal.model.IHelpContextIds;
import org.eclipse.ui.externaltools.internal.model.ToolMessages;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * Ant preference page to set the classpath, tasks, and types.
 */
public class AntPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private AntClasspathPage jarsPage;
	private AntTasksPage tasksPage;
	private AntTypesPage typesPage;
	
	/**
	 * Creates the preference page
	 */
	public AntPreferencePage() {
		setDescription(ToolMessages.getString("AntPreferencePage.description")); //$NON-NLS-1$
		setPreferenceStore(ExternalToolsPlugin.getDefault().getPreferenceStore());
	}
	
	/* (non-Javadoc)
	 * Method declared on IWorkbenchPreferencePage.
	 */
	public void init(IWorkbench workbench) {
	}
	
	/* (non-Javadoc)
	 * Method declared on PreferencePage.
	 */
	protected Control createContents(Composite parent) {
		WorkbenchHelp.setHelp(parent, IHelpContextIds.ANT_PREFERENCE_PAGE);

		TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		jarsPage = new AntClasspathPage(this);
		jarsPage.createTabItem(folder);
		tasksPage = new AntTasksPage(this);
		tasksPage.createTabItem(folder);
		typesPage = new AntTypesPage(this);
		typesPage.createTabItem(folder);

		AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();
		jarsPage.setInput(Arrays.asList(prefs.getCustomURLs()));
		tasksPage.setInput(Arrays.asList(prefs.getCustomTasks()));
		typesPage.setInput(Arrays.asList(prefs.getCustomTypes()));

		return folder;
	}
	
	/* (non-Javadoc)
	 * Method declared on PreferencePage.
	 */
	protected void performDefaults() {
		super.performDefaults();
		
		AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();
		jarsPage.setInput(Arrays.asList(prefs.getDefaultCustomURLs()));
		tasksPage.setInput(Arrays.asList(prefs.getCustomTasks()));
		typesPage.setInput(Arrays.asList(prefs.getCustomTypes()));
	}
	
	/* (non-Javadoc)
	 * Method declared on PreferencePage.
	 */
	public boolean performOk() {
		AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();
		
		List contents = jarsPage.getContents();
		if (contents != null) {
			URL[] urls = (URL[]) contents.toArray(new URL[contents.size()]);
			prefs.setCustomURLs(urls);
		}
		
		contents = tasksPage.getContents();
		if (contents != null) {
			Task[] tasks = (Task[]) contents.toArray(new Task[contents.size()]);
			prefs.setCustomTasks(tasks);
		}
		
		contents = typesPage.getContents();
		if (contents != null) {
			Type[] types = (Type[]) contents.toArray(new Type[contents.size()]);
			prefs.setCustomTypes(types);
		}
		
		return super.performOk();
	}
	
	/**
	 * Sets the <code>GridData</code> on the specified button to
	 * be one that is spaced for the current dialog page units.
	 * 
	 * @param button the button to set the <code>GridData</code>
	 * @return the <code>GridData</code> set on the specified button
	 */
	/*package*/ GridData setButtonGridData(Button button) {
		return setButtonLayoutData(button);
	}
}