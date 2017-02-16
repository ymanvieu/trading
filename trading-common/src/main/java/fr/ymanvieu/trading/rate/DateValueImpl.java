package fr.ymanvieu.trading.rate;

import java.util.Date;

public class DateValueImpl implements DateValue {

	private Date date;
	private Double value;

	public DateValueImpl() {
	}
	
	public DateValueImpl(Date date, Double value) {
		this.date = date;
		this.value = value;
	}

	@Override
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
}