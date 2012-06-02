/**
 * Safe Deposit Box is a software managing your passwords in safe place.
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
package fr.duminy.safe.core.storage;

import static fr.duminy.safe.core.Utils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.picocontainer.PicoContainer;

import fr.duminy.safe.core.Data;

public class DefaultStorage<T> implements Storage<T> {
	private final File file;
    public DefaultStorage(File file) {
    	this.file = file;
    }

    @Override
    public void store(Data<T> data) throws StorageException {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        
        try {
            fos = new FileOutputStream(file);            
            bos = new BufferedOutputStream(fos);
            
            bos.write(data.getBytes());
        } catch (FileNotFoundException e) {
            throw new StorageException(e);
        } catch (IOException e) {
            throw new StorageException(e);
        } finally {
            closeQuietly(bos);
            closeQuietly(fos);
        }
    }
    
    @Override
    public Data<T> load(PicoContainer container) throws StorageException {
    	Data<T> result = null;
    	        
        if (file.exists() && (file.length() > 0)) {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            
	        try {
	            fis = new FileInputStream(file);
	            bis = new BufferedInputStream(fis);
	            
	            byte[] buffer = new byte[(int) file.length()];
	            bis.read(buffer);
	            result = new Data<T>(container, buffer);
	        } catch (FileNotFoundException e) {
	            throw new StorageException(e);
	        } catch (IOException e) {
	            throw new StorageException(e);
	        } finally {
	            closeQuietly(bis);
	            closeQuietly(fis);
	        }
        }
        
        return result;
    }
}
