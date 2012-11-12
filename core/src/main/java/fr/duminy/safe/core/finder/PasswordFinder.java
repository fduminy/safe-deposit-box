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

import fr.duminy.safe.core.finder.PasswordFinder.PasswordFinderResult;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Password;

public class PasswordFinder implements Finder<PasswordFinderResult> {
	private final String categoryName;
    private final String passwordName;
    private final boolean recursive;
    
	private Category currentCategory;
	private List<PasswordWithPath> foundPasswords = new ArrayList<PasswordWithPath>();
	
	/**
	 * Search
	 * @param categoryName
	 * @param passwordName
	 * @param recursive
	 */
	PasswordFinder(String categoryName, String passwordName, boolean recursive) {
		this.categoryName = categoryName;
		this.passwordName = passwordName;
		this.recursive = recursive;
	}

	@Override
	public boolean visit(Password p) {
		if ((categoryName == null) || currentCategory.getName().equals(categoryName)) {
			if ((passwordName == null) || p.getName().equals(passwordName)) {
				foundPasswords.add(new PasswordWithPath(currentCategory.getPath(), p));
			}
		}
		return true;
	}
	
	@Override
	public boolean visit(Category category) {
		currentCategory = category;
		return recursive;
	}
		
	public class PasswordFinderResult implements FinderResult {
		public List<PasswordWithPath> getPasswordsWithPath() {
			return foundPasswords;
		}		
		
		public List<Password> getPasswords() {
			return Finders.getPasswords(foundPasswords);
		}
	}
	
	public static class PasswordWithPath {
		private final Password password;
		private final List<Category> path;
		public PasswordWithPath(List<Category> path, Password password) {
			super();
			this.password = password;
			this.path = path;
		}
		public Password getPassword() {
			return password;
		}
		public List<Category> getPath() {
			return path;
		}
				
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder("PasswordWithPath[");
			boolean begin = true;
			for (Category category : path) {
				if (!begin) {
					result.append("/");					
				}
				begin = false;
				
				result.append(category.getName());
			}
			result.append(' ').append(password.getName());
			result.append(']');
			return result.toString();
		}
	}

	@Override
	public PasswordFinderResult getResult() {
		return new PasswordFinderResult();
	}
}
