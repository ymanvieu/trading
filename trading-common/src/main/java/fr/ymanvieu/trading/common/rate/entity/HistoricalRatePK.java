package fr.ymanvieu.trading.common.rate.entity;

import java.io.Serializable;
import java.time.Instant;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import lombok.Data;

@Data
public class HistoricalRatePK implements Serializable {

	private static final long serialVersionUID = -7492921102198280781L;
	
	private SymbolEntity fromcur;
	private SymbolEntity tocur;
	private Instant date;
}
