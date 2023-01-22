package fr.ymanvieu.trading.scenario;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import fr.ymanvieu.trading.scenario.framework.Scenario;
import fr.ymanvieu.trading.scenario.framework.given.Pair;
import fr.ymanvieu.trading.scenario.framework.then.CreatePairVerification;
import fr.ymanvieu.trading.scenario.framework.then.DeletePairVerification;
import fr.ymanvieu.trading.scenario.framework.then.GetLatestRateVerification;
import fr.ymanvieu.trading.scenario.framework.then.GetPairsVerification;
import fr.ymanvieu.trading.scenario.framework.then.UpdatePairVerification;
import fr.ymanvieu.trading.scenario.framework.when.CreatePair;
import fr.ymanvieu.trading.scenario.framework.when.DeletePair;
import fr.ymanvieu.trading.scenario.framework.when.GetLatestRate;
import fr.ymanvieu.trading.scenario.framework.when.GetPairs;
import fr.ymanvieu.trading.scenario.framework.when.UpdatePair;

@WithMockUser(roles = "ADMIN", username = "1")
public class AdminScenario extends Scenario {

    @Test
    void createPair() {
        when(new CreatePair().code("AAPL"));
        verify(new CreatePairVerification());
    }

    @Test
    void getPair() {
        given(new Pair().code("AAPL"));

        when(new GetPairs().code("AA"));
        verify(new GetPairsVerification().resultSize(2).existingPairsSize(1));
    }

    @Test
    void deletePair() {
        Pair pair;
        given(pair = new Pair().code("AAPL"));

        when(new DeletePair().id(pair.id()));
        verify(new DeletePairVerification());

        when(new GetPairs().code(pair.code()));
        verify(new GetPairsVerification().existingPairsSize(0));
    }

    @Test
    void deletePair_withSymbol() {
        Pair pair;
        given(pair = new Pair().code("AAPL"));

        when(new DeletePair().id(pair.id()).withSymbol(true));
        verify(new DeletePairVerification());

        when(new GetLatestRate().fromcurCode(pair.code()).tocurCode("USD"));
        verify(new GetLatestRateVerification());
    }

    @Test
    void updatePair() {
        Pair pair;
        given(pair = new Pair().code("AAPL"));

        when(new UpdatePair().id(pair.id()).symbol("MSFT"));
        verify(new UpdatePairVerification());
    }
}
