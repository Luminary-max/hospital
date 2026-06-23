package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("drug_price_log")
public class DrugPriceLog {
    @TableId(value = "dpl_id")
    @JsonProperty("dplId")
    private Integer dplId;
    @JsonProperty("drId")
    private String drId;
    @JsonProperty("oldPrice")
    private Double oldPrice;
    @JsonProperty("newPrice")
    private Double newPrice;
    @JsonProperty("changeReason")
    private String changeReason;
    @JsonProperty("operator")
    private String operator;
    @JsonProperty("createTime")
    private String createTime;

    public DrugPriceLog() {}

    public Integer getDplId() { return dplId; }
    public void setDplId(Integer dplId) { this.dplId = dplId; }
    public String getDrId() { return drId; }
    public void setDrId(String drId) { this.drId = drId; }
    public Double getOldPrice() { return oldPrice; }
    public void setOldPrice(Double oldPrice) { this.oldPrice = oldPrice; }
    public Double getNewPrice() { return newPrice; }
    public void setNewPrice(Double newPrice) { this.newPrice = newPrice; }
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
