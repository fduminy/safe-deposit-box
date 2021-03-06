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
import java.util.List;

public class Named implements Serializable {
	public static <T extends Named> int indexOf(List<T> list, T named) {
		int result = -1;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(named.getName())) {
				result = i;
				break;
			}
		}
		return result;
	}
	
    /**
     * 
     */
    private static final long serialVersionUID = 7313928749134494326L;
    
    private final String name;

    public Named(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
    }
    
    public final String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
    	if (!(other instanceof Named)) {
    		return false;
    	}
    	
    	return ((Named) other).name.equals(name);
    }
    
    @Override
    public String toString() {
        return "Named [name=" + name + "]";
    }
}