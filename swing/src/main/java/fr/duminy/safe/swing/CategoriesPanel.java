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

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;

import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.CategoryVisitor;
import fr.duminy.safe.core.model.Password;

@SuppressWarnings("serial")
public class CategoriesPanel extends SPanel {
	private final SwingCore core;
	private final JXTree categoriesTree;
	
	public CategoriesPanel() throws Exception {
		this(new SwingCore());
	}
	/**
	 * Create the panel.
	 */
	public CategoriesPanel(final SwingCore core) {
		this.core = core;
		setLayout(new BorderLayout());
		
	    categoriesTree = new JXTree();
	    categoriesTree.setName("categoriesTree"); //$NON-NLS-1$
	    categoriesTree.setShowsRootHandles(false);
	    add(categoriesTree, BorderLayout.CENTER);
	    
		refresh();
	}
	
	public void addTreeSelectionListener(TreeSelectionListener listener) {
		categoriesTree.getSelectionModel().addTreeSelectionListener(listener);
	}
	
	public void refresh() {
		DefaultTreeModel model = (DefaultTreeModel) categoriesTree.getModel();
				
		final CategoryNode rootNode = new CategoryNode(core.getRootCategory());
		model.setRoot(rootNode);
		
		final Map<Category, CategoryNode> categoryToNode = new HashMap<Category, CategoryNode>();
		categoryToNode.put(core.getRootCategory(), rootNode);
		
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
	}
	
	public Category getSelectedCategory() {
		Category category = null;		
		TreePath path = categoriesTree.getSelectionPath();
		if (path != null) {
			CategoryNode node = (CategoryNode) path.getLastPathComponent();
			category = node.getCategory();
		}
		return category;
	}
}
