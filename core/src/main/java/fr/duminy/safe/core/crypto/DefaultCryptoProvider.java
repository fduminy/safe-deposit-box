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
package fr.duminy.safe.core.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import fr.duminy.safe.core.Data;

public class DefaultCryptoProvider<T> implements CryptoProvider<T> {
	private final Data<?> key;
    public DefaultCryptoProvider(Data<?> key) {
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
        BlockCipher engine = new DESEngine();
        BufferedBlockCipher cipher = new BufferedBlockCipher(
                new CBCBlockCipher(engine));

        cipher.init(encrypt, new KeyParameter(key.getBytes()));

        int inputLength = input.getBytes().length; 
        byte[] output = new byte[cipher.getOutputSize(inputLength)];

        int outputLen = cipher.processBytes(input.getBytes(), 0, inputLength, output,
                0);
        try {
            cipher.doFinal(output, outputLen);
        } catch (CryptoException ce) {
            throw new CryptoProviderException("Error while "
                    + (encrypt ? "encrypting" : "decrypting"), ce);
        }
        
        return new Data<T>(input.getContainer(), output);
    }
}
