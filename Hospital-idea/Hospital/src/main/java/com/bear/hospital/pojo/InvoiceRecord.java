package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("invoice_record")
public class InvoiceRecord {
    @TableId(value = "inv_id")
    @JsonProperty("invId")
    private Integer invId;
    @JsonProperty("invNo")
    private String invNo;
    @JsonProperty("oId")
    private Integer oId;
    @JsonProperty("brId")
    private Integer brId;
    @JsonProperty("invType")
    private String invType;
    @JsonProperty("invAmount")
    private Double invAmount;
    @JsonProperty("invStatus")
    private Integer invStatus;
    @JsonProperty("invOperator")
    private String invOperator;
    @JsonProperty("invCreateTime")
    private String invCreateTime;
    @JsonProperty("invCancelTime")
    private String invCancelTime;
    @JsonProperty("invCancelReason")
    private String invCancelReason;

    public InvoiceRecord() {}

    public InvoiceRecord(Integer oId, String invNo, Double invAmount, String invCreateTime, String invOperator) {
        this.oId = oId; this.invNo = invNo; this.invAmount = invAmount;
        this.invCreateTime = invCreateTime; this.invOperator = invOperator;
        this.invType = "电子"; this.invStatus = 0;
    }

    public Integer getInvId() { return invId; }
    public void setInvId(Integer invId) { this.invId = invId; }
    public String getInvNo() { return invNo; }
    public void setInvNo(String invNo) { this.invNo = invNo; }
    public Integer getOId() { return oId; }
    public void setOId(Integer oId) { this.oId = oId; }
    public Integer getBrId() { return brId; }
    public void setBrId(Integer brId) { this.brId = brId; }
    public String getInvType() { return invType; }
    public void setInvType(String invType) { this.invType = invType; }
    public Double getInvAmount() { return invAmount; }
    public void setInvAmount(Double invAmount) { this.invAmount = invAmount; }
    public Integer getInvStatus() { return invStatus; }
    public void setInvStatus(Integer invStatus) { this.invStatus = invStatus; }
    public String getInvOperator() { return invOperator; }
    public void setInvOperator(String invOperator) { this.invOperator = invOperator; }
    public String getInvCreateTime() { return invCreateTime; }
    public void setInvCreateTime(String invCreateTime) { this.invCreateTime = invCreateTime; }
    public String getInvCancelTime() { return invCancelTime; }
    public void setInvCancelTime(String invCancelTime) { this.invCancelTime = invCancelTime; }
    public String getInvCancelReason() { return invCancelReason; }
    public void setInvCancelReason(String invCancelReason) { this.invCancelReason = invCancelReason; }

    @Override
    public String toString() {
        return "InvoiceRecord{invId=" + invId + ", invNo='" + invNo + "'}";
    }
}
