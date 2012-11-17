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
package fr.duminy.safe.swing;

import static fr.duminy.safe.swing.action.Action.ADD_CATEGORY;
import static fr.duminy.safe.swing.action.Action.REMOVE_CATEGORY;
import static fr.duminy.safe.swing.action.Action.RENAME_CATEGORY;
import static fr.duminy.safe.core.Utils.array;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.tree.DefaultXTreeCellEditor;

import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.CategoryVisitor;
import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.swing.command.Command;

@SuppressWarnings("serial")
public class CategoriesPanel extends SPanel<SwingCore> {
	private final JXTree categoriesTree;
	private final Map<Category, CategoryNode> categoryToNode = new HashMap<Category, CategoryNode>();
	
	public CategoriesPanel() throws Exception {
		this(new SwingCore());
	}
	/**
	 * Create the panel.
	 */
	public CategoriesPanel(final SwingCore core) {
		super(core);
		setLayout(new BorderLayout());
		
	    categoriesTree = new JXTree();
	    categoriesTree.setName("categoriesTree"); //$NON-NLS-1$
	    categoriesTree.setShowsRootHandles(false);
		categoriesTree.setExpandsSelectedPaths(true);
		categoriesTree.setEditable(true);
	    add(categoriesTree, BorderLayout.CENTER);
	    
		addPopupMenu();
		
		final DefaultXTreeCellEditor editor = (DefaultXTreeCellEditor) categoriesTree.getCellEditor();
		editor.addCellEditorListener(new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent event) {
				try {
					CategoryNode selectedNode = (CategoryNode) categoriesTree.getSelectionPath().getLastPathComponent(); 
					String name = (String) editor.getCellEditorValue();
					selectedNode.getCategory().rename(name);					
				} catch (Exception e) {
					core.displayError(e.getMessage(), e);
				}
				refresh();
			}
			
			@Override
			public void editingCanceled(ChangeEvent event) {
			}
		});
		
		addCommand(new Command(ADD_CATEGORY) {
			@Override
			public void run() {
				Category c = new Category("New Category");
				getSelectedCategory().add(c);
				refresh();
				
				startEditing(c);
			}
		});
		
		addCommand(new Command(RENAME_CATEGORY) {
			@Override
			public void run() {
				startEditing(getSelectedCategory());
			}
		});
		
		addCommand(new Command(REMOVE_CATEGORY) {
			@Override
			public void run() {
				getSelectedCategory().remove();
				refresh();
			}
		});
		
		refresh();
	}
	
	private void startEditing(Category category) {
		TreePath treePath = toTreePath(category);
		categoriesTree.setSelectionPath(treePath);
		categoriesTree.startEditingAtPath(treePath);
	}
	
	private void addPopupMenu() {
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new JMenuItem(ADD_CATEGORY.toSwingAction()));
		popupMenu.add(new JMenuItem(REMOVE_CATEGORY.toSwingAction()));
		popupMenu.add(new JMenuItem(RENAME_CATEGORY.toSwingAction()));
		
	    categoriesTree.addMouseListener(new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent e) {
				showPopupMenu(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				showPopupMenu(e);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				showPopupMenu(e);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				showPopupMenu(e);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				showPopupMenu(e);
			}
			
			private void showPopupMenu(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(categoriesTree, e.getX(), e.getY());
					
					TreePath path = categoriesTree.getPathForLocation(e.getX(), e.getY());
					categoriesTree.setSelectionPath(path);
					updateActions(path);
				}
			}
		});
	}
	
	private void updateActions(TreePath selectedPath) {
		boolean categorySelected = (selectedPath != null);
		ADD_CATEGORY.setEnabled(categorySelected);		
		
		CategoryNode rootNode = getRootNode();
		boolean nonRootCategorySelected = categorySelected && (selectedPath.getLastPathComponent() != rootNode); 
		REMOVE_CATEGORY.setEnabled(nonRootCategorySelected);
		RENAME_CATEGORY.setEnabled(nonRootCategorySelected);
	}
	
	public void addTreeSelectionListener(TreeSelectionListener listener) {
		categoriesTree.getSelectionModel().addTreeSelectionListener(listener);
	}
	
	public void refresh() {
		Category root = core.getRootCategory();
		final CategoryNode rootNode = new CategoryNode(root);
		List<Category> selectedCategoryPath = null;
		List<Category> expandedCategories = null;
		boolean initialization = categoryToNode.isEmpty(); 
		
		if (!initialization) {
			// save selection & nodes expansion
			selectedCategoryPath = getSelectedCategoryPath();
			
			if (selectedCategoryPath == null) {
				selectedCategoryPath = Collections.singletonList(root);
			}
			
			// save nodes expansion
			expandedCategories = new ArrayList<Category>();
			Enumeration<TreePath> expandedDesc = categoriesTree.getExpandedDescendants(toTreePath(root));
			if (expandedDesc != null) {
				while (expandedDesc.hasMoreElements()) {
					CategoryNode node = (CategoryNode) expandedDesc.nextElement().getLastPathComponent();
					expandedCategories.add(node.getCategory());
				}
			}
			
			if (expandedCategories.isEmpty()) {
				selectedCategoryPath = Collections.singletonList(root);
			}
		}
		
		// start refresh
		DefaultTreeModel model = (DefaultTreeModel) categoriesTree.getModel();
				
		model.setRoot(rootNode);
		
		categoryToNode.clear();
		categoryToNode.put(root, rootNode);
		
		core.getRootCategory().accept(new CategoryVisitor() {
			@Override
			public boolean visit(Password p) {
				return true;
			}
			
			@Override
			public boolean visit(Category category) {
				List<Category> path = category.getPath();
				if (path.size() > 1) {
					CategoryNode parentNode = categoryToNode.get(path.get(path.size() - 2));
					CategoryNode node = new CategoryNode(category);
					categoryToNode.put(category, node);
					parentNode.add(node);					
				}
				return true;
			}
		});

		if (!initialization) {
			// restore selection & nodes expansion
			categoriesTree.setSelectionPath(toTreePath(selectedCategoryPath));
			categoriesTree.collapseAll();
			for (Category expandedCategory : expandedCategories) {
				categoriesTree.expandPath(toTreePath(expandedCategory));
			}
		}
	}
	
	public void setSelectedCategory(Category category) {
		categoriesTree.setSelectionPath(toTreePath(category));
	}

	public Category getSelectedCategory() {
		List<Category> categoryPath = getSelectedCategoryPath();
		return (categoryPath == null) ? null : categoryPath.get(categoryPath.size() - 1);
	}
	
	public List<Category> getSelectedCategoryPath() {
		List<Category> categoryPath = null;
		
		TreePath path = categoriesTree.getSelectionPath();
		if ((path != null) && (path.getPathCount() > 0)) {
			categoryPath = new ArrayList<Category>();
			
			for (Object nd : path.getPath()) {
				categoryPath.add(((CategoryNode) nd).getCategory());
			}
		}
		
		return categoryPath;
	}
	
	private TreePath toTreePath(Category category) {
		if (category == null) {
			return null;
		}
		
		return toTreePath(category.getPath());
	}

	private TreePath toTreePath(List<Category> path) {
		if (path == null) {
			return null;
		}
		
		List<CategoryNode> categoryPath = new ArrayList<CategoryNode>(path.size());
		for (int i = path.size() - 1; i >= 0; i--) {
			Category c = path.get(i);
			CategoryNode node = categoryToNode.get(c);
			if (node != null) {
				categoryPath.add(node);
			}
		}
						
		Collections.reverse(categoryPath);
		TreePath result = null;
		if (!categoryPath.isEmpty()) {
			result = new TreePath(array(categoryPath, CategoryNode.class));
		}
		return result;
	}
	
	private CategoryNode getRootNode() {
		return (CategoryNode) categoriesTree.getModel().getRoot();		
	}
}
