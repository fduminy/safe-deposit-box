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
package fr.duminy.safe.core.storage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;

import fr.duminy.safe.core.Data;
import fr.duminy.safe.core.TestClass;
import fr.duminy.safe.core.TestUtils;

public class StorageTest {
	private static final byte[] DATA = TestUtils.createTestData(16);
	
    private File file;
    private Storage<TestClass> storage;
    
    @Before
    public void setUp() throws IOException {
        file = File.createTempFile("test", ".test");
        storage = new DefaultStorage<TestClass>(file);
    }

    @After
    public void tearDown() throws IOException {
    	if (file != null) {
    		file.delete();
    		file = null;
    	}
        storage = null;
    }
    
    @Test
    public void testStore() throws StorageException {
    	store();
    }

    private void store() throws StorageException {
        file.delete();
        assertFalse("file must not exist before test", file.exists());
        
        storage.store(new Data<TestClass>(new DefaultPicoContainer(), DATA));
        
        assertTrue("file must exist", file.exists());
        assertTrue("file must not be empty", file.length() > 0);
    }
    
	@Test
	public void testStoreAndLoad() throws StorageException {
		store();
		byte[] result = storage.load(new DefaultPicoContainer()).getBytes();
		assertArrayEquals("input data must be unchanged after store followed by load", DATA, result);
	}    
}
