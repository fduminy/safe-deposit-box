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

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.model.Category;

public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
        
    private Utils() {        
    }
    
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        }
    }    
    
    public static boolean isBlank(String value) {
    	return (value == null) || value.trim().isEmpty();
    }
    
	public static Category[] array(Collection<Category> values) {    	
    	return array(values, Category.class);
    }

	public static <T> T[] array(Collection<T> values, Class<T> clazz) {    	
    	return values.toArray(newArray(values, clazz));
    }
	
	public static <T> T[] newArray(Collection<T> values, Class<T> clazz) {
    	return newArray(clazz, values.size());
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T> clazz, int size) {    	
    	return (T[]) Array.newInstance(clazz, size);
    }    
}
