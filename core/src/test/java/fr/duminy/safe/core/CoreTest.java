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
package fr.duminy.safe.core;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.checksum.ChecksumException;
import fr.duminy.safe.core.crypto.CryptoProviderException;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.core.serialization.SerializerException;
import fr.duminy.safe.core.storage.StorageException;
import fr.duminy.safe.core.system.ClipboardListener;
import fr.duminy.safe.core.system.PasswordListener;
import fr.duminy.safe.core.system.System;
import fr.duminy.safe.core.system.Timer;
import fr.duminy.safe.core.system.UserActionListener;

public class CoreTest {
	private File file;
	private FakeCore core;
	
    @Before
    public void setUp() throws Exception {
        file = File.createTempFile("test", ".test");
		core = new FakeCore();
    }

    @After
    public void tearDown() throws IOException {
    	if (file != null) {
    		file.delete();
    		file = null;
    	}
    }
	
	@Test
	public void testStart() throws CoreException {
		core.start();
	}

	@Test
	public void testLoadModel() throws CoreException {
		core.loadModel();
	}

	@Test
	public void testStoreModel() throws CoreException {
		core.storeModel();
	}

	@Test
	public void testReportError() {
		core.reportError("error", new Exception("error"));
	}

	@Test
	public void testDisplayError() {
		core.displayError("error", new Exception("error"));
		core.assertDisplayErrorCalled();
	}

	@Test
	public void testStop() throws CoreException {
		core.stop();
	}

	@Test
	public void testAddPassword() {
		core.addPassword(new Password("test"));
	}

	@Test
	public void testGetPasswords() {
		Password p = new Password("test");
		core.addPassword(p);
		
		List<Password> passwords = core.getPasswords();
		assertNotNull(passwords);
		assertEquals(1, passwords.size());
		assertSame(p, passwords.get(0));
	}

	public class FakeCore extends Core {
		private Logger LOG = LoggerFactory.getLogger(FakeCore.class);
		
		private boolean displayErrorCalled = false;
		
		public FakeCore() {
			super();
		}

	    @Override
	    protected void init(MutablePicoContainer container) {
	        super.init(container);
	        
	        container.addComponent(FakeSystem.class);
	        
	        container.addComponent(new Data<TestClass>(container, TestUtils.createTestData(8)));
	    }
		
		@Override
		protected void displayError(String message, Exception e) {
			displayErrorCalled = true;
		}	
		
		public void assertDisplayErrorCalled() {
			if (!displayErrorCalled) {
				fail("displayError not called");
			}
		}
		
		@Override
		protected File getPasswordFile() {
			return file;
		}
		
	    @Override
	    protected Data<Model> load() throws StorageException {
	    	Data<Model> result = super.load();
	    	LOG.debug("load: \noutput={}", result);
	        return result;
	    }
	    
	    @Override
	    protected Data<Model> decrypt(Data<Model> input) throws CryptoProviderException {
	    	Data<Model> result = super.decrypt(input);
	    	LOG.debug("decrypt: \ninput ={}\noutput={}", input, result);
	        return result;
	    }

	    @Override
	    protected Data<Model> verifyCheckSum(Data<Model> input) throws ChecksumException {
	    	Data<Model> result = super.verifyCheckSum(input);
	    	LOG.debug("verifyCheckSum: \ninput ={}\noutput={}", input, result);
	        return result;
	    }

	    @Override
	    protected Model deserialize(Data<Model> input) throws SerializerException {
	    	Model result = super.deserialize(input);
	    	LOG.debug("deserialize: \ninput ={}\noutput={}", input, result);
	        return result;
	    }		
	    
	    protected Data<Model> serialize(Model input) throws SerializerException {
	    	Data<Model> result = super.serialize(input);
	    	LOG.debug("serialize: \ninput ={}\noutput={}", input, result);
	        return result;
	    }

	    protected Data<Model> addCheckSum(Data<Model> input) throws ChecksumException {
	    	Data<Model> result = super.addCheckSum(input);
	    	LOG.debug("addCheckSum: \ninput ={}\noutput={}", input.toString(), result.toString());
	        return result;
	    }

	    protected Data<Model> encrypt(Data<Model> input) throws CryptoProviderException {
	    	Data<Model> result = super.encrypt(input);
	    	LOG.debug("encrypt: \ninput ={}\noutput={}", input, result);
	        return result;
	    }

	    protected void store(Data<Model> input) throws StorageException {
	    	super.store(input);
	    	LOG.debug("store: \ninput ={}", input);
	    }
	}
	
	public static class FakeSystem implements System {

		@Override
		public Timer createTimer(int delay) {
			return new FakeTimer();
		}

		@Override
		public void lockScreen() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unlockScreen() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setClipboardContent(String content) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getClipboardContent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addClipboardListener(ClipboardListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeClipboardListener(ClipboardListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addUserActionListener(UserActionListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeUserActionListener(UserActionListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isScreenLocked() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void askPassword(PasswordListener passwordListener) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class FakeTimer implements Timer {

		@Override
		public void start() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setAction(Runnable runnable) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
