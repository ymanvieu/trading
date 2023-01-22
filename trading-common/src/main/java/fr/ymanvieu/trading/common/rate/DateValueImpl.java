package fr.ymanvieu.trading.common.rate;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DateValueImpl implements DateValue {

	private Instant date;
	private Double value;
}
