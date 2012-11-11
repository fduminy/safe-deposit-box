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
import static fr.duminy.safe.core.TestDataUtils.ROOT;
import static fr.duminy.safe.core.TestDataUtils.WRONG_NAME;
import static fr.duminy.safe.core.TestDataUtils.allPasswordNames;
import static fr.duminy.safe.core.TestDataUtils.buildCategoryTree;
import static fr.duminy.safe.core.TestDataUtils.node;
import static fr.duminy.safe.core.TestUtils.PASSWORD_WITH_PATH_COMPARATOR;
import static fr.duminy.safe.core.TestUtils.array;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static fr.duminy.safe.core.finder.Finders.getPasswords;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.duminy.safe.core.TestDataUtils.Node;
import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordFinderResult;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;

@RunWith(Theories.class)
public class PasswordFinderTest  {
	@DataPoint public static final Data ROOT_PASSWORD = data(ROOT, true, true);
	@DataPoint public static final Data CHILD_PASSWORD = data(CHILD, true, true);
	@DataPoint public static final Data ROOT_PASSWORD_NON_RECURSIVE = data(ROOT, true, false);
	@DataPoint public static final Data CHILD_PASSWORD_NON_RECURSIVE = data(CHILD, false, false);

	@DataPoint public static final DataAllCategories ROOT_PASSWORD_ALL_CATEGORIES = dataAllCategories(ROOT, true, true, allPasswordNames());
	@DataPoint public static final DataAllCategories ROOT_PASSWORD_NON_RECURSIVE_ALL_CATEGORIES = dataAllCategories(ROOT, true, false, ROOT.getPasswordNames());
	
	@DataPoint public static final Data ROOT_PASSWORD_WRONG_CATEGORY = data(node(WRONG_NAME, ROOT.getPasswordNames()[0]), false, true);
	@DataPoint public static final Data CHILD_PASSWORD_WRONG_CATEGORY = data(node(WRONG_NAME, CHILD.getPasswordNames()[0]), false, true);

	@Theory
	public void testFindAllInAllCategories(DataAllCategories data) {
		testFindAll(data);
	}
	
	@Theory
	public void testFindAllInGivenCategory(Data data) {
		testFindAll(data);
	}
	
	@Theory
	public void testFindOne(Data data) {
		Map<Node, Category> categoriesMap = new HashMap<Node, Category>(); 
		Category root = buildCategoryTree(categoriesMap);		
		String passwordName = data.node.getPasswordNames()[0];
		
		PasswordFinderResult result = findPassword(data, root, passwordName, data.mustBeFound ? 1 : 0);
		
		if (data.mustBeFound) {
			// checks password name
			assertThat(result.getPasswords().get(0).getName()).isEqualTo(passwordName);
			assertThat(result.getPasswordsWithPath().get(0).getPassword().getName()).isEqualTo(passwordName);
			
			// checks password path
			Category expectedCategory = categoriesMap.get(data.getNode());
			assertThat(result.getPasswordsWithPath().get(0)).hasSamePathAs(expectedCategory);
		}
	}

	private void testFindAll(AbstractData data) {
		Category root = buildCategoryTree();		
		String passwordName = null;
		int expectedSize = data.mustBeFound ? data.getExpectedPasswordNames().length : 0;
		
		PasswordFinderResult result = findPassword(data, root, passwordName, expectedSize);
		
		if (data.mustBeFound) {
			List<String> names = new ArrayList<String>(expectedSize);
			for (Password p : result.getPasswords()) {
				names.add(p.getName());
			}
			assertThat(names).containsAll(Arrays.asList(data.getExpectedPasswordNames()));
		}
	}
	
	private PasswordFinderResult findPassword(AbstractData data, Category start,
			String passwordName, int expectedSize) {
		String categoryName = data.getCategoryName();
		PasswordFinderResult result = Finders.findPassword(start, categoryName, passwordName, data.isRecursive());
		
		assertThat(result).isNotNull();
		if (expectedSize > 0) {
			assertThat(result.getPasswords()).hasSize(expectedSize);
			assertThat(result.getPasswordsWithPath()).hasSize(expectedSize);
			
			assertThat(getPasswords(result.getPasswordsWithPath())).isEqualTo(result.getPasswords());
		} else {
			assertThat(result.getPasswords()).isEmpty();
			assertThat(result.getPasswordsWithPath()).isEmpty();
		}

		// check queries that must be equivalent to the above call to Finders.findPassword
		if ((categoryName != null) && start.getName().equals(categoryName)) {
			PasswordFinderResult result2 = Finders.findPassword(start, passwordName, data.isRecursive());
			assertThat(result2.getPasswords()).isEqualTo(result.getPasswords());
			
			PasswordWithPath[] passwords2 = array(result.getPasswordsWithPath(), PasswordWithPath.class);
			assertThat(result2.getPasswordsWithPath()).usingElementComparator(PASSWORD_WITH_PATH_COMPARATOR).containsExactly(passwords2);
		}
		
		return result;
	}
	
	private static Data data(Node node, boolean mustBeFound, boolean recursive) {
		return new Data(node, mustBeFound, recursive);
	}
	private static DataAllCategories dataAllCategories(Node node, boolean mustBeFound, boolean recursive, String[] expectedPasswordNames) {
		return new DataAllCategories(node, mustBeFound, recursive, expectedPasswordNames);
	}
	private static abstract class AbstractData {
		final Node node;
		final boolean mustBeFound;
		final boolean recursive;
		public AbstractData(Node node, boolean mustBeFound, boolean recursive) {
			super();
			this.node = node;
			this.mustBeFound = mustBeFound;
			this.recursive = recursive;
		}		
		
		public String[] getExpectedPasswordNames() {
			return node.getPasswordNames();
		}

		public Node getNode() {
			return node;
		}
		
		public String getCategoryName() {
			return node.getCategoryName();
		}
		
		public boolean isRecursive() {
			return recursive;
		}
	}
	private static class Data extends AbstractData {
		public Data(Node node, boolean mustBeFound, boolean recursive) {
			super(node, mustBeFound, recursive);
		}
	}
	private static class DataAllCategories extends AbstractData {
		private final String[] expectedPasswordNames;
		public DataAllCategories(Node node, boolean mustBeFound, boolean recursive, String[] expectedPasswordNames) {
			super(node, mustBeFound, recursive);
			this.expectedPasswordNames = expectedPasswordNames;
		}		
		
		@Override
		public Node getNode() {
			return null;
		}

		public String getCategoryName() {
			return null;
		}
		
		@Override
		public String[] getExpectedPasswordNames() {
			return expectedPasswordNames;
		}
	}
}
