package fr.ymanvieu.trading.common.rate.entity;

import java.io.Serializable;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import lombok.Data;

@Data
public class LatestRatePK implements Serializable {
	
	private static final long serialVersionUID = -5231047284548792369L;
	
	private SymbolEntity fromcur;
	private SymbolEntity tocur;
}
