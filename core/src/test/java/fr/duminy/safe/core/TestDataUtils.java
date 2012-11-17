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
import static fr.duminy.safe.core.Utils.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.duminy.safe.core.TestUtils.CategoryFactory;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;

public class TestDataUtils {
	public static final String WRONG_NAME = "wrong name";
	
	public static final Node ROOT = node(null, "root", 2);
	public static final Node CHILD = node(ROOT, "child", 2);
	public static final Node CHILD2 = node(ROOT, "child2", 2);
	public static final Node GRANDCHILD = node(CHILD, "grandchild", 2);
	public static final Node GRANDCHILD2 = node(CHILD, "grandchild2", 2);
	private static final Node[] ALL_NODES = {ROOT, CHILD, CHILD2, GRANDCHILD, GRANDCHILD2};
	private static final List<Node> NONE = Collections.emptyList();

	private static Map<Node, List<Node>> DESCENDANTS = new HashMap<Node, List<Node>>();
	static {
		DESCENDANTS.put(ROOT, allNodesExcept(ROOT));
		DESCENDANTS.put(CHILD, Collections.unmodifiableList(Arrays.asList(GRANDCHILD, GRANDCHILD2)));
		DESCENDANTS.put(CHILD2, NONE);
		DESCENDANTS.put(GRANDCHILD, NONE);
		DESCENDANTS.put(GRANDCHILD2, NONE);
	}

	public static List<String> toCategoryNames(List<Category> categories) {
		List<String> names = new ArrayList<String>(categories.size());
		for (Category c : categories) {
			names.add(c.getName());
		}
		return names;
	}
	
	public static List<Node> allNodesExcept(Node... exceptions) {
		List<Node> result = new ArrayList<Node>(Arrays.asList(ALL_NODES));
		result.removeAll(Arrays.asList(exceptions));
		return result;
	}
	
	public static String[] allPasswordNames() {
		List<String> names = new ArrayList<String>();
		for (Node node : ALL_NODES) {
			for (String name : node.getPasswordNames()) {
				names.add(name);
			}
		}
		return array(names, String.class);
	}
	
	public static Model buildModel() {
		return buildModel(null, null);
	}
	
	public static Category buildCategoryTree() {
		return buildCategoryTree(null, null);
	}
	public static Category buildCategoryTree(Map<Node, Category> categories) {
		return buildCategoryTree(categories, null);
	}
	public static Category buildCategoryTree(Map<Node, Category> categories, CategoryFactory categoryFactory) {
		return buildModel(categories, categoryFactory).getRootCategory();
	}

	private static Model buildModel(Map<Node, Category> categories, CategoryFactory categoryFactory) {
		Model model = new Model();
		
		categoryFactory = (categoryFactory == null) ? new CategoryFactory() : categoryFactory;
		Category root = buildCategory(null, ROOT, model, categories, categoryFactory);
		Category child = buildCategory(root, CHILD, model, categories, categoryFactory);
		buildCategory(root, CHILD2, model, categories, categoryFactory);
		buildCategory(child, GRANDCHILD, model, categories, categoryFactory);
		buildCategory(child, GRANDCHILD2, model, categories, categoryFactory);
		
		return model;
	}
	
	private static Category buildCategory(Category parent, Node node, Model model, Map<Node, Category> categories,
			CategoryFactory categoryFactory) {
		Category category;
		if (parent == null) {
			category = model.getRootCategory();
		} else {
			category = categoryFactory.category(node.getCategoryName());
			parent.add(category);
		}
		
		for (String name : node.getPasswordNames()) {
			Password password = password(name);			
			model.addPassword(category, password);
		}

		if (categories != null) {
			categories.put(node, category);
		}
		
		return category;		
	}

	public static PasswordWithPath[] passwordWithPaths(String[] passwordNames, Node... pathElements) {
		PasswordWithPath[] result = new PasswordWithPath[passwordNames.length];
		int i = 0;
		for (String passwordName : passwordNames) {
			result[i] = passwordWithPath(password(passwordName), path(pathElements));
			i++;
		}
		return result;
	}

	public static PasswordWithPath passwordWithPath(Password password, Category[] path) {
		return new PasswordWithPath(Arrays.asList(path), password);
	}

	public static Category[] path(Node[] pathElements) {
		Category[] result = new Category[pathElements.length];		
		int i = 0;
		for (Node node : pathElements) {
			result[i] = category(node.getCategoryName());
			i++;			
		}
		return result;
	}
	
	public static String passwordName(String categoryName, int childNum) {
		return categoryName + "PasswordName" + childNum;
	}
	
	private static Node node(Node parent, String categoryName, int nbPasswords) {
		String[] passwordNames = new String[nbPasswords];
		for (int i = 0;  i < nbPasswords; i++) {
			passwordNames[i] = passwordName(categoryName, i);
		}
		
		Node[] path;
		if (parent == null) {
			path = new Node[1];
		} else {
			path = new Node[parent.getPath().length + 1];
			System.arraycopy(parent.getPath(), 0, path, 0, path.length - 1);
		}
		
		Node node = node(parent, categoryName, path, passwordNames);
		path[path.length - 1] = node;
		return node;
	}
	
	public static Node node(String categoryName, String... passwordNames) {
		return node(null, categoryName, new Node[0], passwordNames);
	}

	private static Node node(Node parent, String categoryName, Node[] pathElements, String[] passwordNames) {
		return new Node(parent, categoryName, passwordNames, pathElements);
	}
	
	public static class Node {
		private final List<Node> children = new ArrayList<Node>();
		private final String[] passwordNames;
		private final String categoryName;
		private final Node[] pathElements;
		private final Node parent;
		public Node(Node parent, String categoryName, String[] passwordNames, Node... pathElements) {
			super();
			this.parent = parent;
			this.passwordNames = passwordNames;
			this.categoryName = categoryName;
			this.pathElements = pathElements;
			
			if (parent != null) {
				parent.children.add(this);
			}
		}
		public String[] getPasswordNames() {
			return passwordNames;
		}
		public String[] getPasswordNamesRecursively() {
			List<String> passwords = new ArrayList<String>(Arrays.asList(passwordNames));
			for (Node child : children) {
				passwords.addAll(Arrays.asList(child.getPasswordNames()));
			}
			return array(passwords, String.class);
		}
		public String getCategoryName() {
			return categoryName;
		}

		private Node[] getPath() {
			return pathElements;
		}
		
		public Category[] buildExpectedPath(Map<Node, Category> categories) {
			Category[] result = new Category[pathElements.length];
			int i = 0;
			for (Node node : pathElements) {
				result[i] = categories.get(node);
				i++;
			}
			return result;
		}
		public Node getParent() {
			return parent;
		}
		
		public List<Node> getDescendants() {
			return DESCENDANTS.get(this);			
		}
	}
}
