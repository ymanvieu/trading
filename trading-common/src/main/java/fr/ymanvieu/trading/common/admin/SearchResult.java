package fr.ymanvieu.trading.common.admin;

import java.util.List;
import java.util.Objects;

import fr.ymanvieu.trading.common.provider.LookupInfo;
import fr.ymanvieu.trading.common.provider.UpdatedPair;

public record SearchResult(List<UpdatedPair> existingPairs, List<LookupInfo> availableSymbols) {

	public SearchResult {
		Objects.requireNonNull(existingPairs, "existingPairs");
		Objects.requireNonNull(availableSymbols, "availableSymbols");
	}
}
