package fr.ymanvieu.trading.common.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupInfo {
	private String code;
	private String name;
	private String exchange;
	private String type;
	private String providerCode;
}
