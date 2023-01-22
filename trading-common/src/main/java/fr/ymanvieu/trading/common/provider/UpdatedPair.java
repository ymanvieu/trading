package fr.ymanvieu.trading.common.provider;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UpdatedPair {
	private Integer id;
	private Instant lastUpdate;
	private String symbol;
	private String name;
	private String sourceCode;
	private String targetCode;
	private String exchange;
	private String providerCode;
}
