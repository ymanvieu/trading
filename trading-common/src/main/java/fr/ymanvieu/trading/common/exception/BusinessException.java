package fr.ymanvieu.trading.common.exception;

import java.util.Arrays;

public class BusinessException extends RuntimeException {

	private final Object[] args;

	private final String key;

	protected BusinessException(String key, Object... args) {
		super(key + ": " + Arrays.asList(args));
		this.key = key;
		this.args = args;
	}

	public String getKey() {
		return key;
	}

	public Object[] getArgs() {
		return args;
	}
}
