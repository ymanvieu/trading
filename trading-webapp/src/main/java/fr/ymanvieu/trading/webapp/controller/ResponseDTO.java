package fr.ymanvieu.trading.webapp.controller;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseDTO {
	private String message;
	private Object[] args;
}
