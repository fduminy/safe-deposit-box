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

import static fr.duminy.safe.core.TestUtils.category;
import static fr.duminy.safe.core.TestUtils.password;

import java.util.List;
import java.util.Map;

abstract class AbstractFinderTest {
	protected static String WRONG_NAME = "wrong name";
		
	protected static Node ROOT = node("root", 2);
	protected static Node CHILD = node("child", 2);

	protected Category buildCategoryTree() {
		return buildCategoryTree(null, null);
	}
	protected Category buildCategoryTree(List<Category> categories, Map<String, Category> categoriesMap) {
		Category child = buildCategory(CHILD);
		Category root = buildCategory(ROOT);
		root.add(child);
		
		if (categories != null) {
			categories.add(root);
			categories.add(child);
		}
		
		if (categoriesMap != null) {
			categoriesMap.put(root.getName(), root);
			categoriesMap.put(child.getName(), child);
		}
		
		return root;
	}

	protected Category buildCategory(Node node) {
		Category category = category(node.getCategoryName());
		for (String name : node.getPasswordNames()) {
			category.add(password(name, name + "_pwd"));
		}
		return category;
		
	}

	public static Node node(String categoryName, int nbPasswords) {
		String[] passwordNames = new String[nbPasswords];
		for (int i = 0;  i < nbPasswords; i++) {
			passwordNames[i] = categoryName + "PasswordName" + i;
		}
		return node(categoryName, passwordNames);
	}
	public static Node node(String categoryName, String... passwordNames) {
		return new Node(categoryName, passwordNames);
	}
	
	public static class Node {
		private final String[] passwordNames;
		private final String categoryName;
		public Node(String categoryName, String[] passwordNames) {
			super();
			this.passwordNames = passwordNames;
			this.categoryName = categoryName;
		}
		String[] getPasswordNames() {
			return passwordNames;
		}
		String getCategoryName() {
			return categoryName;
		}
	}
}
