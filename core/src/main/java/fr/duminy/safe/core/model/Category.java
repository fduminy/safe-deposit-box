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
	private static final long serialVersionUID = 7044805245877559179L;
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

    public boolean hasDescendant(Category category) {
    	if ((category == null) || (category == this)) {
    		return false;
    	}
    	
    	if (children.contains(category)) {
    		return true;
    	}
    	
    	for (Category child : children) {
    		if (child.hasDescendant(category)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public void remove() {
    	if (parent != null) {    		
    		// checks case of duplicate
    		for (Category category : this.children) {
    			checkParentHasNoCategory(category.getName());
    		}			
    		
    		// move passwords to the parent
    		Password[] passwordsToMove = new Password[this.passwords.size()];
    		this.passwords.toArray(passwordsToMove);
    		for (Password password : passwordsToMove) {
    			remove(password);
    			parent.add(password);
    		}
    		
    		// move categories to the parent
    		Category[] childrenToMove = new Category[this.children.size()];
    		this.children.toArray(childrenToMove);
    		for (Category category : childrenToMove) {
    			children.remove(category);
    			parent.add(category);
    		}
    		
    		parent.children.remove(this);
    		
    		// detach from the parent
    		parent = null;
    	}
    }

    Category add(Password password) {
        passwords.add(password);
        return this;
    }
    
	void remove(Password password) {
		passwords.remove(password);
	}

	private void checkParentHasNoCategory(String category) {
		if (parent.children.contains(category)) {
			parent.children.throwDuplicateNameException(category);
		}
	}
	
	public Category rename(String name) {
		// checks case of no rename
		if (getName().equals(name)) {
			return this;			
		}
		
		// checks case of duplicate
		if (parent != null) {
			checkParentHasNoCategory(name);
		}
		
		// ok, we can rename safely
		Category category = new Category(parent, name, children, passwords);
		for (Category child : children) {
			child.parent = category;
		}
		
		if (parent != null) {
			parent.children.remove(this);
			parent.add(category);
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
        CategoryPrinter v = new CategoryPrinter(false, false); 
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
        private final StringBuilder buffer = new StringBuilder(); 
        private final boolean recursive;
        private final boolean passwords;
        
        public CategoryPrinter(boolean recursive, boolean passwords) {
        	this.recursive = recursive;
        	this.passwords = passwords;
        }
        
        @Override
        public boolean visit(Category category) {
            List<Category> path = category.getPath();            
            buffer.append(System.getProperty("line.separator"));
            indent(path.size() - 1);
            
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) {
                    buffer.append('/');
                }
                buffer.append(path.get(i).getName());
            }
            
            if (passwords) {
            	buffer.append(" : ");
            }
            
            return recursive;
        }
        
        @Override
        public boolean visit(Password p) {
        	if (passwords) {
        		buffer.append(' ').append(p);
        	}
            return true;
        }
        
        @Override
        public String toString() {
            return buffer.toString();
        }
        
        private void indent(int level) {
        	for (int i = 0; i < level; i++) {
        		buffer.append("    ");
        	}
        }
    }
}
