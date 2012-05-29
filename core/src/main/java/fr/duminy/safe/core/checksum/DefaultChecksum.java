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

import fr.duminy.safe.core.Data;

public class DefaultChecksum<T> implements Checksum<T> {

    @Override
    public Data<T> verifyCheckSum(Data<T> data) throws ChecksumException {
        // read size of checksum
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        int checksumSize = buffer.getInt();
        if (buffer.remaining() < checksumSize) {
            throw new WrongChecksumException("checksumSize is too big");
        }
        if (checksumSize <= 0) {
            throw new WrongChecksumException("checksumSize <= 0");
        }

        // read checksum
        byte[] checksum = new byte[checksumSize];
        buffer.get(checksum);
        
        // compute checksum
        buffer.mark();
        byte[] sha1 = computeChecksum(buffer);
        
        // verify checksum
        if (sha1.length != checksum.length) {
            throw new WrongChecksumException("actual checksum has wrong length");
        }
        
        for (int i = 0; i < sha1.length; i++) {
            if (sha1[i] != checksum[i]) {
                throw new WrongChecksumException("wrong checksum");    
            }
        }
        
        buffer.reset();
        byte[] buf = new byte[buffer.remaining()];
        buffer.get(buf);
        return new Data<T>(data.getContainer(), buf);
    }

    @Override
    public Data<T> addCheckSum(Data<T> data) throws ChecksumException {
        // compute checksum
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        buffer.mark();
        byte[] sha1 = computeChecksum(buffer);
        buffer.reset();
        
        // write size of checksum
        ByteBuffer result = ByteBuffer.allocate(4 + sha1.length + buffer.remaining()); // size of int = 4
        result.putInt(sha1.length);

        // write checksum
        result.put(sha1);
        
        // write data
        result.put(buffer);
        
        return new Data<T>(data.getContainer(), result.array());
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
        byte[] sha1 = md.digest(d);
        return sha1;
    }
}
