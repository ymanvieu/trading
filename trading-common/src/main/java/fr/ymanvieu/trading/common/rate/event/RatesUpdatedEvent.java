package fr.ymanvieu.trading.common.rate.event;

import java.util.List;

import fr.ymanvieu.trading.common.rate.Rate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RatesUpdatedEvent {

	private List<Rate> rates;
}
