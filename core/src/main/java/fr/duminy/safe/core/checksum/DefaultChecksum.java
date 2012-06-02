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
package fr.duminy.safe.core.checksum;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.Data;
import fr.duminy.safe.core.WrongSizeException;

public class DefaultChecksum<T> implements Checksum<T> {
	private static Logger LOG = LoggerFactory.getLogger(DefaultChecksum.class);
	
    @Override
    public Data<T> verifyCheckSum(Data<T> data) throws ChecksumException {
        // read checksum
    	try {
	        Data<T> checksum = data.readBlock();
	        Data<T> actualData = data.readBlockAfter(checksum, 0);
	        
	        // compute checksum
	        byte[] sha1 = computeChecksum(ByteBuffer.wrap(actualData.getBytes()));
	        
	        // verify checksum
	        if (sha1.length != checksum.getSize()) {
	            throw new WrongChecksumException("actual checksum has wrong length");
	        }
	        
	        for (int i = 0; i < sha1.length; i++) {
	            if (sha1[i] != checksum.get(i)) {
	                throw new WrongChecksumException("wrong checksum");
	            }
	        }

	        return actualData;
    	} catch (WrongSizeException e) {
    		throw new WrongChecksumException(e.getMessage(), e);
    	}
    }

    @Override
    public Data<T> addCheckSum(Data<T> data) throws ChecksumException {
    	LOG.debug("addCheckSum: data: {}", data);
        
        // compute checksum
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        byte[] sha1 = computeChecksum(buffer);

        return Data.appendBlocks(sha1, data);        

    }

    private byte[] computeChecksum(ByteBuffer buffer)
            throws ChecksumException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new ChecksumException("Error while getting SHA-1 Message Digest", e);
        }
        byte[] d = new byte[buffer.remaining()];
        buffer.get(d);
        if (LOG.isDebugEnabled()) {
        	LOG.debug("computeChecksum: buffer: {}", Data.toString(d, 8));
        }
        
        byte[] sha1 = md.digest(d);
        return sha1;
    }
}
