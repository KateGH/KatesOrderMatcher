package com.google.gwt.kate.ordermatcher.client;

public class OrderPrice
{
	public String type;
	private Integer volume;
	private Integer price;	
	
	public OrderPrice() {
	}
	
	public OrderPrice(String type, Integer volume, Integer price) {
	    this.type = type;
	    this.volume = volume;
	    this.price = price;
	}
	
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Integer getVolume()
	{
		return volume;
	}

	public void setVolume(Integer volume)
	{
		this.volume = volume;
	}

	public Integer getPrice()
	{
		return price;
	}

	public void setPrice(Integer price)
	{
		this.price = price;
	}

}
