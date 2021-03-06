/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.viewers.model.provisional;

/**
 * An adapter used to create column presentations.
 *
 * @see IColumnPresentation
 * @since 3.2
 */
public interface IColumnPresentationFactory {

	/**
	 * Constructs and returns the column presentation for the given presentation
	 * context (view) and input element, or <code>null</code> of none.
	 *
	 * @param context presentation context
	 * @param element the input element
	 * @return column presentation or <code>null</code>
	 */
	public IColumnPresentation createColumnPresentation(IPresentationContext context, Object element);

	/**
	 * Returns the type of column presentation to be used for the given context and object
	 * or <code>null</code> if none. Allows a previous column presentation to be re-used if
	 * it has not changed type.
	 *
	 * @param context presentation context
	 * @param element input element
	 * @return column presentation id or <code>null</code>
	 */
	public String getColumnPresentationId(IPresentationContext context, Object element);
}
