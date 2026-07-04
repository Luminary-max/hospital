package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("billing_record")
public class BillingRecord {
    @TableId(value = "br_id")
    @JsonProperty("brId")
    private Integer brId;
    @JsonProperty("oId")
    private Integer oId;
    @JsonProperty("emrId")
    private Integer emrId;
    @JsonProperty("ocId")
    private Integer ocId;
    @JsonProperty("brType")
    private String brType;
    @JsonProperty("brAmount")
    private Double brAmount;
    @JsonProperty("brPaymentMethod")
    private String brPaymentMethod;
    @JsonProperty("brInvoiceNo")
    private String brInvoiceNo;
    @JsonProperty("brPayTime")
    private String brPayTime;
    @JsonProperty("brOperator")
    private String brOperator;

    public BillingRecord() {}

    public BillingRecord(Integer oId, Integer emrId, Integer ocId, String brType, Double brAmount, String brPaymentMethod, String brInvoiceNo, String brPayTime, String brOperator) {
        this.oId = oId;
        this.emrId = emrId;
        this.ocId = ocId;
        this.brType = brType;
        this.brAmount = brAmount;
        this.brPaymentMethod = brPaymentMethod;
        this.brInvoiceNo = brInvoiceNo;
        this.brPayTime = brPayTime;
        this.brOperator = brOperator;
    }

    public Integer getBrId() { return brId; }
    public void setBrId(Integer brId) { this.brId = brId; }
    public Integer getOId() { return oId; }
    public void setOId(Integer oId) { this.oId = oId; }
    public Integer getEmrId() { return emrId; }
    public void setEmrId(Integer emrId) { this.emrId = emrId; }
    public Integer getOcId() { return ocId; }
    public void setOcId(Integer ocId) { this.ocId = ocId; }
    public String getBrType() { return brType; }
    public void setBrType(String brType) { this.brType = brType; }
    public Double getBrAmount() { return brAmount; }
    public void setBrAmount(Double brAmount) { this.brAmount = brAmount; }
    public String getBrPaymentMethod() { return brPaymentMethod; }
    public void setBrPaymentMethod(String brPaymentMethod) { this.brPaymentMethod = brPaymentMethod; }
    public String getBrInvoiceNo() { return brInvoiceNo; }
    public void setBrInvoiceNo(String brInvoiceNo) { this.brInvoiceNo = brInvoiceNo; }
    public String getBrPayTime() { return brPayTime; }
    public void setBrPayTime(String brPayTime) { this.brPayTime = brPayTime; }
    public String getBrOperator() { return brOperator; }
    public void setBrOperator(String brOperator) { this.brOperator = brOperator; }

    @Override
    public String toString() {
        return "BillingRecord{" +
                "brId=" + brId +
                ", oId=" + oId +
                ", emrId=" + emrId +
                ", ocId=" + ocId +
                ", brType='" + brType + '\'' +
                ", brAmount=" + brAmount +
                ", brPaymentMethod='" + brPaymentMethod + '\'' +
                ", brInvoiceNo='" + brInvoiceNo + '\'' +
                ", brPayTime='" + brPayTime + '\'' +
                ", brOperator='" + brOperator + '\'' +
                '}';
    }
}
