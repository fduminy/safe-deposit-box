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
import static fr.duminy.safe.core.TestDataUtils.buildCategoryTree;
import static fr.duminy.safe.core.TestUtils.category;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.duminy.safe.core.TestDataUtils.Node;
import fr.duminy.safe.core.finder.Finders;

public class CategoryFinderTest {
	@Test
	public void testFindAll() {
		Map<Node, Category> categories = new HashMap<Node, Category>(); 
		Category root = buildCategoryTree(categories);		
		List<Category> result = Finders.findCategory(root, null).getFoundCategories();
		
		assertThat(result).isNotNull().containsAll(categories.values());
	}
	
	@Test
	public void testFindRoot() {
		Category root = buildCategoryTree();		
		List<Category> result = Finders.findCategory(root, ROOT.getCategoryName()).getFoundCategories();
		
		assertThat(result).isNotNull().containsExactly(root);
	}
	
	@Test
	public void testFindChild() {
		Category root = buildCategoryTree();	
		List<Category> result = Finders.findCategory(root, CHILD.getCategoryName()).getFoundCategories();
		
		assertThat(result).isNotNull().containsExactly(category(CHILD));
	}

	@Test
	public void testFindWrongName() {
		Category root = buildCategoryTree();		
		List<Category> result = Finders.findCategory(root, WRONG_NAME).getFoundCategories();
		
		assertThat(result).isNotNull().isEmpty();
	}
	
	@Test
	public void testGetPathRoot() {
		Category root = buildCategoryTree();
		
		assertThat(root).hasPath(root);
	}

	@Test
	public void testGetPathChild() {
		Map<Node, Category> categories = new HashMap<Node, Category>();
		buildCategoryTree(categories);

		for (Node node : categories.keySet()) {
			Category category = categories.get(node);
			Category[] expectedPath = node.buildExpectedPath(categories); 
			assertThat(category).as("Wrong path for category " + node.getCategoryName()).hasPath(expectedPath);
		}
	}
}
