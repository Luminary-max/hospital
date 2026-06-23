package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("inventory_transaction")
public class InventoryTransaction {
    @TableId(value = "it_id")
    @JsonProperty("itId")
    private Integer itId;
    @JsonProperty("drId")
    private String drId;
    @JsonProperty("dbId")
    private Integer dbId;
    @JsonProperty("itType")
    private String itType;
    @JsonProperty("itQuantity")
    private Integer itQuantity;
    @JsonProperty("itBeforeQuantity")
    private Integer itBeforeQuantity;
    @JsonProperty("itAfterQuantity")
    private Integer itAfterQuantity;
    @JsonProperty("itReference")
    private String itReference;
    @JsonProperty("itOperator")
    private String itOperator;
    @JsonProperty("itNote")
    private String itNote;
    @JsonProperty("itCreateTime")
    private String itCreateTime;

    public Integer getItId() { return itId; }
    public void setItId(Integer itId) { this.itId = itId; }
    public String getDrId() { return drId; }
    public void setDrId(String drId) { this.drId = drId; }
    public Integer getDbId() { return dbId; }
    public void setDbId(Integer dbId) { this.dbId = dbId; }
    public String getItType() { return itType; }
    public void setItType(String itType) { this.itType = itType; }
    public Integer getItQuantity() { return itQuantity; }
    public void setItQuantity(Integer itQuantity) { this.itQuantity = itQuantity; }
    public Integer getItBeforeQuantity() { return itBeforeQuantity; }
    public void setItBeforeQuantity(Integer itBeforeQuantity) { this.itBeforeQuantity = itBeforeQuantity; }
    public Integer getItAfterQuantity() { return itAfterQuantity; }
    public void setItAfterQuantity(Integer itAfterQuantity) { this.itAfterQuantity = itAfterQuantity; }
    public String getItReference() { return itReference; }
    public void setItReference(String itReference) { this.itReference = itReference; }
    public String getItOperator() { return itOperator; }
    public void setItOperator(String itOperator) { this.itOperator = itOperator; }
    public String getItNote() { return itNote; }
    public void setItNote(String itNote) { this.itNote = itNote; }
    public String getItCreateTime() { return itCreateTime; }
    public void setItCreateTime(String itCreateTime) { this.itCreateTime = itCreateTime; }
}

