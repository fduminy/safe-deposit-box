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
package fr.duminy.safe.core.finder;

import java.util.ArrayList;
import java.util.List;

import fr.duminy.safe.core.finder.CategoryFinder.CategoryFinderResult;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordFinderResult;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Password;

public class Finders {
	public static CategoryFinderResult findCategory(Category start, String name) {
		return find(start, new CategoryFinder(name));
	}

	public static PasswordFinderResult findPassword(Category start, String passwordName, boolean recursive) {
		return findPassword(start, start.getName(), passwordName, recursive);
	}
	
	public static PasswordFinderResult findPassword(Category start, String categoryName, String passwordName, boolean recursive) {
		return find(start, new PasswordFinder(categoryName, passwordName, recursive));
	}
	
	public static List<Password> getPasswords(List<PasswordWithPath> passwords) {
		List<Password> result = new ArrayList<Password>();
		for (PasswordWithPath pf : passwords) {
			result.add(pf.getPassword());
		}
		return result;
	}

	private static <T extends FinderResult> T find(Category start, Finder<T> finder) {
		start.accept(finder);
		return finder.getResult();
	}
	
	private Finders() {		
	}
}
