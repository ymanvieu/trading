package fr.ymanvieu.trading.common.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.common.provider.lookup.LookupProvider;

@Service
public class LookupService {

	@Autowired
	private List<LookupProvider> lProviders;

	public List<LookupInfo> search(String symbolOrName) throws IOException {

		List<LookupInfo> result = new ArrayList<>();

		for (LookupProvider lp : lProviders) {
			result.addAll(lp.search(symbolOrName));
		}

		return result;
	}

	public LookupDetails getDetails(String code, String provider) throws IOException {
		for (LookupProvider lp : lProviders) {
			if (lp.getProviderCode().equals(provider)) {
				return lp.getDetails(code);
			}
		}

		throw new IllegalArgumentException("No provider found for code: " + provider);
	}

}
