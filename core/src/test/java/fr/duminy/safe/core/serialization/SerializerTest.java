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

import static fr.duminy.safe.core.TestDataUtils.buildModel;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;

import fr.duminy.safe.core.Data;
import fr.duminy.safe.core.model.Model;

@RunWith(Theories.class)
public class SerializerTest {
	@DataPoint public static TestData EMPTY_MODEL = new TestData(new Model(), new DefaultSerializer<Model>());
	@DataPoint public static TestData COMPLETE_MODEL = new TestData(buildModel(), new DefaultSerializer<Model>());
		
	@Theory
	public void testSerialize(TestData data) throws SerializerException {
		Data<Model> result = data.serializer.serialize(data.container, data.model);
				
		assertThat(result).isNotNull().isNotEmpty();
	}
	
	@Theory
	public void testSerializeAndDeserialize(TestData data) throws SerializerException {
		Model result = data.serializer.serialize(data.container, data.model).deserialize();
		
		assertThat(result).isNotNull().isNotSameAs(data.model).isEqualTo(data.model);
	}
	
	private static class TestData {
		private final DefaultPicoContainer container = new DefaultPicoContainer();
		private final Model model;
		private final Serializer<Model> serializer;
		public TestData(Model model, Serializer<Model> serializer) {
			super();
			this.model = model;
			this.serializer = serializer;
			container.addComponent(serializer);
		}
	}
}
