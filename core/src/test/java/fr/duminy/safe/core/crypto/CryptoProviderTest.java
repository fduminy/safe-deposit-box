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

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.Data;
import fr.duminy.safe.core.TestClass;
import fr.duminy.safe.core.TestUtils;

@RunWith(Theories.class)
public class CryptoProviderTest {
    private static Logger LOG = LoggerFactory.getLogger(CryptoProviderTest.class);
    
    private static final String KEY = "T!heKe#y"; // must be 8 chars

	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_0 = new CryptoData(0, new byte[]{ (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0xb6, (byte)0xa0, (byte)0x0b, (byte)0x33, (byte)0x18, (byte)0x37, (byte)0x20 });
	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_1 = new CryptoData(1, new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x03, (byte)0xb6, (byte)0xa0, (byte)0x0b, (byte)0x33, (byte)0x18, (byte)0x37, (byte)0x20 });
    
	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_7 = new CryptoData(7, new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0xb5, (byte)0x0e, (byte)0x8a, (byte)0xfa, (byte)0x8e, (byte)0x22, (byte)0x68, (byte)0xee });
	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_8 = new CryptoData(8, new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x3b, (byte)0x11, (byte)0x0c, (byte)0xfe, (byte)0x2d, (byte)0xe4, (byte)0x60, (byte)0x44, (byte)0x27, (byte)0x39, (byte)0xda, (byte)0x80, (byte)0xf9, (byte)0xd5, (byte)0x7c, (byte)0x2b });
	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_9 = new CryptoData(9, new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x09, (byte)0x3b, (byte)0x11, (byte)0x0c, (byte)0xfe, (byte)0x2d, (byte)0xe4, (byte)0x60, (byte)0x44, (byte)0xf4, (byte)0x75, (byte)0x0c, (byte)0x60, (byte)0xc0, (byte)0x3d, (byte)0x63, (byte)0xa0 });
	
	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_15 = new CryptoData(15, new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0f, (byte)0x3b, (byte)0x11, (byte)0x0c, (byte)0xfe, (byte)0x2d, (byte)0xe4, (byte)0x60, (byte)0x44, (byte)0xaf, (byte)0xeb, (byte)0xb4, (byte)0x61, (byte)0x17, (byte)0x51, (byte)0xcf, (byte)0x8a });
	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_16 = new CryptoData(16, new byte[] {0x00, 0x00, 0x00, 0x10, 0x3b, 0x11, 0x0c, (byte)0xfe, 0x2d, (byte)0xe4, 0x60, 0x44, (byte)0x9d, 0x5d, (byte)0xb2, 0x3, 0x6c, 0x5d, (byte)0xa3, 0x52, (byte)0xaf, (byte)0x6e, (byte)0x5c, (byte)0x75, (byte)0x5c, (byte)0xc7, (byte)0x7a, (byte)0x2e});
	@DataPoint public static final CryptoData TEXT_TO_ENCRYPT_17 = new CryptoData(17, new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x11, (byte)0x3b, (byte)0x11, (byte)0x0c, (byte)0xfe, (byte)0x2d, (byte)0xe4, (byte)0x60, (byte)0x44, (byte)0x9d, (byte)0x5d, (byte)0xb2, (byte)0x03, (byte)0x6c, (byte)0x5d, (byte)0xa3, (byte)0x52, (byte)0x09, (byte)0x99, (byte)0xaf, (byte)0xfa, (byte)0x0c, (byte)0x40, (byte)0xa0, (byte)0x02 });
	
    private PicoContainer container;
    private CryptoProvider<TestClass> provider;
    
    @Before
    public void setUp() {
    	container = new DefaultPicoContainer();
        provider = createProvider(KEY);
    }

    @After
    public void tearDown() {
    	container = null; 
        provider = null;
    }

    @Theory
    public void testEncrypt(CryptoData data) throws CryptoProviderException {
    	Data<TestClass> input = new Data<TestClass>(container, data.getTextToEncrypt());
    	Data<TestClass> result = processBytes(true, input);
    	LOG.debug("testEncrypt: result={}", result.toJavaString());
    	assertArrayEquals("wrong encrypted data", data.getTextToDecrypt(), result.getBytes());
    }

    @Theory
    public void testDecrypt(CryptoData data) throws CryptoProviderException {
    	Data<TestClass> input = new Data<TestClass>(container, data.getTextToDecrypt());
    	Data<TestClass> result = processBytes(false, input);
    	assertArrayEquals("wrong decrypted data", data.getTextToEncrypt(), result.getBytes());
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

    @Theory
    public void testEncryptDecrypt(CryptoData data) throws CryptoProviderException {
    	Data<TestClass> input = new Data<TestClass>(container, data.getTextToEncrypt());
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
    	assertArrayNotEquals("output must not be same as input", data.getBytes(), output.getBytes());
        LOG.info((encrypt ? "encrypt" : "decrypt") + ": in={}\nout={}", data, output);
        
        return output;
    }
    
    private CryptoProvider<TestClass> createProvider(String key) {
    	return new DefaultCryptoProvider<TestClass>(new Data<TestClass>(container, key.getBytes()));    	
    }

    private static class CryptoData {
        final byte[] textToEncrypt;
        final byte[] textToDecrypt;
		public CryptoData(int textSize, byte[] textToDecrypt) {
			super();
			this.textToEncrypt = TestUtils.createTestData(textSize);
			this.textToDecrypt = textToDecrypt;
		}
        public byte[] getTextToDecrypt() {
			return textToDecrypt;
		}
        public byte[] getTextToEncrypt() {
			return textToEncrypt;
		}
        @Override
        public String toString() {
        	return "CryptoData[size=" + textToEncrypt.length + "]";
        }
    }    
}
