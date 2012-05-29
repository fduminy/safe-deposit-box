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

import java.io.File;
import java.util.List;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.checksum.ChecksumException;
import fr.duminy.safe.core.checksum.DefaultChecksum;
import fr.duminy.safe.core.crypto.CryptoProviderException;
import fr.duminy.safe.core.crypto.DefaultCryptoProvider;
import fr.duminy.safe.core.model.DuplicateNameException;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.core.serialization.Serializer;
import fr.duminy.safe.core.serialization.SerializerException;
import fr.duminy.safe.core.storage.DefaultStorage;
import fr.duminy.safe.core.storage.Storage;
import fr.duminy.safe.core.storage.StorageException;
import fr.duminy.safe.core.system.ClipboardListener;
import fr.duminy.safe.core.system.PasswordListener;
import fr.duminy.safe.core.system.System;
import fr.duminy.safe.core.system.Timer;
import fr.duminy.safe.core.system.UserActionListener;

abstract public class Core {
    private static final Logger LOG = LoggerFactory.getLogger(Core.class);
        
    private static final boolean DEBUG = true;
    
    private final MutablePicoContainer container;
    
    public Core() throws Exception {
        container = new DefaultPicoContainer(new Caching());
        init(container);
    }
    
    protected void init(MutablePicoContainer container) throws Exception {
        container.addComponent(new DefaultStorage<Model>(getPasswordFile()));
        
        container.addComponent(Model.class);
        container.addComponent(DefaultCryptoProvider.class);
        container.addComponent(DefaultChecksum.class);
    }
    
    protected File getPasswordFile() {
        File home = new File(java.lang.System.getProperty("user.home"));
    	return new File(home, "passwords.safe");
    }
    
    public final void start() {
        if (DEBUG) {
            for (int i = 0; i < 10; i++) {
                Password p = new Password("" + i);
                p.setPassword("password" + i);
                addPassword(p);
            }
            
            for (Password p : getPasswords()) {
                LOG.debug("passwords={}", p);
            }
            storeModel();
        }
        
        loadModel();
        
        final System system = container.getComponent(System.class);
        final Timer timer = system.createTimer(1000);
        timer.setAction(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    return;
                }
                
                system.lockScreen();
            }
        });
        
        UserListener l = new UserListener(system, timer);
        l.unlockScreen();
        
        system.addClipboardListener(new ClipboardListener() {
            @Override
            public void clipboardChanged(System system) {
                
            }
        });
    }

    public void loadModel() {    	
        Model model = container.getComponent(Model.class);
        container.removeComponentByInstance(model);
        
        @SuppressWarnings("unchecked")
		Storage<Model> storage = container.getComponent(Storage.class);
        if (storage != null) {
			try {
				Data<Model> d = storage.load(container);
				if (d.getBytes().length == 0) {
					// case of an empty / non existing file
					model = new Model();
				} else {
					model = d.decrypt().verifyCheckSum().deserialize();
				}
            	container.addComponent(model);
			} catch (SerializerException e) {
                reportError("Can't serialize passwords", e);
			} catch (StorageException e) {
                reportError("Can't store passwords", e);
			} catch (CryptoProviderException e) {
                reportError("Can't encrypt passwords", e);
			} catch (ChecksumException e) {
                reportError("Can't add checksum to passwords", e);
			}
        } else {
            reportError("No serializer", null);
        }
    }
    
    public void storeModel() {
        Model model = container.getComponent(Model.class);
        if (model != null) {
        	
        	@SuppressWarnings("unchecked")
    		Serializer<Model> serializer = container.getComponent(Serializer.class);
            if (serializer != null) {
				try {
					Data<Model> d = serializer.serialize(container, model);
	            	d.addCheckSum().encrypt().save();
				} catch (SerializerException e) {
	                reportError("Can't serialize passwords", e);
				} catch (StorageException e) {
	                reportError("Can't store passwords", e);
				} catch (CryptoProviderException e) {
	                reportError("Can't encrypt passwords", e);
				} catch (ChecksumException e) {
	                reportError("Can't add checksum to passwords", e);
				}
            } else {
                reportError("No serializer", null);
            }
        }
    }

    protected final void reportError(String message, Exception e) {
        if (message == null) {
            message = (e == null) ? null : e.getMessage();
        }
        LOG.error(message, e);
        displayError(message, e);
    }
    
    abstract protected void displayError(String message, Exception e);
    
    public void stop() throws StorageException {
        storeModel();
    }

    public void addPassword(Password password) throws DuplicateNameException {
        Model model = container.getComponent(Model.class);
        model.addPassword(password);
    }
    
    public List<Password> getPasswords() {
        Model model = container.getComponent(Model.class);
        return model.getPasswords();
    }
    
    @Override
    protected void finalize() throws StorageException {
        stop();
    }    
    
    @Override
    public String toString() {
        return "Core [container=" + container + "]";
    }

    private class UserListener implements UserActionListener, PasswordListener {
        private final System system;
        private final Timer timer;
        
        public UserListener(System system, Timer timer) {
            this.system = system;
            this.timer = timer;
        }
        
        @Override
        public void actionPerformed() {
            timer.stop();
            if (system.isScreenLocked()) {
                system.removeUserActionListener(this);
                system.askPassword(this);
            } else {
                // restart the timer
                timer.start();
            }
        }

        public void unlockScreen() {
            timer.start();
            system.addUserActionListener(this);
            system.unlockScreen();
        }
        
        @Override
        public void passwordEntered(String password) {
            boolean passwordValid = true; //TODO
            if (passwordValid) {
                unlockScreen();
            } else {
                passwordCancelled();
            }
        }

        @Override
        public void passwordCancelled() {
            //TODO
        }
    }
}
