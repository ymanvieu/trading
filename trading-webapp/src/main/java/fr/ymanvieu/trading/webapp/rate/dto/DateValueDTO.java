package fr.ymanvieu.trading.webapp.rate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import fr.ymanvieu.trading.common.rate.DateValueImpl;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(shape = Shape.ARRAY)
@JsonPropertyOrder({ "date", "value" })
public class DateValueDTO extends DateValueImpl {

}
