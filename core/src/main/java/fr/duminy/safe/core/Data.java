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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

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
	private static final int INT_SIZE = 4;
	
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

	public Data<T> readBlock() throws WrongSizeException {
		return readBlock(0, false);
	}
	
	public Data<T> readBlockAfter(Data<T> block, int blockPosition) throws WrongSizeException {
		return readBlock(blockPosition + INT_SIZE + block.getSize(), true);
	}

	public int readSize(int position) {
		return toByteBuffer(position).getInt();
	}

	public byte[] readBlockAfterSize(int position) {
		ByteBuffer buffer = toByteBuffer(position);
		buffer.getInt();
		
		// read block
		byte[] block = new byte[buffer.remaining()];
		buffer.get(block);
		return block;
	}
	
	private ByteBuffer toByteBuffer(int position) {
		return (position == 0) ? ByteBuffer.wrap(data) : ByteBuffer.wrap(data, position, data.length - position);
	}
	private Data<T> readBlock(int position, boolean untilEnd) throws WrongSizeException {
		ByteBuffer buffer = toByteBuffer(position);
		int sizeToRead = buffer.remaining(); 
		if (!untilEnd) {
			// read size
			sizeToRead = buffer.getInt();
			if (buffer.remaining() < sizeToRead) {
				throw new WrongSizeException("size is too big (0x"
						+ Integer.toHexString(sizeToRead) + ")");
			}
			if (sizeToRead < 0) {
				throw new WrongSizeException("size < 0");
			}
		}
		
		// read block
		byte[] block = new byte[sizeToRead];
		buffer.get(block);
		return new Data<T>(container, block);
	}
	
	public int getSize() {
		return data.length;
	}
	
	public byte get(int position) {
		return data[position];
	}
	
	@Override
	public String toString() {
		return toString(data, 8);
	}

	public String toJavaString() {
		return toString(data, Integer.MAX_VALUE, true);
	}
	
	public String toString(int beginAndEndSize) {
		return toString(data, beginAndEndSize);
	}

	public static String toString(byte[] bytes, int beginAndEndSize) {
		return toString(bytes, beginAndEndSize, false);
	}
	
	private static String toString(byte[] bytes, int beginAndEndSize, boolean asJavaString) {
		StringBuilder buffer = new StringBuilder();
		boolean toShorten = (beginAndEndSize > 0) && (bytes.length >= beginAndEndSize) && (bytes.length > (2 * beginAndEndSize));
		if (!asJavaString && (beginAndEndSize > 0) && toShorten) {
			appendBytes(buffer, bytes, 0, beginAndEndSize, asJavaString);
			buffer.append(" ...");
			appendBytes(buffer, bytes, bytes.length - beginAndEndSize, beginAndEndSize, asJavaString);
			buffer.append(" (size=").append(bytes.length).append(")");
		} else {
			appendBytes(buffer, bytes, 0, bytes.length, asJavaString);
		}
		return buffer.toString();
	}
	
	public static <T> Data<T> appendBlocks(byte[] block1, Data<T> block2) {
        // write size & data of block1
        ByteBuffer result = writeBlock(block1.length, block1, block2.getSize());
        
        // write data of block2
        result.put(block2.getBytes());
        
        return new Data<T>(block2.getContainer(), result.array());		
	}

	private static void appendBytes(StringBuilder buffer, byte[] bytes,
			int pos, int length, boolean asJavaString) {
		if (asJavaString) {
			buffer.append("{ ");
		}
		
		for (int i = 0; i < length; i++) {
			byte b = bytes[pos + i];
			if (asJavaString) {
				if (i > 0) {
					buffer.append(", ");
				}
				
				buffer.append("(byte)0x");
			} else {
				buffer.append(' ');
			}
			String s = Integer.toHexString(((int) b) & 0x000000FF);
			if (s.length() < 2) {
				buffer.append('0');
			}
			buffer.append(s);
		}
		
		if (asJavaString) {
			buffer.append(" }");
		}
	}

	public static <T> Data<T> writeIntAndBlock(PicoContainer container, int value, byte[] block) {
		return new Data<T>(container, writeBlock(value, block, 0).array());
	}
	
	private static ByteBuffer writeBlock(int value, byte[] block, int additionalSize) {
        ByteBuffer result = ByteBuffer.allocate(INT_SIZE + block.length + additionalSize);
        
        // write size & data of block1
        result.putInt(value);
        result.put(block);
        
        return result;
	}
}
