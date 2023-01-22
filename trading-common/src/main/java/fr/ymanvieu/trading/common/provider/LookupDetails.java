package fr.ymanvieu.trading.common.provider;

import lombok.Value;

@Value
public class LookupDetails {
	String code;
	String name;
	String source;
	String currency;
	String exchange;
	String providerCode;
}
