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
package fr.duminy.safe.core.assertions;

import static fr.duminy.safe.core.TestUtils.CATEGORY_COMPARATOR;
import static fr.duminy.safe.core.TestUtils.PASSWORD_COMPARATOR;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Comparator;

import org.fest.assertions.api.AbstractAssert;

import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordFinderResult;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Password;

public class CategoryAssert extends AbstractAssert<CategoryAssert, Category> {
	protected CategoryAssert(Category actual) {
		super(actual, CategoryAssert.class);
		super.usingComparator(CATEGORY_COMPARATOR);
	}

	@Override
	public CategoryAssert usingComparator(
			Comparator<? super Category> customComparator) {
		throw new UnsupportedOperationException(
				"custom Comparator is not supported for Category comparison");
	}
	
	public CategoryAssert isEmpty() {
    	PasswordFinderResult result = Finders.findPassword(actual, null, true);
    	assertThat(result.getPasswordsWithPath()).isNotNull().isEmpty();
    	return this;
	}

	public CategoryAssert containsExactly(Password... passwords) {
    	PasswordFinderResult result = Finders.findPassword(actual, null, null, true);
    	assertThat(Finders.getPasswords(result.getPasswordsWithPath())).isNotNull().hasSize(passwords.length).usingElementComparator(PASSWORD_COMPARATOR).containsExactly(passwords);
    	return this;
	}
}
