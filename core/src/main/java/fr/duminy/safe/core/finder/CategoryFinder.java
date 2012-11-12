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
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Password;

public class CategoryFinder implements Finder<CategoryFinderResult> {
	private final String name;
	private final List<Category> categories = new ArrayList<Category>();
	
	CategoryFinder(String name) {
		this.name = name;
	}
	
	@Override
	public boolean visit(Category category) {
		if ((name == null) || name.equals(category.getName())) {
			categories.add(category);
		}
		return true;
	}

	@Override
	public boolean visit(Password p) {
		return true;
	}

	@Override
	public CategoryFinderResult getResult() {
		return new CategoryFinderResult();
	}
	
	public class CategoryFinderResult implements FinderResult {
		public List<Category> getFoundCategories() {
			return categories;
		}
		
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder("CategoryFinderResult[");
			boolean begin = true;
			for (Category category : categories) {
				if (!begin) {
					result.append(", ");					
				}
				begin = false;
				
				result.append(category.getName());
			}
			result.append(']');
			return result.toString();
		}
	}
}
