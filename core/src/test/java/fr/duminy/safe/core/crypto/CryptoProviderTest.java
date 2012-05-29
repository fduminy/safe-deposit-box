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
package fr.duminy.safe.core.crypto;


import static fr.duminy.safe.core.TestUtils.assertArrayNotEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.Data;
import fr.duminy.safe.core.TestClass;
import fr.duminy.safe.core.TestUtils;

public class CryptoProviderTest {
    private static Logger LOG = LoggerFactory.getLogger(CryptoProviderTest.class);
    
    private static final String KEY = "T!heKe#y"; // must be 8 chars
    private static final byte[] TEXT_TO_ENCRYPT = TestUtils.createTestData(16);
    private static final byte[] TEXT_TO_DECRYPT = {0x3b, 0x11, 0x0c, (byte)0xfe, 0x2d, (byte)0xe4, 0x60, 0x44, (byte)0x9d, 0x5d, (byte)0xb2, 0x3, 0x6c, 0x5d, (byte)0xa3, 0x52};
    
    private PicoContainer container;
    private CryptoProvider<TestClass> provider;
    
    @Before
    public void setUp() {
    	container = new DefaultPicoContainer();
        provider = createProvider(KEY);
    }

    @After
    public void tearDown() {
        provider = null;
    }

    @Test
    public void testEncrypt() throws CryptoProviderException {
    	Data<TestClass> input = new Data<TestClass>(container, TEXT_TO_ENCRYPT);
    	Data<TestClass> result = processBytes(true, input);
    	assertArrayEquals("wrong encrypted data", TEXT_TO_DECRYPT, result.getBytes());
    }

    @Test
    public void testDecrypt() throws CryptoProviderException {
    	Data<TestClass> input = new Data<TestClass>(container, TEXT_TO_DECRYPT);
    	Data<TestClass> result = processBytes(false, input);
    	assertArrayEquals("wrong decrypted data", TEXT_TO_ENCRYPT, result.getBytes());
    }

//    @Test(expected = WrongChecksumException.class)
//    public void testDecryptWithWrongKey() throws CryptoProviderException {
//    	Data<TestClass> d = processBytes(true, new Data<TestClass>(container, TEXT_TO_ENCRYPT));
//    	
//        String wrongKey = "01234567";
//        assertFalse(wrongKey.equals(KEY));
//        
//        processBytes(createProvider(wrongKey), false, d);
//    }

    @Test
    public void testEncryptDecrypt() throws CryptoProviderException {
    	Data<TestClass> input = new Data<TestClass>(container, TEXT_TO_ENCRYPT);
    	Data<TestClass> d = processBytes(true, input);
    	Data<TestClass> result = processBytes(false, d);
                
        assertArrayEquals("input data must be unchanged after encryption followed by decryption", input.getBytes(), result.getBytes());
    }
    
    private Data<TestClass> processBytes(boolean encrypt, Data<TestClass> data) throws CryptoProviderException {
    	return processBytes(provider, encrypt, data);
    }
    
    private Data<TestClass> processBytes(CryptoProvider<TestClass> provider, boolean encrypt, Data<TestClass> data) throws CryptoProviderException {
    	Data<TestClass> output = encrypt ? provider.encrypt(data) : provider.decrypt(data);
        assertNotNull(output);
        assertTrue("output length must be > 0", output.getBytes().length > 0);
        assertArrayNotEquals("output must not be same as input", data.getBytes(), output.getBytes());
        LOG.info((encrypt ? "encrypt" : "decrypt") + ": in={}\nout={}", 
                TestUtils.toString(data.getBytes()), TestUtils.toString(output.getBytes()));
        
        return output;
    }
    
    private CryptoProvider<TestClass> createProvider(String key) {
    	return new DefaultCryptoProvider<TestClass>(new Data<TestClass>(container, key.getBytes()));    	
    }
}
