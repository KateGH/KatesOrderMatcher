package com.google.gwt.kate.ordermatcher.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KatesOrderMatcher implements EntryPoint
{	
	private VerticalPanel mainPanel = new VerticalPanel();

	private HorizontalPanel tablePanel = new HorizontalPanel();
	private FlexTable ordersFlexTable1 = new FlexTable();
	private FlexTable ordersFlexTable2 = new FlexTable();
	
	private Label orderMatchedLabel = new Label("Order Matched!");
	private Label tradeLabel = new Label();	
	private VerticalPanel matchPanel = new VerticalPanel();
	
	private HorizontalPanel addPanel1 = new HorizontalPanel();
	private HorizontalPanel addPanel2 = new HorizontalPanel();
	private HorizontalPanel addPanel3 = new HorizontalPanel();
	private VerticalPanel addPanel = new VerticalPanel();

	private TextBox newVolumeTextBox = new TextBox();
	private TextBox newPriceTextBox = new TextBox();
	private Button buyOrderButton = new Button("BUY");
	private Button sellOrderButton = new Button("SELL");
	
	private ArrayList<OrderPrice> buyOrders = new ArrayList<>();
	private ArrayList<OrderPrice> sellOrders = new ArrayList<>();
	
	private boolean buy;

	public void onModuleLoad()
	{
		// Create table for order data.
		ordersFlexTable1.setText(0, 0, "BUY Orders");
		ordersFlexTable2.setText(0, 0, "SELL Orders");

		// Add styles to elements in the stock list table
		ordersFlexTable1.setCellPadding(12);
		ordersFlexTable1.getRowFormatter().addStyleName(0, "orderListHeader");
		ordersFlexTable1.addStyleName("orderList");

		ordersFlexTable2.setCellPadding(12);
		ordersFlexTable2.getRowFormatter().addStyleName(0, "orderListHeader");
		ordersFlexTable2.addStyleName("orderList");

		// Assemble Add Table panel.
		tablePanel.add(ordersFlexTable1);
		tablePanel.add(ordersFlexTable2);

		// Assemble Trade Match panel.
		matchPanel.add(orderMatchedLabel);
		matchPanel.add(tradeLabel);
		matchPanel.addStyleName("tradeMatched");
		
		// Assemble Add Order panel.
		addPanel1.add(new HTML("<b>Volume&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Price</b>"));
		addPanel2.add(newVolumeTextBox);
		addPanel2.add(newPriceTextBox);
		addPanel3.add(buyOrderButton);
		addPanel3.add(sellOrderButton);
		addPanel.add(addPanel1);
		addPanel.add(addPanel2);
		addPanel.add(addPanel3);
		addPanel.addStyleName("addPanel");

		// Assemble Main panel.
		mainPanel.add(tablePanel);
		mainPanel.add(matchPanel);
		mainPanel.add(addPanel);
		mainPanel.addStyleName("mainPanel");

		// Associate the Main panel with the HTML host page.
		RootPanel.get("orderList").add(mainPanel);

		// Move cursor focus to the input volumen box.
		newVolumeTextBox.setFocus(true);

		// Listen for mouse events on the BUY button.
		buyOrderButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				buy = true;
				addOrder();
			}
		});

		// Listen for mouse events on the SELL button.
		sellOrderButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				buy = false;
				addOrder();
			}
		});
	}

	private void addOrder()
	{
		String volumeStr = newVolumeTextBox.getText().toUpperCase().trim();
		String priceStr = newPriceTextBox.getText().toUpperCase().trim();
		
		Integer volume = Integer.parseInt(volumeStr);
		Integer price = Integer.parseInt(priceStr);

		newVolumeTextBox.setFocus(true);

		// Volume & Price must be positive (>0) integers
		if (!volumeStr.matches("^[1-9]\\d*$") || !priceStr.matches("^[1-9]\\d*$"))
		{
			Window.alert("Invalid value, Please enter positive numbers.");
			newVolumeTextBox.selectAll();
			return;
		}

		newVolumeTextBox.setText("");
		newPriceTextBox.setText("");

		// Add the order to the table
		if (buy)
		{
			OrderPrice buyOrder = new OrderPrice("BUY", volume, price);
			buyOrders.add(buyOrder);
			
			refreshOrderList(buyOrder, sellOrders);
			
		} else if (!buy)
		{
			OrderPrice sellOrder = new OrderPrice("SELL", volume, price);
			sellOrders.add(sellOrder);
			
			refreshOrderList(sellOrder, buyOrders);
		}
	}

	private void refreshOrderList(OrderPrice orderPrice, ArrayList<OrderPrice> orders)
	{
		Integer tradeVolume;
		Integer tradePrice;
		
		Iterator<OrderPrice> ordersItr = orders.iterator(); 
		
		try {
			while(ordersItr.hasNext()) 
			{
				OrderPrice orderLoop = ordersItr.next();
				
				if(buy) {
					//Trade Matched!! buy.Price > sell.Price
					if(orderPrice.getPrice() >= orderLoop.getPrice()) {
						if(orderPrice.getVolume() <= orderLoop.getVolume()) {
							orderLoop.setVolume(orderLoop.getVolume()-orderPrice.getVolume());
							tradeVolume = orderPrice.getVolume();
							orderPrice.setVolume(0);
							tradePrice = orderLoop.getPrice();
						}else {
							orderPrice.setVolume(orderPrice.getVolume()-orderLoop.getVolume());
							tradeVolume = orderLoop.getVolume();
							orderLoop.setVolume(0);
							tradePrice = orderLoop.getPrice();
						}
					}else {
						//NO Trade Matched
						tradeVolume = 0;
						tradePrice = 0;
					}
				}
				
				else if(!buy) {
					//Trade Matched!! sell.Price < buy.Price
					if(orderPrice.getPrice() <= orderLoop.getPrice()) {
						if(orderPrice.getVolume() <= orderLoop.getVolume()) {
							orderLoop.setVolume(orderLoop.getVolume()-orderPrice.getVolume());
							tradeVolume = orderPrice.getVolume();
							orderPrice.setVolume(0);
							tradePrice = orderLoop.getPrice();
						}else {
							orderPrice.setVolume(orderPrice.getVolume()-orderLoop.getVolume());
							tradeVolume = orderLoop.getVolume();
							orderLoop.setVolume(0);
							tradePrice = orderLoop.getPrice();
						}
					}
					//NO Trade Matched
					tradeVolume = 0;
					tradePrice = 0;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		updateTable();
	}
	
	private void updateTable()
	{
			for (int i=0;i<sellOrders.size();i++){
				ordersFlexTable2.setText(i+1, 0, "SELL " + sellOrders.get(i).getVolume() + "@" + sellOrders.get(i).getPrice());
				if(sellOrders.get(i).getVolume()==0) {
					sellOrders.remove(i);
					ordersFlexTable2.removeRow(i+1);
				}
			}
			for (int i=0;i<buyOrders.size();i++){
				ordersFlexTable1.setText(i+1, 0, "BUY " + buyOrders.get(i).getVolume() + "@" + buyOrders.get(i).getPrice());
				if(buyOrders.get(i).getVolume()==0) {
					buyOrders.remove(i);
					ordersFlexTable1.removeRow(i+1);
				}
			}
	}
	
	private void updateObject(OrderPrice orderPrice)
	{
		//TODO: show Matched Trade
		orderMatchedLabel.setStyleName("changeColor");
		tradeLabel.setStyleName("changeColor");
	}
}
