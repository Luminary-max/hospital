package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("order_check")
public class OrderCheck {
    @TableId(value = "oc_id", type = IdType.AUTO)
    @JsonProperty("ocId")
    private Integer ocId;
    @JsonProperty("emrId")
    private Integer emrId;
    @JsonProperty("chId")
    private String chId;
    @JsonProperty("chName")
    private String chName;
    @JsonProperty("chPrice")
    private Double chPrice;
    @JsonProperty("ocStatus")
    private Integer ocStatus;       // 0=未缴费 1=待检查 2=已完成 3=异常
    @JsonProperty("ocResult")
    private String ocResult;
    @JsonProperty("ocAttachment")
    private String ocAttachment;
    @JsonProperty("ocResultTime")
    private String ocResultTime;
    @JsonProperty("ocOperator")
    private String ocOperator;
    @JsonProperty("ocCreateTime")
    private String ocCreateTime;
    @JsonProperty("ocNote")
    private String ocNote;

    @TableField(exist = false)
    @JsonProperty("pName")
    private String pName;
    @TableField(exist = false)
    @JsonProperty("dName")
    private String dName;

    public OrderCheck() {}

    public Integer getOcId() { return ocId; }
    public void setOcId(Integer ocId) { this.ocId = ocId; }
    public Integer getEmrId() { return emrId; }
    public void setEmrId(Integer emrId) { this.emrId = emrId; }
    public String getChId() { return chId; }
    public void setChId(String chId) { this.chId = chId; }
    public String getChName() { return chName; }
    public void setChName(String chName) { this.chName = chName; }
    public Double getChPrice() { return chPrice; }
    public void setChPrice(Double chPrice) { this.chPrice = chPrice; }
    public Integer getOcStatus() { return ocStatus; }
    public void setOcStatus(Integer ocStatus) { this.ocStatus = ocStatus; }
    public String getOcResult() { return ocResult; }
    public void setOcResult(String ocResult) { this.ocResult = ocResult; }
    public String getOcAttachment() { return ocAttachment; }
    public void setOcAttachment(String ocAttachment) { this.ocAttachment = ocAttachment; }
    public String getOcResultTime() { return ocResultTime; }
    public void setOcResultTime(String ocResultTime) { this.ocResultTime = ocResultTime; }
    public String getOcOperator() { return ocOperator; }
    public void setOcOperator(String ocOperator) { this.ocOperator = ocOperator; }
    public String getOcCreateTime() { return ocCreateTime; }
    public void setOcCreateTime(String ocCreateTime) { this.ocCreateTime = ocCreateTime; }
    public String getOcNote() { return ocNote; }
    public void setOcNote(String ocNote) { this.ocNote = ocNote; }
    public String getPName() { return pName; }
    public void setPName(String pName) { this.pName = pName; }
    public String getDName() { return dName; }
    public void setDName(String dName) { this.dName = dName; }
}
