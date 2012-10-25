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

import static fr.duminy.safe.core.TestUtils.array;
import static fr.duminy.safe.core.TestUtils.category;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CategoryFinderTest {
	private static String ROOT_NAME = "root";
	private static String CHILD_NAME = "child";
	private static String WRONG_NAME = "wrong name";
	
	@Test
	public void testFindRoot() {
		Category root = buildCategoryTree();		
		Category result = CategoryFinder.find(root, ROOT_NAME);
		
		assertThat(result).isNotNull().isEqualsToByComparingFields(root);
	}
	
	@Test
	public void testFindChild() {
		Category root = buildCategoryTree();	
		Category result = CategoryFinder.find(root, CHILD_NAME);
		
		assertThat(result).isNotNull().isEqualsToByComparingFields(category(CHILD_NAME));
	}

	@Test
	public void testFindWrongName() {
		Category root = buildCategoryTree();		
		Category result = CategoryFinder.find(root, WRONG_NAME);
		
		assertThat(result).isNull();
	}
	
	@Test
	public void testGetPathRoot() {
		Category root = buildCategoryTree();
		
		assertThat(root.getPath()).isNotNull().containsExactly(root);
	}

	@Test
	public void testGetPathChild() {
		List<Category> categories = new ArrayList<Category>();
		buildCategoryTree(categories);
		
		assertThat(categories.get(1).getPath()).isNotNull().containsExactly(array(categories));
	}

	private Category buildCategoryTree() {
		return buildCategoryTree(null);
	}
	private Category buildCategoryTree(List<Category> categories) {
		Category child = category(CHILD_NAME);
		Category root = category(ROOT_NAME).add(child);
		
		if (categories != null) {
			categories.add(root);
			categories.add(child);
		}
		
		return root;
	}
}
