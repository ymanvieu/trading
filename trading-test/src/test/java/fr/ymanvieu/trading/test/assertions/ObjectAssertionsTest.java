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
package fr.ymanvieu.trading.test.assertions;

import static fr.ymanvieu.trading.test.assertions.ObjectAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ObjectAssertionsTest {

	private static SuperObjet createFilledObject() {
		SuperObjet object = new SuperObjet();

		object.setFieldBoolean(true);
		object.setFieldString("str");
		object.setFieldInt(1);
		object.setFieldLong(1);
		object.setFieldFloat(1f);
		object.setFieldDouble(1.8);

		object.setFieldList(Arrays.asList(""));
		object.setFieldMap(Map.of(5, 0.8));
		object.setFieldObject(new Object());

		return object;
	}

	@Test
	public void testHasAllFieldsSet() {
		SuperObjet object = createFilledObject();

		assertThat(object).hasAllFieldsSet();
	}

	@Test
	public void testHasAllFieldsSet_WithUnsetField() {
		SuperObjet object = createFilledObject();
		object.fieldDouble = 0;

		// fieldDouble unset
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(object).hasAllFieldsSet())
			.withMessageContaining("fieldDouble");
	}

	@Test
	public void testHasAllFieldsSet_WithIgnoredField() {
		SuperObjet object = createFilledObject();
		object.fieldDouble = 0;

		// fieldDouble unset
		assertThat(object).ignoringFields("fieldDouble").hasAllFieldsSet();
	}

	@Test
	public void testIgnoringFieldsKO_WithUnknownField() {
		SuperObjet object = createFilledObject();

		// fieldUnknown doesn't exist
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> assertThat(object).ignoringFields("fieldUnknown"))
			.withMessageContaining("fieldUnknown");
	}

	@Test
	public void testHasNoFieldSet() {
		SuperObjet object = new SuperObjet();

		assertThat(object).hasNoFieldSet();
	}

	@Test
	public void testHasNoFieldsSet_WithFieldSet() {
		SuperObjet object = new SuperObjet();
		object.fieldBoolean = true;

		// fieldBoolean set
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(object).hasNoFieldSet())
			.withMessageContaining("fieldBoolean");
	}

	@Test
	public void testHasNoFieldSet_WithIgnoredSetField() {
		SuperObjet object = new SuperObjet();
		object.fieldDouble = 1.5;

		// fieldDouble
		assertThat(object).ignoringFields("fieldDouble").hasNoFieldSet();
	}

	protected static class SuperObjet {

		private boolean fieldBoolean;
		private String fieldString;
		public int fieldInt;
		private long fieldLong;
		private float fieldFloat;
		private double fieldDouble;
		protected List<?> fieldList;

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

		Map<?, ?> fieldMap;
		private Object fieldObject;
	}
}
