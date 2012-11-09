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
package fr.duminy.safe.core;

import static fr.duminy.safe.core.TestUtils.category;
import static fr.duminy.safe.core.TestUtils.password;

import java.util.List;
import java.util.Map;

import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;

public class TestDataUtils {
	public static String WRONG_NAME = "wrong name";
	
	private static String ROOT_NAME = "root";		
	public static Node ROOT = node(ROOT_NAME, 2);
	
	private static String CHILD_NAME = "child";
	public static Node CHILD = node(CHILD_NAME, 2);
	
	private static String GRANDCHILD_NAME = "grandchild";
	public static Node GRANDCHILD = node(GRANDCHILD_NAME, 2);

	public static Model buildModel() {
		return buildModel(null, null);
	}
	
	public static Category buildCategoryTree() {
		return buildCategoryTree(null, null);
	}
	public static Category buildCategoryTree(List<Category> categories, Map<String, Category> categoriesMap) {
		return buildModel(categories, categoriesMap).getRootCategory();
	}

	private static Model buildModel(List<Category> categories, Map<String, Category> categoriesMap) {
		Model model = new Model();
		Category root = buildCategory(model, ROOT, null);
		Category child = buildCategory(model, CHILD, root);
		Category grandchild = buildCategory(model, GRANDCHILD, child);
		
		if (categories != null) {
			categories.add(root);
			categories.add(child);
			categories.add(grandchild);
		}
		
		if (categoriesMap != null) {
			categoriesMap.put(root.getName(), root);
			categoriesMap.put(child.getName(), child);
			categoriesMap.put(grandchild.getName(), grandchild);
		}

		return model;
	}
	
	private static Category buildCategory(Model model, Node node, Category parent) {
		Category category;
		if (parent == null) {
			category = model.getRootCategory();
		} else {
			category = category(node.getCategoryName());
			parent.add(category);
		}
		
		for (String name : node.getPasswordNames()) {
			Password password = password(name, name + "_pwd");			
			model.addPassword(category, password);
		}
		return category;
		
	}

	private static Node node(String categoryName, int nbPasswords) {
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
		public String[] getPasswordNames() {
			return passwordNames;
		}
		public String getCategoryName() {
			return categoryName;
		}
	}
}
