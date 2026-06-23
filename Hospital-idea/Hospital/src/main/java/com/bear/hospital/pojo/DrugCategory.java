package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("drug_category")
public class DrugCategory {
    @TableId(value = "dc_id")
    @JsonProperty("dcId")
    private Integer dcId;
    @JsonProperty("dcName")
    private String dcName;
    @JsonProperty("dcParentId")
    private Integer dcParentId;
    @JsonProperty("dcCode")
    private String dcCode;
    @JsonProperty("dcNote")
    private String dcNote;
    @JsonProperty("dcSort")
    private Integer dcSort;

    public DrugCategory() {}

    public Integer getDcId() { return dcId; }
    public void setDcId(Integer dcId) { this.dcId = dcId; }
    public String getDcName() { return dcName; }
    public void setDcName(String dcName) { this.dcName = dcName; }
    public Integer getDcParentId() { return dcParentId; }
    public void setDcParentId(Integer dcParentId) { this.dcParentId = dcParentId; }
    public String getDcCode() { return dcCode; }
    public void setDcCode(String dcCode) { this.dcCode = dcCode; }
    public String getDcNote() { return dcNote; }
    public void setDcNote(String dcNote) { this.dcNote = dcNote; }
    public Integer getDcSort() { return dcSort; }
    public void setDcSort(Integer dcSort) { this.dcSort = dcSort; }
}
