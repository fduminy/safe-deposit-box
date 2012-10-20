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
package fr.duminy.safe.core.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.KeyParameter;

import fr.duminy.safe.core.Data;

public class DefaultCryptoProvider<T> implements CryptoProvider<T> {
	private final Key key;
    public DefaultCryptoProvider(Key key) {
    	this.key = key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Data<T> encrypt(Data<T> input) throws CryptoProviderException {
        return processBytes(true, input);
    }

    /**
     * {@inheritDoc}
     * @throws CryptoProviderException 
     */
    @Override
    public Data<T> decrypt(Data<T> input) throws CryptoProviderException {
        return processBytes(false, input);
    }
    
	private final Data<T> processBytes(boolean encrypt, Data<T> input)
			throws CryptoProviderException {
		final byte[] inputBytes;
		final int size;
		if (encrypt) {
			inputBytes = input.getBytes();
			size = inputBytes.length;
		} else {
			size = input.readSize(0);
			if (size == 0) {
				return new Data<T>(input.getContainer(), new byte[0]);
			}

			inputBytes = input.readBlockAfterSize(0);
		}

		byte[] output = processBytes(encrypt, inputBytes, size);

		Data<T> result;		
		if (encrypt) {
			result = Data.writeIntAndBlock(input.getContainer(), size,
					output);
		} else {
			result = new Data<T>(input.getContainer(), output);
		}

		return result;
	}
    
	private final byte[] processBytes(boolean encrypt, byte[] input, int size)
			throws CryptoProviderException {
		BlockCipherPadding blockCipherPadding = new ZeroBytePadding();

		BlockCipher engine = new DESEngine();
		BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(engine), blockCipherPadding);

		cipher.init(encrypt, new KeyParameter(key.getBytes()));

		byte[] output = new byte[encrypt ? cipher.getOutputSize(size) : size];

		int outputLen = cipher.processBytes(input, 0, input.length, output, 0);
		try {
			cipher.doFinal(output, outputLen);
		} catch (CryptoException ce) {
			throw new CryptoProviderException("Error while "
					+ (encrypt ? "encrypting" : "decrypting"), ce);
		}

		return output;
	}
}
