/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.launchConfigurations;

import com.ibm.icu.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;


public class CompileErrorProjectPromptStatusHandler implements IStatusHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IStatusHandler#handleStatus(org.eclipse.core.runtime.IStatus, java.lang.Object)
	 */
	public Object handleStatus(IStatus status, Object source) throws CoreException {
		ILaunchConfiguration config = null;
		List projects = new ArrayList();
		
		if (source instanceof List) {
			List args = (List) source;
			Iterator iterator = args.iterator();
			while (iterator.hasNext()) {
				Object arg = iterator.next();
				if (arg instanceof ILaunchConfiguration) {
					config = (ILaunchConfiguration) arg;
					if (DebugUITools.isPrivate(config)) {
						return Boolean.TRUE;
					}
				} else if (arg instanceof IProject) {
					projects.add(arg);
				}
			}
		}	
		Shell shell = DebugUIPlugin.getShell();
		
		StringBuffer projectMessage = new StringBuffer();
		for (int i = 0; i < projects.size(); i++) {
			if (i > 0) {
				projectMessage.append(", "); //$NON-NLS-1$
			}
			projectMessage.append(((IProject)projects.get(i)).getName());
		}
		String plural = ""; //$NON-NLS-1$
		if(projects.size() > 1) {
			plural = "s"; //$NON-NLS-1$
		}
		String title =  MessageFormat.format(LaunchConfigurationsMessages.CompileErrorPromptStatusHandler_0, new String[] {plural}); 
		String message = MessageFormat.format(LaunchConfigurationsMessages.CompileErrorPromptStatusHandler_2, new String[]{plural, projectMessage.toString()}); 
		IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore(); 
		
		String pref = store.getString(IInternalDebugUIConstants.PREF_CONTINUE_WITH_COMPILE_ERROR);
		if (pref != null) {
			if (pref.equals(MessageDialogWithToggle.ALWAYS)) {
				return Boolean.TRUE;
			}
		}
		MessageDialogWithToggle dialog = new MessageDialogWithToggle(shell, 
				title, 
				null, 
				message, 
				MessageDialog.QUESTION,
				new String[] {LaunchConfigurationsMessages.CompileErrorProjectPromptStatusHandler_0, IDialogConstants.CANCEL_LABEL}, 
				0,
				LaunchConfigurationsMessages.CompileErrorProjectPromptStatusHandler_1,
				false);
        if (dialog.open() == IDialogConstants.INTERNAL_ID) {
        	if(dialog.getToggleState()) {
        		store.setValue(IInternalDebugUIConstants.PREF_CONTINUE_WITH_COMPILE_ERROR, MessageDialogWithToggle.ALWAYS);
        	}
            return Boolean.TRUE;
        } 
        else {
            return Boolean.FALSE;
        }
	}
}