/**
 * Safe Deposit Box is a software managing your passwords in a safe place.
 *
 * Copyright (C) 2012 Fabien DUMINY (fduminy at jnode dot org)
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
import java.util.List;

public class Category extends Named implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 360495040202283307L;
    
    private NamedList<Category> children = new NamedList<Category>();
    private NamedList<Password> passwords = new NamedList<Password>();
    private Category parent;
    
    public Category() {
        this(null);
    }
    
    public Category(String name) {
        super(name);
    }

    public void add(Category category) {
        category.parent = this;
        children.add(category);
    }
    
    public void add(Password password) {
        passwords.add(password);
    }
    
    public void accept(CategoryVisitor visitor) {
        visitor.visit(this);
        for (Password p : passwords) {
            visitor.visit(p);
        }
        for (Category c : children) {
            visitor.visit(c);
        }
    }

    @Override
    public String toString() {
        CategoryPrinter v = new CategoryPrinter(); 
        accept(v);
        return v.toString();
    }
    
    private static class CategoryPrinter implements CategoryVisitor {
        private StringBuilder buffer = new StringBuilder(); 
        
        @Override
        public void visit(Category category) {
            List<Category> path = new ArrayList<Category>();            
            Category c = category;
            while (c != null) {
                path.add(c);
                c = c.parent;
            }
            
            for (int i = path.size() - 1; i >= 0; i--) {
                buffer.append(path.get(i));
                if (i > 0) {
                    buffer.append('.');
                }
            }
        }
        
        @Override
        public void visit(Password p) {
            buffer.append(p);
        }
        
        @Override
        public String toString() {
            return buffer.toString();
        }
    }
    
}
