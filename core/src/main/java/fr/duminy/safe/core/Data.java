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
package fr.duminy.safe.core;

import java.io.ByteArrayOutputStream;

import org.picocontainer.PicoContainer;

import fr.duminy.safe.core.checksum.Checksum;
import fr.duminy.safe.core.checksum.ChecksumException;
import fr.duminy.safe.core.crypto.CryptoProvider;
import fr.duminy.safe.core.crypto.CryptoProviderException;
import fr.duminy.safe.core.serialization.Serializer;
import fr.duminy.safe.core.serialization.SerializerException;
import fr.duminy.safe.core.storage.Storage;
import fr.duminy.safe.core.storage.StorageException;

public class Data<T> {
    private final byte[] data;
    private final PicoContainer container;
    
    public Data() {
        this(null, new byte[0]);
    }
    public Data(PicoContainer container, byte[] data) {
        this.data = data;
        this.container = container;
    }
    
    public Data(PicoContainer container, ByteArrayOutputStream baos) {
        this(container, baos.toByteArray());
    }
    
    public Data<T> load() throws StorageException {
        return getStorage().load(container);
    }
    public Data<T> save() throws StorageException {
        getStorage().store(this);
        return this;
    }
    
    public Data<T> serialize(T object) throws SerializerException {
        return getSerializer().serialize(container, object);
    }
    public T deserialize() throws SerializerException {
        return getSerializer().deserialize(this);
    }
    
    public Data<T> encrypt() throws CryptoProviderException {
        return getCrypto().encrypt(this);
    }
    public Data<T> decrypt() throws CryptoProviderException {
        return getCrypto().encrypt(this);
    }

    public Data<T> verifyCheckSum() throws ChecksumException {
        return getChecksum().verifyCheckSum(this);
    }
    public Data<T> addCheckSum() throws ChecksumException {
        return getChecksum().addCheckSum(this);
    }
    public byte[] getBytes() {
        return data;
    }
    
    @SuppressWarnings("unchecked")
    private Storage<T> getStorage() {
        return container.getComponent(Storage.class);
    }
    
    @SuppressWarnings("unchecked")
    private Serializer<T> getSerializer() {
        return container.getComponent(Serializer.class);
    }
    
    @SuppressWarnings("unchecked")
    private Checksum<T> getChecksum() {
        return container.getComponent(Checksum.class);
    }
    
    @SuppressWarnings("unchecked")
    private CryptoProvider<T> getCrypto() {
        return container.getComponent(CryptoProvider.class);
    }
    public PicoContainer getContainer() {
        return container;
    }
}
