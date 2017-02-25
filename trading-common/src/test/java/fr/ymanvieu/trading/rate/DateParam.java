package fr.ymanvieu.trading.rate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

import fr.ymanvieu.trading.rate.DateParam.FormattedDateConverter;
import fr.ymanvieu.trading.test.time.DateParser;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;
import junitparams.converters.Param;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Param(converter = FormattedDateConverter.class)
public @interface DateParam {

	public static class FormattedDateConverter implements Converter<DateParam, Date> {

		@Override
		public void initialize(DateParam annotation) {
		}

		@Override
		public Date convert(Object param) throws ConversionFailedException {
			if(param == null)
				return null;
			
			return DateParser.parse(param.toString());
		}
	}
}
