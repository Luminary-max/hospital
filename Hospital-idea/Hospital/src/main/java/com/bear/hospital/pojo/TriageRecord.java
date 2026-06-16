package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("triage_record")
public class TriageRecord {
    @TableId(value = "t_id")
    @JsonProperty("tId")
    private Integer tId;
    @JsonProperty("oId")
    private Integer oId;
    @JsonProperty("pId")
    private Integer pId;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("tLevel")
    private Integer tLevel;
    @JsonProperty("tStatus")
    private Integer tStatus;
    @JsonProperty("tNote")
    private String tNote;
    @JsonProperty("tCreateTime")
    private String tCreateTime;

    public TriageRecord() {}

    public Integer getTId() { return tId; }
    public void setTId(Integer tId) { this.tId = tId; }
    public Integer getOId() { return oId; }
    public void setOId(Integer oId) { this.oId = oId; }
    public Integer getPId() { return pId; }
    public void setPId(Integer pId) { this.pId = pId; }
    public String getDId() { return dId; }
    public void setDId(String dId) { this.dId = dId; }
    public Integer getTLevel() { return tLevel; }
    public void setTLevel(Integer tLevel) { this.tLevel = tLevel; }
    public Integer getTStatus() { return tStatus; }
    public void setTStatus(Integer tStatus) { this.tStatus = tStatus; }
    public String getTNote() { return tNote; }
    public void setTNote(String tNote) { this.tNote = tNote; }
    public String getTCreateTime() { return tCreateTime; }
    public void setTCreateTime(String tCreateTime) { this.tCreateTime = tCreateTime; }
}
