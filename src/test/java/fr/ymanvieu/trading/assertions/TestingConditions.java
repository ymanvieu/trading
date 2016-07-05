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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.assertj.core.util.Lists;

import com.google.common.base.Strings;

public class TestingConditions<T> extends Condition<T> {
	
	private static boolean DEFAULT_BOOLEAN;
	
	private static int DEFAULT_INT;
	private static long DEFAULT_LONG;
	
	private static float DEFAULT_FLOAT;
	private static double DEFAULT_DOUBLE;
	
	private final List<String> ignoredFields;
	
	private TestingConditions() {
		ignoredFields = new ArrayList<>();
	}
	
	private TestingConditions(String... ignoredFields) {
		this.ignoredFields = Lists.newArrayList(ignoredFields);
	}

	@Override
	public boolean matches(T value) {
		
	    Field[] allFields = value.getClass().getDeclaredFields();
	   
	    List<String> ignoredFieldsToFound = Lists.newArrayList(ignoredFields);
	    
	    for (Field field : allFields) {
	    	
	    	if(Modifier.isStatic(field.getModifiers())) {
	    		continue;
	    	}
	    	
	    	if(!Modifier.isPublic(field.getModifiers())) {
	    		field.setAccessible(true);
	    	}
	    	
	    	if(ignoredFields.contains(field.getName())) {
	    		ignoredFieldsToFound.remove(field.getName());
	    		continue;
	    	}
	    	
	    	Object o;
	    	
			try {
				o = field.get(value);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			} 
			
			
			if (o == null) {
				Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be non-null for testing");
			} 
			
			if (o instanceof Boolean) {			    		
	    		if((Boolean) o == DEFAULT_BOOLEAN) {
	    			Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be true for testing");
	    		}
	    	} 
			
			if(o instanceof Integer) {
	    		if (((Integer) o) == DEFAULT_INT) {
					Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be != 0 for testing");
				}
	    	} 
			
			if(o instanceof Long) {
	    		if (((Long) o) == DEFAULT_LONG) {
					Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be != 0 for testing");
				}
	    	} 
			
			if(o instanceof Float) {
	    		if (((Float) o) == DEFAULT_FLOAT) {
					Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be != 0 for testing");
				}
	    	}
			
			if(o instanceof Double) {
	    		if (((Double) o) == DEFAULT_DOUBLE) {
					Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be != 0 for testing");
				}
	    	}
	    	
	    	if (o instanceof String) {
	    		if (Strings.isNullOrEmpty((String) o)) {
					Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be non-empty for testing");
				}
	    	} 
	    	
	    	if (o instanceof Collection<?>) {
	    		if (((Collection<?>)o).isEmpty()) {
					Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be non-empty for testing");
				}
	    	} 
	    	
	    	if (o instanceof Map<?, ?>) {	    		
	    		if (((Map<?, ?>)o).isEmpty()) {
					Assertions.fail(field.getType().getSimpleName() + " " + field.getName() + " must be non-empty for testing");
				}
	    	}
	    }
	    
	    
	    if(!ignoredFieldsToFound.isEmpty()) {
	    	Assertions.fail("Ignored fields " + ignoredFieldsToFound + " were not found in class "
	    			+ value.getClass().getSimpleName()+ ", maybe a typo or it has been renamed");
	    }
	    
	    return true;
	}
	
	public static <T> TestingConditions<T> create(String... ignoredFields) {
		return new TestingConditions<>(ignoredFields);
	}
	
}
