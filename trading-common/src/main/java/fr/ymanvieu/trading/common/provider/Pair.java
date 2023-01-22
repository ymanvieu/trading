package fr.ymanvieu.trading.common.provider;

import fr.ymanvieu.trading.common.symbol.Symbol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Pair {
	private Integer id;
	private String symbol;
	private String name;
	private Symbol source;
	private Symbol target;
	private String exchange;
	private String providerCode;
}
