package fr.ymanvieu.trading.common.symbol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "code")
@AllArgsConstructor
@NoArgsConstructor
public class Symbol {
	private String code;
	private String name;
	private String countryFlag;
	private Symbol currency;
}
