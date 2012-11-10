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
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;

import org.fest.assertions.api.AbstractAssert;

import fr.duminy.safe.core.TestUtils;
import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;

public class ModelAssert extends AbstractAssert<ModelAssert, Model> {
	protected ModelAssert(Model actual) {
		super(actual, ModelAssert.class);
		super.usingComparator(ModelComparator.INSTANCE);
	}

	@Override
	public ModelAssert usingComparator(
			Comparator<? super Model> customComparator) {
		throw new UnsupportedOperationException(
				"custom Comparator is not supported for Model comparison");
	}
	
	public ModelAssert isEmpty() {
    	// checks model's global password list
    	assertThat(actual.getPasswords()).isNotNull().isEmpty();
    	
    	// checks model's categories
    	assertThat(actual.getRootCategory()).hasNoPassword();
    	return this;
	}
	
	public ModelAssert containsExactly(Password... passwords) {
    	// checks model's global password list
    	assertThat(actual.getPasswords()).isNotNull().hasSize(passwords.length).usingElementComparator(PASSWORD_COMPARATOR).containsExactly(passwords);    	
    	
    	// checks model's categories
    	assertThat(actual.getRootCategory()).containsExactly(passwords);
    	return this;
    }
	
	private static class ModelComparator implements Comparator<Model> {
		private static final ModelComparator INSTANCE = new ModelComparator(); 
		
		@Override
		public int compare(Model o1, Model o2) {
			List<Category> categories1 = Finders.findCategory(o1.getRootCategory(), null).getFoundCategories();
			List<Category> categories2 = Finders.findCategory(o2.getRootCategory(), null).getFoundCategories();
			
			int result = TestUtils.compare(categories1, categories2, CATEGORY_COMPARATOR);
			if (result != 0) {
				return result;
			}
					
			List<PasswordWithPath> passwords1 = Finders.findPassword(o1.getRootCategory(), null, true).getPasswordsWithPath();
			List<PasswordWithPath> passwords2 = Finders.findPassword(o2.getRootCategory(), null, true).getPasswordsWithPath();
			
			return TestUtils.compare(passwords1, passwords2, PASSWORD_WITH_PATH_COMPARATOR);
		}
	}
}
