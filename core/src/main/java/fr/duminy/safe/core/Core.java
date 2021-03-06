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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
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
import fr.duminy.safe.core.crypto.Key;
import fr.duminy.safe.core.imp.CsvImporter;
import fr.duminy.safe.core.imp.Importer;
import fr.duminy.safe.core.model.Category;
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
        container.addComponent(CsvImporter.class);
        container.addComponent(getKey(container));
    }
    
    protected <T> void addComponent(Object component) {
    	container.addComponent(component);
    }
    
    protected <T> T getComponent(Class<T> componentClass) {
    	return container.getComponent(componentClass);
    }
    	    
    protected Key getKey(MutablePicoContainer container) {
    	Key result = container.getComponent(Key.class);
    	if (result == null) {
	    	byte[] key = new byte[]{1, 2, 3, 4, 5, 6, 7, 8}; //TODO get password from user
	    	result = new Data<Object>(container, key);
    	}
    	return result;
    }
    
    protected File getPasswordFile() {
        File home = new File(java.lang.System.getProperty("user.home"));
    	return new File(home, "passwords.safe");
    }
    
    public final void start() throws CoreException {
    	container.start();
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
        if (container.getLifecycleState().isStarted()) {
        	container.stop();
        }
        container.removeComponentByInstance(model);
        if (container.getLifecycleState().isStopped()) {
        	container.start();
        }
        
    	try {            
    		Data<Model> d = load();
    		if (d != null) {
	    		d = decrypt(d);
	    		d = verifyCheckSum(d);
	    		model = deserialize(d);
    		} else {
    			model = new Model();
    		}
    		LOG.debug("{} passwords loaded from file", model.getPasswords().size());
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
    
    public final void reportError(String message, Exception e) {
        if (message == null) {
        	Throwable cause = e;
        	while ((cause != null) && (message == null)) {
        		message = cause.getMessage();
        		cause = cause.getCause();
        	}
        }
        
        if ((message == null) || message.trim().isEmpty()) {
        	message = "An unexpected error happened";
        }
        
        LOG.error(message, e);
        displayError(message, e);
    }
    
    abstract protected void displayError(String message, Exception e);
    
    public void stop() throws CoreException {
        storeModel();
        if (container.getLifecycleState().isStarted()) {
        	container.stop();
        }
    }
     
    public void addPassword(Password password) throws DuplicateNameException {
    	addPassword(null, password);
    }
    public void addPassword(Category category, Password password) throws DuplicateNameException {
        Model model = container.getComponent(Model.class);
        model.addPassword(category, password);
    }

    public void removePassword(Password password) throws DuplicateNameException {
        Model model = container.getComponent(Model.class);
        model.removePassword(password);
    }
    
    public List<Password> getPasswords() {
        return getModel().getPasswords();
    }
    
    public Category getRootCategory() {
        return getModel().getRootCategory();
    }

    public Model getModel() {
        return container.getComponent(Model.class);
    }
    
    @Override
    protected void finalize() throws CoreException {
        stop();
    }    

    public final Collection<Importer> getImporters() {
    	return container.getComponents(Importer.class);
    }
    
    public final Model importPasswords(Importer importer, Reader reader) throws CoreException {
    	LOG.debug("before import: core contains {} passwords. password file : {}. runtime={}", 
    			new Object[]{getPasswords().size(), getPasswordFile().exists() ?  "exists" : "doesn't exist",
    					Runtime.getRuntime()});
    	
    	Model model;
		try {
			model = importer.read(reader);
		} catch (IOException e) {
			throw new CoreException("error while importing passwords", e);
		}
    	LOG.info("read {} passwords from importer", model.getPasswords().size());

    	getModel().add(model);
    	return model;
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
