/*
 * Copyright (C) 2010-2022 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core;

import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;

/**
 * A value holder that can be fetched and set in the presence of a
 * {@link SecurityContext}.
 * 
 *
 */
public interface Value<T> {

	/**
	 * Sets the current value of this value holder.
	 * 
	 * @param securityContext the security context
	 * @param value the value to be set
	 * @throws FrameworkException 
	 */
	public void set(SecurityContext securityContext, T value) throws FrameworkException;
	
	/**
	 * Gets the current value of this value holder.
	 * 
	 * @param securityContext the security context
	 * @return the current value
	 */
	public T get(SecurityContext securityContext);
}
