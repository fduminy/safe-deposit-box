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

import static fr.duminy.safe.core.TestUtils.PASSWORD_WITH_PATH_COMPARATOR;
import static fr.duminy.safe.core.Utils.array;

import java.util.Comparator;

import org.fest.assertions.api.AbstractAssert;

import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;
import fr.duminy.safe.core.model.Category;

public class PasswordWithPathAssert extends AbstractAssert<PasswordWithPathAssert, PasswordWithPath> {
	protected PasswordWithPathAssert(PasswordWithPath actual) {
		super(actual, PasswordWithPathAssert.class);
		super.usingComparator(PASSWORD_WITH_PATH_COMPARATOR);
	}

	@Override
	public PasswordWithPathAssert usingComparator(
			Comparator<? super PasswordWithPath> customComparator) {
		throw new UnsupportedOperationException(
				"custom Comparator is not supported for PasswordWithPath comparison");
	}

	public void hasPath(Category... expectedPath) {
		CategoryAssert.assertSamePath(actual.getPath(), expectedPath);
	}

	public void hasSamePathAs(Category expectedCategory) {
		hasPath(array(expectedCategory.getPath()));
	}
}
