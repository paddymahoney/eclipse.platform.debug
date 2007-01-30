/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.launchConfigurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.DefaultLabelProvider;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.internal.ui.SWTUtil;
import org.eclipse.debug.internal.ui.contextlaunching.ContextRunner;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

import com.ibm.icu.text.MessageFormat;

/**
 * Specialized dialog for showing/selecting a specific launch shortcut extension, and allowing it
 * to be marked to be set as the default
 * 
 * @see {@link org.eclipse.debug.internal.ui.actions.ContextLaunchingAction}
 * 
 * @since 3.3
 * EXPERIMENTAL
 * CONTEXTLAUNCHING
 */
public class LaunchShortcutSelectionDialog extends ListDialog {

	private static final String DIALOG_SETTINGS = IDebugUIConstants.PLUGIN_ID + ".SELECT_LAUNCH_SHORTCUT_DIALOG"; //$NON-NLS-1$;
	
	/**
	 * The list of input for the dialog
	 */
	private String fMode = null;
	private IResource fResource = null;
	private boolean fChecked = false;
	private boolean fProject = false;
	private boolean fShowProject = false;
	private boolean fShowResource = false;
	
	/**
	 * Constructor
	 * @param input
	 * @param resource
	 * @param mode
	 */
	public LaunchShortcutSelectionDialog(IResource resource, String mode, boolean showresource, boolean showproject) {
		super(DebugUIPlugin.getShell());
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fResource = resource;
		fMode = mode;
		fShowProject = showproject;
		fShowResource = showresource;
		ILaunchMode lmode = DebugPlugin.getDefault().getLaunchManager().getLaunchMode(fMode);
		String modename = fMode;
		if (lmode != null) {
			modename = DebugUIPlugin.removeAccelerators(lmode.getLabel());
		}
		setTitle(MessageFormat.format(LaunchConfigurationsMessages.LaunchShortcutSelectionDialog_0, new String[] {modename}));
		setAddCancelButton(true);
		setMessage(MessageFormat.format(LaunchConfigurationsMessages.LaunchShortcutSelectionDialog_1, new String[] {fMode, fResource.getName()}));
		setLabelProvider(new DefaultLabelProvider());
		setContentProvider(new ArrayContentProvider());
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite comp = (Composite) super.createContents(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(comp, IDebugHelpContextIds.SELECT_LAUNCH_METHOD_DIALOG);
		return comp;
	}

	/**
	 * @see org.eclipse.ui.dialogs.SelectionDialog#getDialogBoundsSettings()
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = DebugUIPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS);
		} 
		return section;
	}
	
	/**
	 * @see org.eclipse.ui.dialogs.ListDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getOkButton().setEnabled(false);
		getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getOkButton().setEnabled(!event.getSelection().isEmpty());
			}	
		});
	}

	/**
	 * @see org.eclipse.ui.dialogs.ListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite container) {
		Composite comp = (Composite) super.createDialogArea(container);
		try {
			List input = new ArrayList(ContextRunner.getDefault().getLaunchShortcuts(fResource));
			Button butt = null;
			if(!input.isEmpty()) {
				if(fShowResource) {
					butt = SWTUtil.createCheckButton(comp, MessageFormat.format(LaunchConfigurationsMessages.LaunchShortcutSelectionDialog_2, new String[] {fResource.getName()}), null, fChecked);
					butt.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {}
						public void widgetSelected(SelectionEvent e) {
							fChecked = ((Button)e.getSource()).getSelection();
						}
					});
				}
				if(fShowProject) {
					butt = SWTUtil.createCheckButton(comp, MessageFormat.format(LaunchConfigurationsMessages.LaunchShortcutSelectionDialog_3, new String[] {fResource.getProject().getName()}), null, false);
					butt.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {}
						public void widgetSelected(SelectionEvent e) {
							fProject = ((Button)e.getSource()).getSelection();
						}
					});
				}
			}
			getTableViewer().setInput(input);
		}
		catch(CoreException ce) {DebugUIPlugin.log(ce);}
		return comp;
	}
 
	/**
	 * Returns if the launched config from the selected shortcut should be made the default for the underlying resource
	 * @return if the launched config should be made the default
	 */
	public boolean makeDefault() {
		return fChecked;
	}
	
	/**
	 * Returns if the launched config from the selected shortcut should be made the default for the underlying resources' associated project
	 * @return if the launched config from the selected shortcut should be made the default for the underlying resources' associated project
	 */
	public boolean makeProjectDefault() {
		return fProject;
	}
}