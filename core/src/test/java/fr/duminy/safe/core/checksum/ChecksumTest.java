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
package fr.duminy.safe.core.checksum;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;

import fr.duminy.safe.core.Data;
import fr.duminy.safe.core.TestClass;

public class ChecksumTest {
    private static final String TEXT = "This is a simple text";
    private static final byte[] TEXT_WITH_CHECKSUM = {0, 0, 0, 20, -81, 79, -10, -39, 107, -24, -52, -62, 54, 93, 92, -81, -26, -84, 10, 3, 49, 26, -112, 18, 84, 104, 105, 115, 32, 105, 115, 32, 97, 32, 115, 105, 109, 112, 108, 101, 32, 116, 101, 120, 116};
    
    private MutablePicoContainer container;
    
    @Before
    public void setUp() {
        container = new DefaultPicoContainer(new Caching());        
        container.addComponent(DefaultChecksum.class);
    }

    @After
    public void tearDown() {
        container = null;
    }

    @Test
    public void testAddChecksum() throws ChecksumException {                
        testChecksum(TEXT.getBytes(), TEXT_WITH_CHECKSUM, false);
    }

    @Test
    public void testVerifyChecksum() throws ChecksumException {
        testChecksum(TEXT_WITH_CHECKSUM, TEXT.getBytes(), true);
    }

    @Test(expected = WrongChecksumException.class)
    public void testWrongChecksumSize() throws ChecksumException {
        testWrongChecksumSize(0, (byte) 255);
    }
    
    @Test(expected = WrongChecksumException.class)
    public void testWrongChecksum() throws ChecksumException {
        testWrongChecksumSize(4, (byte) 255);
    }
    
    private void testWrongChecksumSize(int indexToModify, byte valueToSet) throws ChecksumException {
        byte[] d = (byte[]) TEXT_WITH_CHECKSUM.clone();
        d[indexToModify] = valueToSet;
                
        Data<TestClass> data = new Data<TestClass>(container, d);        
        data.verifyCheckSum();
    }
    
    private void testChecksum(byte[] input, byte[] expectedOutput, boolean verify) throws ChecksumException {
        Data<TestClass> data = new Data<TestClass>(container, input);
        
        Data<TestClass> result = verify ? data.verifyCheckSum() : data.addCheckSum();
        
        assertNotNull(result);
        assertNotSame(data, result);
        assertArrayEquals(expectedOutput, result.getBytes());
    }
}
