package fr.ymanvieu.trading.rate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import fr.ymanvieu.trading.rate.DateValueImpl;

@JsonFormat(shape = Shape.ARRAY)
@JsonPropertyOrder({ "date", "value" })
public class DateValueDTO extends DateValueImpl {

}