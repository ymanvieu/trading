package fr.ymanvieu.trading.common.admin;

import fr.ymanvieu.trading.common.provider.Quote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PairInfo {
	private Integer id;
	private String code;
	private String name;
	private Quote quote;
}
