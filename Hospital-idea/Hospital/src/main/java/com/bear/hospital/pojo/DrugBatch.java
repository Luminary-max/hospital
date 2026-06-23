package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("drug_batch")
public class DrugBatch {
    @TableId(value = "db_id")
    @JsonProperty("dbId")
    private Integer dbId;
    @JsonProperty("drId")
    private String drId;
    @JsonProperty("dbBatchNo")
    private String dbBatchNo;
    @JsonProperty("dbExpireDate")
    private String dbExpireDate;
    @JsonProperty("dbQuantity")
    private Integer dbQuantity;
    @JsonProperty("dbPurchasePrice")
    private Double dbPurchasePrice;
    @JsonProperty("dbSupplier")
    private String dbSupplier;
    @JsonProperty("dbCreateTime")
    private String dbCreateTime;

    @TableField(exist = false)
    @JsonProperty("dbDrugName")
    private String dbDrugName;

    public DrugBatch() {}

    public Integer getDbId() { return dbId; }
    public void setDbId(Integer dbId) { this.dbId = dbId; }
    public String getDrId() { return drId; }
    public void setDrId(String drId) { this.drId = drId; }
    public String getDbBatchNo() { return dbBatchNo; }
    public void setDbBatchNo(String dbBatchNo) { this.dbBatchNo = dbBatchNo; }
    public String getDbExpireDate() { return dbExpireDate; }
    public void setDbExpireDate(String dbExpireDate) { this.dbExpireDate = dbExpireDate; }
    public Integer getDbQuantity() { return dbQuantity; }
    public void setDbQuantity(Integer dbQuantity) { this.dbQuantity = dbQuantity; }
    public Double getDbPurchasePrice() { return dbPurchasePrice; }
    public void setDbPurchasePrice(Double dbPurchasePrice) { this.dbPurchasePrice = dbPurchasePrice; }
    public String getDbSupplier() { return dbSupplier; }
    public void setDbSupplier(String dbSupplier) { this.dbSupplier = dbSupplier; }
    public String getDbCreateTime() { return dbCreateTime; }
    public void setDbCreateTime(String dbCreateTime) { this.dbCreateTime = dbCreateTime; }
    public String getDbDrugName() { return dbDrugName; }
    public void setDbDrugName(String dbDrugName) { this.dbDrugName = dbDrugName; }
}
