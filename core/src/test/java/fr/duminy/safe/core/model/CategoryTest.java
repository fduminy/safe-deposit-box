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

import static fr.duminy.safe.core.TestDataUtils.CHILD;
import static fr.duminy.safe.core.TestDataUtils.GRANDCHILD;
import static fr.duminy.safe.core.TestDataUtils.ROOT;
import static fr.duminy.safe.core.TestDataUtils.buildCategoryTree;
import static fr.duminy.safe.core.TestUtils.array;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.duminy.safe.core.MutableInteger;
import fr.duminy.safe.core.TestDataUtils.Node;
import fr.duminy.safe.core.Transformer;
import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;

public class CategoryTest {
	@Test
	public void testRename_Root() {
		testRename(ROOT);		
	}
	
	@Test
	public void testRename_Child() {
		testRename(CHILD);		
	}
	
	@Test
	public void testRename_GrandChild() {
		testRename(GRANDCHILD);		
	}
	
	private void testRename(Node node) {
		Map<Node, Category> allCategories = new HashMap<Node, Category>();
		buildCategoryTree(allCategories);
		
		// memorize initial state
		final Category category = allCategories.get(node);
		PasswordWithPath[] passwords = array(Finders.findPassword(category, null, true).getPasswordsWithPath(), PasswordWithPath.class);
		Category[] categories = array(Finders.findCategory(category, null).getFoundCategories());
		String name = category.getName();
		
		// rename
		String name2 = name + "Renamed";
		final Category category2 = category.rename(name2);
		
		// check final state
		assertThat(category.getName()).isEqualTo(name);
		assertThat(category).hasPath(category);
		assertThat(category).hasNoPassword().containsExactly(category);
		
		assertThat(category2).isNotNull().isNotSameAs(category);		
		assertThat(category2.getName()).isEqualTo(name2);
		
		Category[] expectedPath = node.buildExpectedPath(allCategories);
		expectedPath[expectedPath.length - 1] = category2;
		assertThat(category2).hasPath(expectedPath);
			
		assertThat(category2).usingPasswordTransformer(replaceEndOfPathBy(category2)).hasPasswords(passwords);
		
		assertThat(category2).usingCategoryTransformer(replace(category, category2)).hasCategories(categories);
	}

	private Transformer<PasswordWithPath> replaceEndOfPathBy(final Category category) {
		return new Transformer<PasswordWithPath>() {
			@Override
			public PasswordWithPath transform(PasswordWithPath password, MutableInteger transformationCounter) {
				List<Category> path = password.getPath();
				path.set(path.size() - 1, category);
				transformationCounter.increment();
				return password;
			}			
		};
	}
	
	private Transformer<Category> replace(final Category from, final Category to) {
		return new Transformer<Category>() {
			@Override
			public Category transform(Category c, MutableInteger transformationCounter) {
				if (c.getName().equals(from.getName())) {
					c = to;
					transformationCounter.increment();
				}
				return c;
			}			
		};
	}
}
