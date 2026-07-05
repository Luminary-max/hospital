package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("orders")
public class Orders {
    // Order state constants for visit flow
    public static final int STATE_REGISTERED = 0;      // 已挂号
    public static final int STATE_TRIAGED = 1;          // 已分诊
    public static final int STATE_IN_CONSULTATION = 2;  // 就诊中
    public static final int STATE_ORDERED = 3;          // 已开处方/检查
    public static final int STATE_PENDING_PAYMENT = 4;  // 待缴费
    public static final int STATE_PAID = 5;             // 已缴费
    public static final int STATE_DISPENSED = 6;        // 已发药/检查完成
    public static final int STATE_COMPLETED = 7;        // 就诊完成

    @TableId(value = "o_id", type = IdType.AUTO)
    @JsonProperty("oId")
    private int oId;
    @JsonProperty("pId")
    private int pId;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("oRecord")
    private String oRecord;
    @JsonProperty("oStart")
    private String oStart;
    @JsonProperty("oEnd")
    private String oEnd;
    @JsonProperty("oState")
    private Integer oState;
    @JsonProperty("oDrug")
    private String oDrug;
    @JsonProperty("oCheck")
    private String oCheck;
    @JsonProperty("oTotalPrice")
    private Double oTotalPrice;
    @JsonProperty("oPriceState")
    private Integer oPriceState;
    @JsonProperty("countGender")
    @TableField(exist = false)
    private Integer countGender;
    @JsonProperty("oAdvice")
    private String oAdvice;
    @JsonProperty("oQueueNumber")
    private String oQueueNumber;
    @JsonProperty("oTriage")
    @Deprecated
    private String oTriage; // 已废弃，请使用 oRegType（挂号类型）

    // 新增字段
    @JsonProperty("oRegistrationFee")
    private Double oRegistrationFee;
    @JsonProperty("oPaymentMethod")
    private String oPaymentMethod;
    @JsonProperty("oInvoiceNo")
    private String oInvoiceNo;
    @JsonProperty("oInsuranceCovered")
    private Double oInsuranceCovered;
    @JsonProperty("oSelfPay")
    private Double oSelfPay;
    @JsonProperty("oRegType")
    private String oRegType;
    @JsonProperty("oCancelReason")
    private String oCancelReason;
    @JsonProperty("oMissed")
    private Integer oMissed;
    @TableField(exist = false)
    @JsonProperty("tLevel")
    private Integer tLevel;

    //多表查询用
    @TableField(exist = false)
    private Doctor doctor;
    @TableField(exist = false)
    private Patient patient;
    @TableField(exist = false)
    private Integer countSection;
    @JsonProperty("dName")
    @TableField(exist = false)
    private String dName;
    @JsonProperty("pName")
    @TableField(exist = false)
    private String pName;

    public Orders() {}

    public Orders(int oId, int pId, String dId, String oRecord, String oStart, String oEnd, Integer oState, String oDrug, String oCheck, Double oTotalPrice, Integer oPriceState, Integer countGender, String oAdvice, String oQueueNumber, String oTriage, Double oRegistrationFee, String oPaymentMethod, String oInvoiceNo, Double oInsuranceCovered, Double oSelfPay, Doctor doctor, Patient patient, Integer countSection, String dName, String pName) {
        this.oId = oId;
        this.pId = pId;
        this.dId = dId;
        this.oRecord = oRecord;
        this.oStart = oStart;
        this.oEnd = oEnd;
        this.oState = oState;
        this.oDrug = oDrug;
        this.oCheck = oCheck;
        this.oTotalPrice = oTotalPrice;
        this.oPriceState = oPriceState;
        this.countGender = countGender;
        this.oAdvice = oAdvice;
        this.oQueueNumber = oQueueNumber;
        this.oTriage = oTriage;
        this.oRegistrationFee = oRegistrationFee;
        this.oPaymentMethod = oPaymentMethod;
        this.oInvoiceNo = oInvoiceNo;
        this.oInsuranceCovered = oInsuranceCovered;
        this.oSelfPay = oSelfPay;
        this.doctor = doctor;
        this.patient = patient;
        this.countSection = countSection;
        this.dName = dName;
        this.pName = pName;
    }

