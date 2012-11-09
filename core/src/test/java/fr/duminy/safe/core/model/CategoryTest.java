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

import static fr.duminy.safe.core.TestDataUtils.buildCategoryTree;
import static fr.duminy.safe.core.TestDataUtils.ROOT_INDEX;
import static fr.duminy.safe.core.TestDataUtils.CHILD_INDEX;
import static fr.duminy.safe.core.TestDataUtils.GRANDCHILD_INDEX;
import static fr.duminy.safe.core.TestUtils.array;
import static fr.duminy.safe.core.TestUtils.PASSWORD_WITH_PATH_COMPARATOR;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;

public class CategoryTest {
	@Test
	public void testRename_Root() {
		testRename(ROOT_INDEX);		
	}
	
	@Test
	public void testRename_Child() {
		testRename(CHILD_INDEX);		
	}
	
	@Test
	public void testRename_GrandChild() {
		testRename(GRANDCHILD_INDEX);		
	}
	
	private void testRename(int categoryIndex) {
		List<Category> allCategories = new ArrayList<Category>(); 
		buildCategoryTree(allCategories, null);
		
		// memorize initial state
		Category category = allCategories.get(categoryIndex);
		PasswordWithPath[] passwords = array(Finders.findPassword(category, null, true).getPasswordsWithPath());
		Category[] categories = array(Finders.findCategory(category, null).getFoundCategories());
		String name = category.getName();
		
		// rename
		String name2 = name + "Renamed";
		Category category2 = category.rename(name2);
		
		// check final state
		assertThat(category.getName()).isEqualTo(name);
		assertThat(category.getPath()).containsExactly(category);
		List<PasswordWithPath> passwordsAfter = Finders.findPassword(category, null, true).getPasswordsWithPath();
		assertThat(passwordsAfter).isEmpty();
		List<Category> categoriesAfter = Finders.findCategory(category, null).getFoundCategories();
		assertThat(categoriesAfter).containsExactly(category);
		
		assertThat(category2).isNotNull().isNotSameAs(category);		
		assertThat(category2.getName()).isEqualTo(name2);
		
		List<Category> expectedPathAsList = new ArrayList<Category>(allCategories.subList(0, categoryIndex));
		expectedPathAsList.add(category2);
		Category[] expectedPath = array(expectedPathAsList);
		assertThat(category2.getPath()).containsExactly(expectedPath);
				
		for (int i = 0; i < passwords.length; i++) {
			List<Category> path = passwords[i].getPath();
			path.set(path.size() - 1, category2);
		}
		List<PasswordWithPath> passwords2 = Finders.findPassword(category2, null, true).getPasswordsWithPath();
		assertThat(passwords2).usingElementComparator(PASSWORD_WITH_PATH_COMPARATOR).containsExactly(passwords);
		List<Category> categories2 = Finders.findCategory(category2, null).getFoundCategories();
		boolean replaced = false;
		for (int i = 0; i < categories.length; i++) {
			if (categories[i].getName().equals(category.getName())) {
				categories[i] = category2;
				replaced = true;
				break;
			}
		}
		assertThat(replaced).isTrue();
		assertThat(categories2).containsExactly(categories);
	}
}
