package com.pos.leaders.leaderspossystem.Models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pos.leaders.leaderspossystem.Tools.DateConverter;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.Tools.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by KARAM on 19/10/2016.
 */

public class OrderDetails {
	private long orderDetailsId;
	private long productId;
	private double quantity;
	private double userOffer;
	private long orderId;

	private double unitPrice;
	private double paidAmount;
	private double discount;
	private String orderKey;
	private long offerCategory;
	private double paidAmountAfterTax;
	@JsonIgnore
	public double rowDiscount = 0;

	@JsonIgnore
	private Product product;

	@JsonIgnore
	public List<Offer> offerList = new ArrayList<>();

	@JsonIgnore
	public boolean giftProduct = false;
	@JsonIgnore
	public int position = 0;
	@JsonIgnore
	public boolean scannable = true;

	@JsonIgnore
	public boolean offerRule = false;

	@JsonIgnore
	public Offer offer = null;

	@JsonIgnore
	private int objectID = 0;
    private long offerId = 0;
	private long productSerialNumber;
	private String serialNumber;

	public long getProductSerialNumber() {
		return productSerialNumber;
	}

	public void setProductSerialNumber(long productSerialNumber) {
		this.productSerialNumber = productSerialNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@JsonIgnore
	private void initObjectID(){
		int hash=7;
		int prime=31;
		hash = prime * hash + ((int) (productId % 10000));
		hash = (int) (hash * prime + quantity);
		hash = hash * prime + (int) discount;
		hash = hash * prime + (giftProduct ? 37 : 17);
		hash = hash * prime + (offerRule ? 7 : 3);
		hash = hash * prime + (offer == null ? 0 : offer.hashCode());
		hash = hash * prime + (product == null ? 0 : product.hashCode());
		hash = hash * prime + (product == null ? 0 : (product.getProductCode()==null?0:product.getProductCode().hashCode()));
		hash = hash * prime + (int) rowDiscount;
		hash = hash * prime + (int) (new Date().getTime() % 10000);
		hash = prime * hash + ((int) (orderId % 10000));
		hash = hash * prime + hashCode();

		objectID = hash;
	}

	@JsonIgnore
	public int getObjectID(){
		return objectID;
	}

	public long getCustomer_assistance_id() {
		return customer_assistance_id;
	}

	public void setCustomer_assistance_id(long customer_assistance_id) {
		this.customer_assistance_id = customer_assistance_id;
	}

	private long customer_assistance_id;

	//region Constructors
	public OrderDetails(long orderDetailsId, long productId, double quantity, double userOffer, long orderId, long customer_assistance_id) {
		this.orderDetailsId = orderDetailsId;
		this.productId = productId;
		this.quantity = quantity;
		this.userOffer = userOffer;
		this.orderId = orderId;
		this.customer_assistance_id = customer_assistance_id;
		initObjectID();
	}

	public OrderDetails(long orderDetailsId, long productId, double quantity, double userOffer, long orderId, double paidAmount, double original_price, double discount, long customer_assistance_id , String key , long offerId,long productSerialNumber ,double paidAmountAfterTax,String serialNumber) {
		this.orderDetailsId = orderDetailsId;
		this.productId = productId;
		this.quantity = quantity;
		this.userOffer = userOffer;
		this.orderId = orderId;
		this.paidAmount = paidAmount;
		this.unitPrice = original_price;
		this.discount = discount;
		this.customer_assistance_id = customer_assistance_id;
		this.orderKey =key;
        this.offerId=offerId;
		this.productSerialNumber=productSerialNumber;
		this.paidAmountAfterTax=paidAmountAfterTax;
		this.serialNumber=serialNumber;
		initObjectID();
	}

	public OrderDetails(long orderDetailsId, long productId, int quantity, double userOffer, long orderId, Product product, long customer_assistance_id) {
		this.orderDetailsId = orderDetailsId;
		this.productId = productId;
		this.quantity = quantity;
		this.userOffer = userOffer;
		this.orderId = orderId;
		this.product = product;
		this.customer_assistance_id = customer_assistance_id;
		initObjectID();
	}
	public OrderDetails(long productId, int quantity, double userOffer, long orderId, Product product) {
		this.productId = productId;
		this.quantity = quantity;
		this.userOffer = userOffer;
		this.orderId = orderId;
		this.product = product;
		initObjectID();

	}
	public OrderDetails(int quantity, double userOffer, Product product) {
		this.quantity = quantity;
		this.userOffer = userOffer;
		this.product = product;
		this.productId = product.getProductId();
		this.paidAmount = product.getPriceWithTax();
		this.unitPrice = product.getPriceWithTax();
		this.discount = 0;
		initObjectID();
	}

	public OrderDetails(int quantity, double userOffer, Product product, double paidAmount, double original_price, double discount) {
		this.quantity = quantity;
		this.userOffer = userOffer;
		this.product = product;
		this.productId =product.getProductId();
		this.paidAmount = paidAmount;
		this.unitPrice = original_price;
		this.discount = discount;
		initObjectID();
	}

	public OrderDetails(OrderDetails o) {
		this(o.getOrderDetailsId(), o.getProductId(), o.getQuantity(), o.getUserOffer(), o.getOrderId(), o.getPaidAmount(), o.getUnitPrice(), o.getDiscount(),o.getCustomer_assistance_id(),o.getOrderKey(),o.getOfferId(),o.getProductSerialNumber(),o.getPaidAmountAfterTax(),o.getSerialNumber());
		this.product = o.getProduct();
	}

	public OrderDetails newInstance(OrderDetails o) {
		return new OrderDetails(o.getOrderDetailsId(), o.getProductId(), o.getQuantity(), o.getUserOffer(), o.getOrderId(),o.getCustomer_assistance_id());
	}

	public OrderDetails() {
		initObjectID();
	}
	//endregion Constructors

	//region Getters


	public double getPaidAmountAfterTax() {
		return paidAmountAfterTax;
	}

	public void setPaidAmountAfterTax(double paidAmountAfterTax) {
		this.paidAmountAfterTax = paidAmountAfterTax;
	}

	public long getOfferId() {
        return offerId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public long getProductId() {
		return productId;
	}

	public long getOrderDetailsId() {
		return orderDetailsId;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double x) {
		this.quantity = x;
	}

	public double getUserOffer() {
		return userOffer;
	}

	public long getOrderId() {
		return orderId;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

	public void setOrderId(long orderId) {

		this.orderId = orderId;
	}

	public void setOrderDetailsId(long orderDetailsId) {
		this.orderDetailsId = orderDetailsId;
	}

	public Product getProduct() {
		return product;
	}

	@JsonIgnore
	public double getItemTotalPrice() {
		double tempPrice = (quantity * (unitPrice * (1 - (discount / 100))));

		//there is no discount for this row
		if(rowDiscount==0) return tempPrice;
		//calculate the price with row discount after the offer discount
		return (tempPrice - (tempPrice * (rowDiscount / 100)));
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public double getPaidAmount() {
		Log.d("unitPrice",unitPrice+"");
		Log.d("quantity",quantity+"");
		double temp = (unitPrice * (1 - (discount / 100)));
		if (rowDiscount == 0)
			return temp*quantity;
		return (temp - (temp * (rowDiscount / 100)))*this.quantity;
	}
	public double getPaidAmountForWeight() {
		return this.paidAmount;
	}

	public double getDiscount() {
		return discount;
	}

	//endregion Getters


	//region Setters

	public double setCount(double count) {
		if (count >=1)
			this.quantity = count;
		return this.quantity;
	}

	public void setProduct(Product p){
		this.product=p;
	}

	public void setDiscount(double discount){
		this.discount = discount;
	}

	public void setPaidAmount(double paidAmount){
		if(paidAmount==0.0){
			this.paidAmount=0.0;
		}else {
			this.paidAmount = (unitPrice * (discount / 100));
		}
	}



	//endregion Setters

	//region Methods

	public double increaseCount(){
		this.quantity++;
		return this.quantity;
	}

	public double decreaseCount(){
		if(this.quantity >1) {
			this.quantity--;
		}
		return this.quantity;
	}

	public String DKMVDATA(int rowNumber,String companyID,Date date) {
		String s = "320", OP = "+", mOP = "-";
		double totalDiscount = (getItemTotalPrice() * discount / 100);
		double noTax = paidAmount / (1 + (SETTINGS.tax / 100));
		if(product.getDisplayName().length()>29){
			product.setProductCode(product.getDisplayName().substring(0,29));
		}

		return "D110" + String.format(Util.locale, "%09d", rowNumber) + companyID + s + String.format(Util.locale, "%020d", orderId) + String.format(Util.locale, "%04d", orderDetailsId)
				+ s + String.format(Util.locale, "%020d", orderId) + "3" + String.format(Util.locale, "%20s", productId) + String.format(Util.locale, "%30s", "sale")
				+ Util.spaces(50) + String.format(Util.locale, "%30s", product.getSku()) + Util.spaces(20)
				+ "+" + String.format(Util.locale, "%012d", quantity) + String.format(Util.locale, "%04d", (int) ((quantity - Math.floor(quantity) + 0.00001) * 10000))
				+ OP + Util.x12V99(noTax) + mOP + Util.x12V99(totalDiscount) + OP + Util.x12V99((noTax-totalDiscount)* quantity)
				+ String.format(Util.locale, "%02.0f", SETTINGS.tax) + String.format(Util.locale, "%02d", (int) ((SETTINGS.tax - Math.floor(SETTINGS.tax) + 0.001) * 100))
				+ Util.spaces(7) + DateConverter.getYYYYMMDD(date) + String.format(Util.locale, "%07d", orderId) + Util.spaces(7) + Util.spaces(21);
	}

	//endregion Methods


	@Override
	public String toString() {
		return "{"  +
				"\"productId\":" + productId +
				", \"quantity\":" + quantity +
				", \"userOffer\":" + userOffer +
				", \"unitPrice\":" + unitPrice +
				", \"paidAmount\":" + paidAmount +
				", \"discount\":" + discount +
				", \"scannable\":" + "\""+scannable +"\""+
				", \"giftProduct\":" + "\""+giftProduct +"\""+
				", \"position\":" + "\""+position +"\""+
				", \"orderKey\":" + "\""+orderKey +"\""+
				", \"displayName\":" + "\""+product.getDisplayName()+"\""+
				", \"offerCategory\":" + "\""+offerCategory +"\""+
				", \"productSerialNumber\":" + "\""+productSerialNumber +"\""+
				", \"serialNumber\":" + "\""+serialNumber +"\""+
				", \"paid_amount_amount_after_tax\":" + "\""+paidAmountAfterTax +"\""+'}';
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public long getOfferCategory() {
		return offerCategory;
	}

	public void setOfferCategory(long offerCategory) {
		this.offerCategory = offerCategory;
	}
}