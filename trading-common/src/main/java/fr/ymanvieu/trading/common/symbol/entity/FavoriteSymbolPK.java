package fr.ymanvieu.trading.common.symbol.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class FavoriteSymbolPK implements Serializable {

	private static final long serialVersionUID = 6658378738799786817L;

	private Integer userId;
	private String fromSymbolCode;
	private String toSymbolCode;
}
