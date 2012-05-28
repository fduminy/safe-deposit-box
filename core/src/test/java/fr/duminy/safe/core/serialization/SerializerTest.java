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
package fr.duminy.safe.core.serialization;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

import fr.duminy.safe.core.model.Model;

public class SerializerTest {
	private DefaultPicoContainer container = new DefaultPicoContainer();
	private Model model;
    private Serializer<Model> serializer;
    
    @Before
    public void setUp() {
    	serializer = new DefaultSerializer<Model>();
    	container.addComponent(serializer);
    	model = new Model();
    }

    @After
    public void tearDown() {
    	serializer = null;
    }

	@Test
	public void testSerialize() throws SerializerException {
		serializer.serialize(container, model);
	}
	
	@Test
	public void testSerializeAndDeserialize() throws SerializerException {
		Model result = serializer.serialize(container, model).deserialize();
        assertNotNull(result);
        assertNotSame(model, result);
	}
}
