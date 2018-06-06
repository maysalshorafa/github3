package com.pos.leaders.leaderspossystem.Models;

import com.pos.leaders.leaderspossystem.Tools.DateConverter;
import com.pos.leaders.leaderspossystem.Tools.Util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by KARAM on 02/11/2016.
 */

public class Check {
	private long checkId;
	private int checkNum;
	private int bankNum;
	private int branchNum;
	private int accountNum;
	private double amount;
	private Timestamp createdAt;
	private boolean deleted;
	private long orderId;

	// region Constructors



	public Check(long checkId, int checkNum, int bankNum, int branchNum, int accountNum, double amount, Timestamp date, boolean deleted, long orderId) {
		this.accountNum = accountNum;
		this.amount = amount;
		this.bankNum = bankNum;
		this.branchNum = branchNum;
		this.checkNum = checkNum;
		this.createdAt = date;
		this.checkId = checkId;
		this.deleted = deleted;
		this.orderId = orderId;
	}

	public Check(int checkNum, int bankNum, int branchNum, int accountNum, double amount, Timestamp date, boolean deleted) {
		this.accountNum = accountNum;
		this.amount = amount;
		this.bankNum = bankNum;
		this.branchNum = branchNum;
		this.checkNum = checkNum;
		this.createdAt = date;
		this.deleted = deleted;
	}

	public Check(){

	}

	public Check(Check check){
		this(check.getCheckNum(),check.getBankNum(),check.getBranchNum(),check.getAccountNum(),check.getAmount(),check.getDate(),false);
	}

	//endregion

	//region Getters

	public int getAccountNum() {
		return accountNum;
	}

	public double getAmount() {
		return amount;
	}

	public int getBankNum() {
		return bankNum;
	}

	public int getBranchNum() {
		return branchNum;
	}

	public int getCheckNum() {
		return checkNum;
	}

	public Timestamp getDate() {
		return createdAt;
	}

	public long getCheckId() {
		return checkId;
	}

	public long getOrderId() {
		return orderId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	//endregion

	//region Setters

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public void setAccountNum(int accountNum) {
		this.accountNum = accountNum;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setBankNum(int bankNum) {
		this.bankNum = bankNum;
	}

	public void setBranchNum(int branchNum) {
		this.branchNum = branchNum;
	}

	public void setCheckNum(int checkNum) {
		this.checkNum = checkNum;
	}

	public void setDate(Timestamp date) {
		this.createdAt = date;
	}

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    //endregion

	//region Methods

	@Override
	public String toString() {
		return "Check{" +
				"accountNum=" + accountNum +
				", accountingId=" + checkId +
				", checkNum=" + checkNum +
				", bankNum=" + bankNum +
				", branchNum=" + branchNum +
				", amount=" + amount +
				", date=" + createdAt +
				", orderId=" + orderId +
				'}';
	}

    public String BKMVDATA(int rowNumber, String companyID, Date date,long parentNumber) {

		String s = "320",OP="+",paymentType="2",cardType="0";

		if(amount<0) {
            s = "330";
            OP = "-";
        }

        return ("D120" + String.format(Util.locale, "%09d", rowNumber) + companyID + s + String.format(Util.locale, "%020d", orderId) + String.format(Util.locale, "%04d", orderId) +
                paymentType + String.format(Util.locale, "%010d", bankNum) + String.format(Util.locale, "%010d", branchNum) + String.format(Util.locale, "%015d", accountNum) +
                String.format(Util.locale, "%010d", checkNum) + DateConverter.getYYYYMMDD(new Date(String.valueOf(this.createdAt))) + OP + Util.x12V99(amount) + cardType + Util.spaces(20) +
                "0" + Util.spaces(7) + DateConverter.getYYYYMMDD(date) + String.format(Util.locale, "%07d", parentNumber) + Util.spaces(60));
    }

	//endregion

}
