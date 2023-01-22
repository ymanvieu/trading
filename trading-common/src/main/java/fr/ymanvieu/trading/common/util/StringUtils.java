package fr.ymanvieu.trading.common.util;

import java.text.MessageFormat;

public class StringUtils {
	
	public static String format(String text, Object... args) {
		return (text == null) ? null : MessageFormat.format(text, args);
	}
}
