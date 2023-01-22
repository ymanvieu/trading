package fr.ymanvieu.trading.common.provider.lookup;

import java.io.IOException;
import java.util.List;

import fr.ymanvieu.trading.common.provider.LookupDetails;
import fr.ymanvieu.trading.common.provider.LookupInfo;

public interface LookupProvider {

	List<LookupInfo> search(String symbolOrName) throws IOException;

	String getProviderCode();

	LookupDetails getDetails(String code) throws IOException;
}
