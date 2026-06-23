package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("refund_request")
public class RefundRequest {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;

    @TableId(value = "rf_id")
    @JsonProperty("rfId")
    private Integer rfId;
    @JsonProperty("oId")
    private Integer oId;
    @JsonProperty("brId")
    private Integer brId;
    @JsonProperty("rfAmount")
    private Double rfAmount;
    @JsonProperty("rfReason")
    private String rfReason;
    @JsonProperty("rfStatus")
    private Integer rfStatus;
    @JsonProperty("rfRequester")
    private String rfRequester;
    @JsonProperty("rfApprover")
    private String rfApprover;
    @JsonProperty("rfApproveTime")
    private String rfApproveTime;
    @JsonProperty("rfCreateTime")
    private String rfCreateTime;
    @JsonProperty("rfNote")
    private String rfNote;

    public RefundRequest() {}

    public Integer getRfId() { return rfId; }
    public void setRfId(Integer rfId) { this.rfId = rfId; }
    public Integer getOId() { return oId; }
    public void setOId(Integer oId) { this.oId = oId; }
    public Integer getBrId() { return brId; }
    public void setBrId(Integer brId) { this.brId = brId; }
    public Double getRfAmount() { return rfAmount; }
    public void setRfAmount(Double rfAmount) { this.rfAmount = rfAmount; }
    public String getRfReason() { return rfReason; }
    public void setRfReason(String rfReason) { this.rfReason = rfReason; }
    public Integer getRfStatus() { return rfStatus; }
    public void setRfStatus(Integer rfStatus) { this.rfStatus = rfStatus; }
    public String getRfRequester() { return rfRequester; }
    public void setRfRequester(String rfRequester) { this.rfRequester = rfRequester; }
    public String getRfApprover() { return rfApprover; }
    public void setRfApprover(String rfApprover) { this.rfApprover = rfApprover; }
    public String getRfApproveTime() { return rfApproveTime; }
    public void setRfApproveTime(String rfApproveTime) { this.rfApproveTime = rfApproveTime; }
    public String getRfCreateTime() { return rfCreateTime; }
    public void setRfCreateTime(String rfCreateTime) { this.rfCreateTime = rfCreateTime; }
    public String getRfNote() { return rfNote; }
    public void setRfNote(String rfNote) { this.rfNote = rfNote; }
}
