package fr.ymanvieu.trading.webapp.symbol.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SymbolDTO {
	
	private String code;
	private String name;
	private String countryFlag;		
}
