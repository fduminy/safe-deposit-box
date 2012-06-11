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
package fr.duminy.safe.core;

import java.io.File;
import java.util.List;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.checksum.Checksum;
import fr.duminy.safe.core.checksum.ChecksumException;
import fr.duminy.safe.core.checksum.DefaultChecksum;
import fr.duminy.safe.core.crypto.CryptoProvider;
import fr.duminy.safe.core.crypto.CryptoProviderException;
import fr.duminy.safe.core.crypto.DefaultCryptoProvider;
import fr.duminy.safe.core.model.DuplicateNameException;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.core.serialization.DefaultSerializer;
import fr.duminy.safe.core.serialization.Serializer;
import fr.duminy.safe.core.serialization.SerializerException;
import fr.duminy.safe.core.storage.DefaultStorage;
import fr.duminy.safe.core.storage.Storage;
import fr.duminy.safe.core.storage.StorageException;
import fr.duminy.safe.core.system.ClipboardListener;
import fr.duminy.safe.core.system.NullTimer;
import fr.duminy.safe.core.system.PasswordListener;
import fr.duminy.safe.core.system.System;
import fr.duminy.safe.core.system.Timer;
import fr.duminy.safe.core.system.UserActionListener;

abstract public class Core {
    private static final Logger LOG = LoggerFactory.getLogger(Core.class);
        
    private final MutablePicoContainer container;
    
    public Core() {
        container = new DefaultPicoContainer(new Caching());
        init(container);
    }
    
    protected void init(MutablePicoContainer container) {
        container.addComponent(new DefaultStorage<Model>(getPasswordFile()));
        
        container.addComponent(Model.class);
        container.addComponent(DefaultCryptoProvider.class);
        container.addComponent(DefaultChecksum.class);
        container.addComponent(DefaultSerializer.class);
    }
    
    protected File getPasswordFile() {
        File home = new File(java.lang.System.getProperty("user.home"));
    	return new File(home, "passwords.safe");
    }
    
    public final void start() throws CoreException {
        loadModel();
        
        final System system = container.getComponent(System.class);
        final Timer t = system.createTimer(1000);
        final Timer timer;
        if (t == null) {
        	LOG.warn("{} returned a null timer. Using NullTimer.", system);
        	timer = NullTimer.INSTANCE;
        } else {
        	timer = t;
        }
        timer.setAction(new Runnable() {
            @Override
            public void run() {
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

    public final void loadModel() throws CoreException {    	
        Model model = container.getComponent(Model.class);
        container.removeComponentByInstance(model);
        
    	try {            
    		Data<Model> d = load();
    		if (d != null) {
	    		d = decrypt(d);
	    		d = verifyCheckSum(d);
	    		model = deserialize(d);
    		} else {
    			model = new Model();
    		}
		} catch (Exception e) {
			throw new CoreException("can't load passwords", e);
		}
		
    	container.addComponent(model);
    }

    protected Data<Model> load() throws StorageException {
        @SuppressWarnings("unchecked")
		Storage<Model> storage = container.getComponent(Storage.class);
        return storage.load(container);
    }
    
    protected Data<Model> decrypt(Data<Model> input) throws CryptoProviderException {
        @SuppressWarnings("unchecked")
        CryptoProvider<Model> crypto = container.getComponent(CryptoProvider.class);
		return crypto.decrypt(input);
    }

    protected Data<Model> verifyCheckSum(Data<Model> input) throws ChecksumException {
        @SuppressWarnings("unchecked")
        Checksum<Model> checksum = container.getComponent(Checksum.class);
		return checksum.verifyCheckSum(input);
    }

    protected Model deserialize(Data<Model> input) throws SerializerException {
        @SuppressWarnings("unchecked")
        Serializer<Model> serializer = container.getComponent(Serializer.class);
		return serializer.deserialize(input);
    }
    
    public final void storeModel() throws CoreException {
    	Model model = container.getComponent(Model.class);
    	try {
			Data<Model> d = serialize(model);
			d = addCheckSum(d);
			d = encrypt(d);
			store(d);
		} catch (Exception e) {
			throw new CoreException("can't store passwords", e);
		}
    }

    protected Data<Model> serialize(Model input) throws SerializerException {
		@SuppressWarnings("unchecked")
		Serializer<Model> serializer = container.getComponent(Serializer.class);
		return serializer.serialize(container, input);
    }

    protected Data<Model> addCheckSum(Data<Model> input) throws ChecksumException {
		@SuppressWarnings("unchecked")
		Checksum<Model> checksum = container.getComponent(Checksum.class);
		return checksum.addCheckSum(input);
    }

    protected Data<Model> encrypt(Data<Model> input) throws CryptoProviderException {
		@SuppressWarnings("unchecked")
		CryptoProvider<Model> crypto = container.getComponent(CryptoProvider.class);
		return crypto.encrypt(input);
    }

    protected void store(Data<Model> input) throws StorageException {
		@SuppressWarnings("unchecked")
		Storage<Model> storage = container.getComponent(Storage.class);
    	storage.store(input);
    }
    
    protected final void reportError(String message, Exception e) {
        if (message == null) {
            message = (e == null) ? null : e.getMessage();
        }
        LOG.error(message, e);
        displayError(message, e);
    }
    
    abstract protected void displayError(String message, Exception e);
    
    public void stop() throws CoreException {
        storeModel();
    }

    public void addPassword(Password password) throws DuplicateNameException {
        Model model = container.getComponent(Model.class);
        model.addPassword(password);
    }

    public void removePassword(Password password) throws DuplicateNameException {
        Model model = container.getComponent(Model.class);
        model.removePassword(password);
    }
    
    public List<Password> getPasswords() {
        Model model = container.getComponent(Model.class);
        return model.getPasswords();
    }
    
    @Override
    protected void finalize() throws CoreException {
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
