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
package fr.duminy.safe.core.serialization;

import static fr.duminy.safe.core.Utils.closeQuietly;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.picocontainer.PicoContainer;

import fr.duminy.safe.core.Data;

public class DefaultSerializer<T> implements Serializer<T> {
    @Override
    public Data<T> serialize(PicoContainer container, T object) throws SerializerException {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        
        try {
            baos = new ByteArrayOutputStream();            
            oos = new ObjectOutputStream(baos);            
            oos.writeObject(object);
        } catch (IOException e) {
            throw new SerializerException(e);
        } finally {
            closeQuietly(oos);
            closeQuietly(baos);
        }
        
        return new Data<T>(container, baos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Data<T> data) throws SerializerException {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        
        try {
            bais = new ByteArrayInputStream(data.getBytes());
            ois = new ObjectInputStream(bais);
            
            return (T) ois.readObject();
        } catch (IOException e) {
            throw new SerializerException(e);
        } catch (ClassNotFoundException e) {
            throw new SerializerException(e);
        } finally {
            closeQuietly(ois);
            closeQuietly(bais);
        }
    }
}
