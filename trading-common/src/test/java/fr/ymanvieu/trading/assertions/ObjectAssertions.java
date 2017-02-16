package fr.ymanvieu.trading.assertions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Preconditions;

/**
 * Makes assertions on fields' state of an object.
 * <p>
 * 
 * Inherited/static fields of the object to verify are ignored.
 *
 */
public class ObjectAssertions extends Assertions {

	public static ObjectAssert assertThat(Object actual) {
		return new ObjectAssert(actual);
	}

	public static class ObjectAssert extends AbstractObjectAssert<ObjectAssert, Object> {

		private static final String MSG_IGNORED_FIELD_NOT_FOUND = "Ignored fields %s were not found in class %s, maybe a typo or have been renamed";
		private static final String MSG_TRUE = "%s must be true";
		private static final String MSG_FALSE = "%s must be false";
		private static final String MSG_NOT_NULL = "%s must be non-null";
		private static final String MSG_NOT_EMPTY = "%s must be non-empty";

		private static final String MSG_ZERO = "%s must be == 0";
		private static final String MSG_NOT_ZERO = "%s must be != 0";

		private static final String MSG_EMPTY = "%s must be empty";

		private final List<String> ignoredFields = new ArrayList<>();

		protected ObjectAssert(Object actual) {
			super(actual, ObjectAssert.class);
		}

		/**
		 * Ignored fields during the verification.
		 * <p>
		 * Discards all previously specified fields.
		 * 
		 * @throws IllegalArgumentException
		 *             if one of the specified field cannot be found.
		 */
		public ObjectAssert ignoringFields(String... ignoredFields) {
			List<String> ignoredFieldsToSearch = Lists.newArrayList(ignoredFields);

			for (String ignoredField : ignoredFields) {

				try {
					actual.getClass().getDeclaredField(ignoredField);
					ignoredFieldsToSearch.remove(ignoredField);
				} catch (NoSuchFieldException e) {
				}
			}

			Preconditions.checkArgument(ignoredFieldsToSearch.isEmpty(), MSG_IGNORED_FIELD_NOT_FOUND, ignoredFieldsToSearch,
					actual.getClass().getSimpleName());

			this.ignoredFields.clear();
			this.ignoredFields.addAll(Lists.newArrayList(ignoredFields));

			return this;
		}

		public void hasAllFieldsSet() {
			SoftAssertions softAss = new SoftAssertions();

			Field[] allFields = actual.getClass().getDeclaredFields();

			for (Field field : allFields) {

				if (ignoredFields.contains(field.getName())) {
					continue;
				}

				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if (!Modifier.isPublic(field.getModifiers())) {
					field.setAccessible(true);
				}

				Object o;

				try {
					o = field.get(actual);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}

				softAss.assertThat(o).overridingErrorMessage(MSG_NOT_NULL, field.getName()).isNotNull();

				if (o instanceof Boolean) {
					softAss.assertThat(((Boolean) o)).overridingErrorMessage(MSG_TRUE, field.getName()).isTrue();
				}

				if (o instanceof Number) {
					softAss.assertThat(((Number) o).doubleValue()).overridingErrorMessage(MSG_NOT_ZERO, field.getName()).isNotZero();
				}

				if (o instanceof String) {
					softAss.assertThat(((String) o)).overridingErrorMessage(MSG_NOT_EMPTY, field.getName()).isNotEmpty();
				}

				if (o instanceof Iterable<?>) {
					softAss.assertThat(((Iterable<?>) o)).overridingErrorMessage(MSG_NOT_EMPTY, field.getName()).isNotEmpty();
				}

				if (o instanceof Map<?, ?>) {
					softAss.assertThat(((Map<?, ?>) o)).overridingErrorMessage(MSG_NOT_EMPTY, field.getName()).isNotEmpty();
				}
			}

			softAss.assertAll();
		}

		public void hasNoFieldSet() {
			SoftAssertions softAss = new SoftAssertions();

			Field[] allFields = actual.getClass().getDeclaredFields();

			for (Field field : allFields) {

				if (ignoredFields.contains(field.getName())) {
					continue;
				}

				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if (!Modifier.isPublic(field.getModifiers())) {
					field.setAccessible(true);
				}

				Object o;

				try {
					o = field.get(actual);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}

				softAss.assertThat(o).overridingErrorMessage(MSG_NOT_NULL, field.getName()).isNotNull();

				if (o instanceof Boolean) {
					softAss.assertThat(((Boolean) o)).overridingErrorMessage(MSG_FALSE, field.getName()).isFalse();
				}

				if (o instanceof Number) {
					softAss.assertThat(((Number) o).doubleValue()).overridingErrorMessage(MSG_ZERO, field.getName()).isZero();
				}

				if (o instanceof String) {
					softAss.assertThat(((String) o)).overridingErrorMessage(MSG_EMPTY, field.getName()).isEmpty();
				}

				if (o instanceof Iterable<?>) {
					softAss.assertThat(((Iterable<?>) o)).overridingErrorMessage(MSG_EMPTY, field.getName()).isEmpty();
				}

				if (o instanceof Map<?, ?>) {
					softAss.assertThat(((Map<?, ?>) o)).overridingErrorMessage(MSG_EMPTY, field.getName()).isEmpty();
				}
			}

			softAss.assertAll();
		}
	}
}