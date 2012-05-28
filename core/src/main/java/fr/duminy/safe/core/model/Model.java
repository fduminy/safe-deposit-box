/**
 * Safe is a software managing your passwords in safe place.
 *
 * Copyright (C) 2012 Fabien DUMINY (fduminy at jnode dot org)
 *
 * Safe is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Safe is distributed in the hope that it will be useful,
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
import java.util.List;

public class Model  implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -2006864951731867534L;
    
    private Category rootCategory = new Category("root");
    
    private NamedList<Password> passwords = new NamedList<Password>();
    
    public void addPassword(Password password) throws DuplicateNameException {
        addPassword(rootCategory, password);
    }

    public void addPassword(Category category, Password password) throws DuplicateNameException {
        passwords.add(password);
        category.add(password);
    }
    
    public void removePassword(Password password) {
        passwords.remove(password);
    }

    public List<Password> getPasswords() {
        return passwords;
    }

    public Category getRootCategory() {
        return rootCategory;
    }
}
