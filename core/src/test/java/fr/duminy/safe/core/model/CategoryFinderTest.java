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
import static fr.duminy.safe.core.TestDataUtils.ROOT;
import static fr.duminy.safe.core.TestDataUtils.CHILD;
import static fr.duminy.safe.core.TestDataUtils.GRANDCHILD;
import static fr.duminy.safe.core.TestDataUtils.WRONG_NAME;
import static fr.duminy.safe.core.TestDataUtils.buildCategoryTree;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.duminy.safe.core.finder.Finders;

public class CategoryFinderTest {
	@Test
	public void testFindAll() {
		Category root = buildCategoryTree();		
		List<Category> result = Finders.findCategory(root, null).getFoundCategories();
		
		assertThat(result).isNotNull().containsExactly(root, child(), grandchild());
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
		
		assertThat(result).isNotNull().containsExactly(child());
	}

	private Category child() {
		return category(CHILD.getCategoryName());
	}

	private Category grandchild() {
		return category(GRANDCHILD.getCategoryName());
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
		
		assertThat(root.getPath()).isNotNull().containsExactly(root);
	}

	@Test
	public void testGetPathChild() {
		List<Category> categories = new ArrayList<Category>();
		buildCategoryTree(categories, null);
		
		assertThat(categories.get(0).getPath()).isNotNull().containsExactly(array(categories.subList(0, 1)));
		assertThat(categories.get(1).getPath()).isNotNull().containsExactly(array(categories.subList(0, 2)));
		assertThat(categories.get(2).getPath()).isNotNull().containsExactly(array(categories));
	}
}
