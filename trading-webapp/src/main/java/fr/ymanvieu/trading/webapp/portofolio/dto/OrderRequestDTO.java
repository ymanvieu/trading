package fr.ymanvieu.trading.webapp.portofolio.dto;

import fr.ymanvieu.trading.webapp.portofolio.controller.OrderType;
import lombok.Value;

@Value
public class OrderRequestDTO {

	OrderType type;
	String code;
	double quantity;
}
