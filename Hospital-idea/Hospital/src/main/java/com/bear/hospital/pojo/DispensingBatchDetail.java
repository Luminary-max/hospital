package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("dispensing_batch_detail")
public class DispensingBatchDetail {
    @TableId(value = "dbd_id")
    private Integer dbdId;
    private String pdReference;
    private String drId;
    private Integer dbId;
    private Integer dbdQuantity;
    private Integer dbdReturned;
    private String dbdCreateTime;

    public Integer getDbdId() { return dbdId; }
    public void setDbdId(Integer dbdId) { this.dbdId = dbdId; }
    public String getPdReference() { return pdReference; }
    public void setPdReference(String pdReference) { this.pdReference = pdReference; }
    public String getDrId() { return drId; }
    public void setDrId(String drId) { this.drId = drId; }
    public Integer getDbId() { return dbId; }
    public void setDbId(Integer dbId) { this.dbId = dbId; }
    public Integer getDbdQuantity() { return dbdQuantity; }
    public void setDbdQuantity(Integer dbdQuantity) { this.dbdQuantity = dbdQuantity; }
    public Integer getDbdReturned() { return dbdReturned; }
    public void setDbdReturned(Integer dbdReturned) { this.dbdReturned = dbdReturned; }
    public String getDbdCreateTime() { return dbdCreateTime; }
    public void setDbdCreateTime(String dbdCreateTime) { this.dbdCreateTime = dbdCreateTime; }
}

