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
import static fr.duminy.safe.core.TestUtils.PASSWORD_WITH_PATH_COMPARATOR;
import static fr.duminy.safe.core.TestUtils.allPasswords;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.internal.Objects;

import fr.duminy.safe.core.MutableInteger;
import fr.duminy.safe.core.TestUtils;
import fr.duminy.safe.core.Transformer;
import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Password;

public class CategoryAssert extends AbstractAssert<CategoryAssert, Category> {
	private Transformer<PasswordWithPath> pwdTransformer;
	private Transformer<Category> categTransformer;
	
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
	
	public CategoryAssert containsExactly(Password... passwords) {
    	assertThat(allPasswords(actual)).isNotNull().hasSize(passwords.length).usingElementComparator(PASSWORD_COMPARATOR).containsExactly(passwords);
    	return this;
	}

	public CategoryAssert hasNoPassword() {
		assertThat(allPasswords(actual)).isEmpty();
		return this;
	}

	public void containsExactly(Category... categories) {
		List<Category> categoriesAfter = Finders.findCategory(actual, null).getFoundCategories();
		assertThat(categoriesAfter).containsExactly(categories);
	}

	static void assertSamePath(List<Category> actualPath, Category... expectedPath) {
		String errorMessage = " path can't be empty";
		assertThat(actualPath).as("actual" + errorMessage).isNotEmpty();
		assertThat(expectedPath).as("expected" + errorMessage).isNotEmpty();
		
		assertThat(actualPath).as("wrong actual path").usingElementComparator(CATEGORY_COMPARATOR).containsExactly(expectedPath);
	}
	
	public void hasPath(Category... expectedPath) {
		assertSamePath(actual.getPath(), expectedPath);
	}

	public void isEqualsTo(Category other) {
		Objects.instance().assertIsLenientEqualsToByIgnoringFields(getWritableAssertionInfo(), actual, other);
	}

	public CategoryAssert usingPasswordTransformer(Transformer<PasswordWithPath> pwdTransformer) {
		this.pwdTransformer = pwdTransformer;
		return this;
	}

	public void hasPasswords(PasswordWithPath... passwords) {
		List<PasswordWithPath> actualPasswords = Finders.findPassword(actual, null, true).getPasswordsWithPath();
		int expectedTransformations = (pwdTransformer == null) ? 0 : passwords.length;
		assertThat(actualPasswords).usingElementComparator(PASSWORD_WITH_PATH_COMPARATOR).containsAll(Arrays.asList(transform(passwords, pwdTransformer, expectedTransformations)));
	}

	public CategoryAssert usingCategoryTransformer(Transformer<Category> categTransformer) {
		this.categTransformer = categTransformer;
		return this;
	}

	public void hasCategories(Category... categories) {
		List<Category> actualCategories = Finders.findCategory(actual, null).getFoundCategories();
		assertThat(actualCategories).containsAll(Arrays.asList(transform(categories, categTransformer, 1)));
	}
	
	public static <T> T[] transform(T[] items, Transformer<T> transformer, int expectedTransformations) {
		MutableInteger transformationCounter = new MutableInteger();
		T[] result = TestUtils.transform(items, transformer, transformationCounter);		
		assertThat(transformationCounter.getValue()).as("wrong number of transformations").isEqualTo(expectedTransformations);
		return result;
	}	
}
