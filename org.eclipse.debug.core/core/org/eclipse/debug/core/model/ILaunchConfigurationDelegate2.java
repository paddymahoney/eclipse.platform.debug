/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Optional enhancements to the launch configuration delegate interface.
 * Allows launch delegates to abort a launch, build relevant projects in
 * the workspace before a launch, and create the launch object to be used
 * in a launch.
 * <p>
 * Clients implementing <code>ILaunchConfigurationDelegate</code> may also
 * implement this interface.
 * </p>
 * @since 3.0
 */
public interface ILaunchConfigurationDelegate2 extends ILaunchConfigurationDelegate {

	/**
	 * Returns a launch object to use when launching the given launch
	 * configuration in the given mode, or <code>null</code> if a new default
	 * launch object should be created by the debug platform. If a launch object
	 * is returned, its launch mode must match that of the mode specified in
	 * this method call.
	 *
	 * @param configuration the configuration being launched
	 * @param mode the mode the configuration is being launched in
	 * @return a launch object or <code>null</code>
	 * @throws CoreException if unable to launch
	 */
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException;

	/**
	 * Optionally performs any required building before launching the given
	 * configuration in the specified mode, and returns whether the debug platform
	 * should perform an incremental workspace build before the launch continues.
	 * If <code>false</code> is returned the launch will proceed without further
	 * building, and if <code>true</code> is returned an incremental build will
	 * be performed on the workspace before launching.
	 * <p>
	 * This method is only called if the launch is invoked with flag indicating
	 * building should take place before the launch. This is done via the
	 * method
	 * <code>ILaunchConfiguration.launch(String mode, IProgressMonitor monitor, boolean build)</code>.
	 * </p>
	 * @param configuration the configuration being launched
	 * @param mode the mode the configuration is being launched in
	 * @param monitor progress monitor, or <code>null</code>. A cancelable progress monitor is provided by the Job
	 *  framework. It should be noted that the setCanceled(boolean) method should never be called on the provided
	 *  monitor or the monitor passed to any delegates from this method; due to a limitation in the progress monitor
	 *  framework using the setCanceled method can cause entire workspace batch jobs to be canceled, as the canceled flag
	 *  is propagated up the top-level parent monitor. The provided monitor is not guaranteed to have been started.
	 * @return whether the debug platform should perform an incremental workspace
	 *  build before the launch
	 * @throws CoreException if an exception occurs while building
	 */
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns whether a launch should proceed. This method is called after
	 * <code>preLaunchCheck()</code> and <code>buildForLaunch()</code> providing
	 * a final chance for this launch delegate to abort a launch if required.
	 * For example, a delegate could cancel a launch if it discovered compilation
	 * errors that would prevent the launch from succeeding.
	 *
	 * @param configuration the configuration being launched
	 * @param mode launch mode
	 * @param monitor progress monitor, or <code>null</code>. A cancelable progress monitor is provided by the Job
	 *  framework. It should be noted that the setCanceled(boolean) method should never be called on the provided
	 *  monitor or the monitor passed to any delegates from this method; due to a limitation in the progress monitor
	 *  framework using the setCanceled method can cause entire workspace batch jobs to be canceled, as the canceled flag
	 *  is propagated up the top-level parent monitor. The provided monitor is not guaranteed to have been started.
	 * @return whether the launch should proceed
	 * @throws CoreException if an exception occurs during final checks
	 */
	public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns whether a launch should proceed. This method is called first
	 * in the launch sequence providing an opportunity for this launch delegate
	 * to abort the launch.
	 *
	 * @param configuration configuration being launched
	 * @param mode launch mode
	 * @param monitor progress monitor, or <code>null</code>. A cancelable progress monitor is provided by the Job
	 *  framework. It should be noted that the setCanceled(boolean) method should never be called on the provided
	 *  monitor or the monitor passed to any delegates from this method; due to a limitation in the progress monitor
	 *  framework using the setCanceled method can cause entire workspace batch jobs to be canceled, as the canceled flag
	 *  is propagated up the top-level parent monitor. The provided monitor is not guaranteed to have been started.
	 * @return whether the launch should proceed
	 * @throws CoreException if an exception occurs while performing pre-launch checks
	 */
	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException;
}
