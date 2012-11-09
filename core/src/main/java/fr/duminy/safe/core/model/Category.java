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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Category extends Named implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 360495040202283307L;
    
    private NamedList<Category> children;
    private NamedList<Password> passwords;
    private Category parent;
    
    public Category() {
        this(null);
    }

    public Category(String name) {
    	this(null, name, null, null);
    }
    
    private Category(Category parent, String name, NamedList<Category> children, NamedList<Password> passwords) {
        super(name);
        this.parent = parent;
        this.children = (children == null) ? new NamedList<Category>("child") : children;
        this.passwords = (passwords == null) ? new NamedList<Password>("password") : passwords;
    }

    /**
     * TODO make this non-public because, like {@link #add(Password)}, it doesn't force to modify the list of passwords in {@link Model}. 
     * @param category
     * @return
     */
    public Category add(Category category) {
        category.parent = this;
        children.add(category);
        return this;
    }
    
    Category add(Password password) {
        passwords.add(password);
        return this;
    }
    
	void remove(Password password) {
		passwords.remove(password);
	}

	public Category rename(String name) {
		Category category = new Category(this.parent, name, this.children, this.passwords);
		
		if (parent != null) {
			parent.children.remove(this);
			parent.children.add(category);
		}
		
		this.parent = null;
		this.children = null;
		this.passwords = null;
		
		return category;
	}
    
    public boolean accept(CategoryVisitor visitor) {
    	return acceptImpl(visitor, true);
    }

    /**
     * 
     * @param visitor
     * @param topCategory The ancestor of all being visited categories. It's not necessarily the root category but it's the first category being visited. 
     * @return
     */
    private boolean acceptImpl(CategoryVisitor visitor, boolean topCategory) {
        boolean go = visitor.visit(this);
        if (!topCategory && !go) {
        	return false;
        }
        
        if (passwords != null) {
	        for (Password p : passwords) {
	            if (!topCategory && !go) {
	            	return false;
	            }
	            go = visitor.visit(p);
	        }
        }
        
        if (children != null) {
	        for (Category c : children) {
	            if (!topCategory && !go) {
	            	return false;
	            }
	        	go = c.acceptImpl(visitor, false);
	        }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        CategoryPrinter v = new CategoryPrinter(); 
        accept(v);
        return v.toString();
    }
    
    public List<Category> getPath() {
        List<Category> path = new ArrayList<Category>();            
        Category c = this;
        while (c != null) {
            path.add(c);
            c = c.parent;
        }
        Collections.reverse(path);
        return path;
    }
    
    private static class CategoryPrinter implements CategoryVisitor {
        private StringBuilder buffer = new StringBuilder(); 
        
        @Override
        public boolean visit(Category category) {
            List<Category> path = category.getPath();            
            
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) {
                    buffer.append('.');
                }
                buffer.append(path.get(i).getName());
            }
            return true;
        }
        
        @Override
        public boolean visit(Password p) {
            buffer.append(p);
            return true;
        }
        
        @Override
        public String toString() {
            return buffer.toString();
        }
    }
}
