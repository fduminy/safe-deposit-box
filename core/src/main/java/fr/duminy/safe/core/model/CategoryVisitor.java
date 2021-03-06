/**
 * Safe Deposit Box is a software managing your passwords in a safe place.
 *
 * Copyright (C) 2012-2012 Fabien DUMINY (fduminy at jnode dot org)
 *
 * Safe Deposit Box is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Safe Deposit Box is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package fr.duminy.safe.core.model;

/**
 * Interface that a visitor must implement to be able to visit a {@link Category}.
 * @author fabien
 *
 */
public interface CategoryVisitor {

	/**
	 * Visit the given category.
	 * @param category The category to visit.
	 * @return true if the visit should stop, false otherwise.
	 */
    boolean visit(Category category);

    /**
     * Visit the given password.
     * @param password The password to visit.
	 * @return true if the visit should stop, false otherwise.
     */
    boolean visit(Password password);

}