    public int getOId() { return oId; }
    public void setOId(int oId) { this.oId = oId; }
    public int getPId() { return pId; }
    public void setPId(int pId) { this.pId = pId; }
    public String getdId() { return dId; }
    public void setdId(String dId) { this.dId = dId; }
    public String getORecord() { return oRecord; }
    public void setORecord(String oRecord) { this.oRecord = oRecord; }
    public String getOStart() { return oStart; }
    public void setOStart(String oStart) { this.oStart = oStart; }
    public String getOEnd() { return oEnd; }
    public void setOEnd(String oEnd) { this.oEnd = oEnd; }
    public Integer getOState() { return oState; }
    public void setOState(Integer oState) { this.oState = oState; }
    public String getODrug() { return oDrug; }
    public void setODrug(String oDrug) { this.oDrug = oDrug; }
    public String getOCheck() { return oCheck; }
    public void setOCheck(String oCheck) { this.oCheck = oCheck; }
    public Double getOTotalPrice() { return oTotalPrice; }
    public void setOTotalPrice(Double oTotalPrice) { this.oTotalPrice = oTotalPrice; }
    public Integer getOPriceState() { return oPriceState; }
    public void setOPriceState(Integer oPriceState) { this.oPriceState = oPriceState; }
    public Integer getCountGender() { return countGender; }
    public void setCountGender(Integer countGender) { this.countGender = countGender; }
    public String getOAdvice() { return oAdvice; }
    public void setOAdvice(String oAdvice) { this.oAdvice = oAdvice; }
    public String getOQueueNumber() { return oQueueNumber; }
    public void setOQueueNumber(String oQueueNumber) { this.oQueueNumber = oQueueNumber; }
    public String getOTriage() { return oTriage; }
    @Deprecated
    public void setOTriage(String oTriage) { this.oTriage = oTriage; }

    public Double getORegistrationFee() { return oRegistrationFee; }
    public void setORegistrationFee(Double oRegistrationFee) { this.oRegistrationFee = oRegistrationFee; }
    public String getOPaymentMethod() { return oPaymentMethod; }
    public void setOPaymentMethod(String oPaymentMethod) { this.oPaymentMethod = oPaymentMethod; }
    public String getOInvoiceNo() { return oInvoiceNo; }
    public void setOInvoiceNo(String oInvoiceNo) { this.oInvoiceNo = oInvoiceNo; }
    public Double getOInsuranceCovered() { return oInsuranceCovered; }
    public void setOInsuranceCovered(Double oInsuranceCovered) { this.oInsuranceCovered = oInsuranceCovered; }
    public Double getOSelfPay() { return oSelfPay; }
    public void setOSelfPay(Double oSelfPay) { this.oSelfPay = oSelfPay; }

    public String getORegType() { return oRegType; }
    public void setORegType(String oRegType) { this.oRegType = oRegType; }
    public String getOCancelReason() { return oCancelReason; }
    public void setOCancelReason(String oCancelReason) { this.oCancelReason = oCancelReason; }
    public Integer getOMissed() { return oMissed; }
    public void setOMissed(Integer oMissed) { this.oMissed = oMissed; }
    public Integer getTLevel() { return tLevel; }
    public void setTLevel(Integer tLevel) { this.tLevel = tLevel; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Integer getCountSection() { return countSection; }
    public void setCountSection(Integer countSection) { this.countSection = countSection; }
    public String getdName() { return dName; }
    public void setdName(String dName) { this.dName = dName; }
    public String getPName() { return pName; }
    public void setPName(String pName) { this.pName = pName; }

    @Override
    public String toString() {
        return "Orders{" +
                "oId=" + oId +
                ", pId=" + pId +
                ", dId=" + dId +
                ", oRecord='" + oRecord + '\'' +
                ", oStart='" + oStart + '\'' +
                ", oEnd='" + oEnd + '\'' +
                ", oState=" + oState +
                ", oTotalPrice=" + oTotalPrice +
                ", oPriceState=" + oPriceState +
                ", oAdvice='" + oAdvice + '\'' +
                ", oRegistrationFee=" + oRegistrationFee +
                ", oPaymentMethod='" + oPaymentMethod + '\'' +
                ", oInvoiceNo='" + oInvoiceNo + '\'' +
                ", oInsuranceCovered=" + oInsuranceCovered +
                ", oSelfPay=" + oSelfPay +
                ", oRegType='" + oRegType + '\'' +
                ", oCancelReason='" + oCancelReason + '\'' +
                ", oMissed=" + oMissed +
                '}';
    }
}
