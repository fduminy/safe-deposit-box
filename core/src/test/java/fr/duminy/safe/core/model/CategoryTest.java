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
import static fr.duminy.safe.core.TestDataUtils.CHILD2;
import static fr.duminy.safe.core.TestDataUtils.GRANDCHILD;
import static fr.duminy.safe.core.TestDataUtils.ROOT;
import static fr.duminy.safe.core.TestDataUtils.buildCategoryTree;
import static fr.duminy.safe.core.TestDataUtils.passwordWithPaths;
import static fr.duminy.safe.core.TestUtils.array;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Arrays;
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
	public void testRename_SameNameAsChild() {
		Exception e = testRename(CHILD, GRANDCHILD.getCategoryName(), passwordWithPaths(GRANDCHILD.getPasswordNames(), ROOT, GRANDCHILD, GRANDCHILD));
		assertThat(e).isNull();
	}
	
	@Test
	public void testRename_SameNameAsParent() {
		// we can't rename CHILD because test methods won't work
		Exception e = testRename(GRANDCHILD, CHILD.getCategoryName());
		assertThat(e).isNull();
	}
	
	@Test
	public void testRename_SameName() {
		Exception e = testRename(CHILD, CHILD.getCategoryName());		
		assertThat(e).isNull();
	}
	
	@Test
	public void testRename_DuplicateName() {
		String child2 = CHILD2.getCategoryName();
		Exception e = testRename(CHILD, child2);
		assertThat(e).isExactlyInstanceOf(DuplicateNameException.class).hasMessageContaining("'" + child2 + "'");
	}
	
	@Test
	public void testRename_Root() {
		Exception e = testRename(ROOT, null);		
		assertThat(e).isNull();
	}
	
	@Test
	public void testRename_Child() {
		Exception e = testRename(CHILD, null);		
		assertThat(e).isNull();
	}
	
	@Test
	public void testRename_GrandChild() {
		Exception e = testRename(GRANDCHILD, null);		
		assertThat(e).isNull();
	}
	
	private DuplicateNameException testRename(Node node, String forcedNewName, PasswordWithPath... additionalExpectedPasswords) {
		DuplicateNameException result = null;
		
		Map<Node, Category> allCategories = new HashMap<Node, Category>();
		Category root = buildCategoryTree(allCategories);
		
		// memorize initial state
		State initialNodeState = new State(allCategories, node, additionalExpectedPasswords);
		State initialRootState = new State(allCategories, ROOT);
		
		// rename
		String name2 = (forcedNewName == null) ? initialNodeState.name + "Renamed" : forcedNewName;
		Category category2;
		try {
			category2 = initialNodeState.category.rename(name2);
		} catch (DuplicateNameException dne) {
			result = dne;
			
			category2 = initialNodeState.category;
			name2 = initialNodeState.name; // not renamed
		}
		
		// check final state
		assertThat(initialNodeState.category.getName()).isEqualTo(initialNodeState.name);
		
		if (name2.equals(initialNodeState.name)) {
			assertThat(category2).isSameAs(initialNodeState.category);
		} else {		
			assertThat(initialNodeState.category).hasPath(initialNodeState.category);
			assertThat(initialNodeState.category).hasNoPassword().containsExactly(initialNodeState.category);				
			assertThat(category2).isNotNull().isNotSameAs(initialNodeState.category);		
			assertThat(category2.getName()).isEqualTo(name2);
		}
		
		Category[] expectedPath = node.buildExpectedPath(allCategories);
		expectedPath[expectedPath.length - 1] = category2;
		assertThat(category2).hasPath(expectedPath);
			
		checkFinalState(category2, initialNodeState, initialNodeState.category, category2);
		if (node != ROOT) {
			checkFinalState(root, initialRootState, initialNodeState.category, category2);
		}
		
		return result;
	}

	private void checkFinalState(Category category, State initialState, Category beforeRename, Category afterRename) {
		Transformer<PasswordWithPath> passwordTransformer = ROOT.getCategoryName().equals(category.getName()) ? null : replaceEndOfPathBy(category);
		assertThat(category).usingPasswordTransformer(passwordTransformer).hasPasswords(initialState.passwords);		
		assertThat(category).usingCategoryTransformer(replace(beforeRename, afterRename)).hasCategories(initialState.categories);
	}
	
	private static class State {
		private final Category category;
		private final String name;
		private final PasswordWithPath[] passwords;
		private final Category[] categories;
		public State(Map<Node, Category> allCategories, Node node, PasswordWithPath... additionalExpectedPasswords) {
			category = allCategories.get(node);
			List<PasswordWithPath> pwds = Finders.findPassword(category, null, true).getPasswordsWithPath();
			pwds.addAll(Arrays.asList(additionalExpectedPasswords));
			passwords = array(pwds, PasswordWithPath.class);
			categories = array(Finders.findCategory(category, null).getFoundCategories());
			name = category.getName();
		}
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
