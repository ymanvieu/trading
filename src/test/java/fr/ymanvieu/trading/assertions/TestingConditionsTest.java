/**
 * Copyright (C) 2016 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.assertions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Maps;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestingConditionsTest {
	
	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Test
	public void testCreateOK() {
		SuperObject object =  new SuperObject();
		
		object.setFieldBoolean(true);
		object.setFieldString("str");
		object.setFieldInt(1);
		object.setFieldLong(1);
		object.setFieldFloat(1f);
		object.setFieldDouble(1.8);
		
		object.setFieldList(Arrays.asList(""));
		object.setFieldMap(Maps.newHashMap(5, 0.8));
		object.setFieldObject(new Object());
		
		Assertions.assertThat(object).has(TestingConditions.create());
	}
	
	@Test
	public void testCreateKO() {
		SuperObject object =  new SuperObject();
		
		object.setFieldBoolean(true);
		object.setFieldString("str");
		object.setFieldInt(1);
		object.setFieldLong(1);
		object.setFieldFloat(1f);
		
		object.setFieldList(Arrays.asList(""));
		object.setFieldMap(Maps.newHashMap(5, 0.8));
		object.setFieldObject(new Object());
		
		expectAssertionErrorWithMessage("fieldDouble");
		
		// fieldDouble not set
		Assertions.assertThat(object).has(TestingConditions.create());
	}

	
	@Test
	public void testCreateOK_WithUnknownField() {
		SuperObject object =  new SuperObject();
		
		object.setFieldBoolean(true);
		object.setFieldString("str");
		object.setFieldInt(1);
		object.setFieldLong(1);
		object.setFieldFloat(1f);
		
		object.setFieldList(Arrays.asList(""));
		object.setFieldMap(Maps.newHashMap(5, 0.8));
		object.setFieldObject(new Object());
		
		// fieldDouble not set
		Assertions.assertThat(object).has(TestingConditions.create("fieldDouble"));
	}
	
	@Test
	public void testCreateKO_WithUnknownField() {
		SuperObject object =  new SuperObject();
		
		object.setFieldBoolean(true);
		object.setFieldString("str");
		object.setFieldInt(1);
		object.setFieldLong(1);
		object.setFieldFloat(1f);
		object.setFieldDouble(1.8);
		
		object.setFieldList(Arrays.asList(""));
		object.setFieldMap(Maps.newHashMap(5, 0.8));
		object.setFieldObject(new Object());
		
		expectAssertionErrorWithMessage("fieldUnknown");
		
		// fieldUnknown doesn't exist
		Assertions.assertThat(object).has(TestingConditions.create("fieldUnknown"));
	}
	
	
	
	private void expectAssertionErrorWithMessage(String message) {
		expect.expect(AssertionError.class);
		expect.expectMessage(message);
	}


	protected static class SuperObject {
		
		private boolean fieldBoolean;
		private String fieldString;
		public int fieldInt;
		private long fieldLong;
		private float fieldFloat;
		private double fieldDouble;
		protected List<?> fieldList;
		Map<?, ?> fieldMap;
		private Object fieldObject;
		
		public boolean isFieldBoolean() {
			return fieldBoolean;
		}
		public void setFieldBoolean(boolean fieldBoolean) {
			this.fieldBoolean = fieldBoolean;
		}
		public String getFieldString() {
			return fieldString;
		}
		public void setFieldString(String fieldString) {
			this.fieldString = fieldString;
		}
		public int getFieldInt() {
			return fieldInt;
		}
		public void setFieldInt(int fieldInt) {
			this.fieldInt = fieldInt;
		}
		public long getFieldLong() {
			return fieldLong;
		}
		public void setFieldLong(long fieldLong) {
			this.fieldLong = fieldLong;
		}
		public float getFieldFloat() {
			return fieldFloat;
		}
		public void setFieldFloat(float fieldFloat) {
			this.fieldFloat = fieldFloat;
		}
		public double getFieldDouble() {
			return fieldDouble;
		}
		public void setFieldDouble(double fieldDouble) {
			this.fieldDouble = fieldDouble;
		}
		public List<?> getFieldList() {
			return fieldList;
		}
		public void setFieldList(List<?> fieldList) {
			this.fieldList = fieldList;
		}
		public Map<?, ?> getFieldMap() {
			return fieldMap;
		}
		public void setFieldMap(Map<?, ?> fieldMap) {
			this.fieldMap = fieldMap;
		}
		public Object getFieldObject() {
			return fieldObject;
		}
		public void setFieldObject(Object fieldObject) {
			this.fieldObject = fieldObject;
		}
	}
}
