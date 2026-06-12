package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("bed")
public class Bed {
    @TableId(value = "b_id")
    @JsonProperty("bId")
    private String bId;
    @JsonProperty("pId")
    private int pId;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("bState")
    private Integer bState;
    @JsonProperty("bStart")
    private String bStart;
    @JsonProperty("bReason")
    private String bReason;
    @Version
    private Integer version;

    // 门诊观察相关字段
    @JsonProperty("bType")
    private Integer bType;
    @JsonProperty("bObsStart")
    private String bObsStart;
    @JsonProperty("bObsEnd")
    private String bObsEnd;
    @JsonProperty("bObsNote")
    private String bObsNote;

    @TableField(exist = false)
    @JsonProperty("pName")
    private String pName;
    @TableField(exist = false)
    @JsonProperty("dName")
    private String dName;

    public Bed() {}

    public Bed(String bId, int pId, String dId, Integer bState, String bStart, String bReason, Integer version, Integer bType, String bObsStart, String bObsEnd, String bObsNote) {
        this.bId = bId;
        this.pId = pId;
        this.dId = dId;
        this.bState = bState;
        this.bStart = bStart;
        this.bReason = bReason;
        this.version = version;
        this.bType = bType;
        this.bObsStart = bObsStart;
        this.bObsEnd = bObsEnd;
        this.bObsNote = bObsNote;
    }

    public String getBId() { return bId; }
    public void setBId(String bId) { this.bId = bId; }
    public int getpId() { return pId; }
    public void setpId(int pId) { this.pId = pId; }
    public String getdId() { return dId; }
    public void setdId(String dId) { this.dId = dId; }
    public Integer getBState() { return bState; }
    public void setBState(Integer bState) { this.bState = bState; }
    public String getBStart() { return bStart; }
    public void setBStart(String bStart) { this.bStart = bStart; }
    public String getBReason() { return bReason; }
    public void setBReason(String bReason) { this.bReason = bReason; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getBType() { return bType; }
    public void setBType(Integer bType) { this.bType = bType; }
    public String getBObsStart() { return bObsStart; }
    public void setBObsStart(String bObsStart) { this.bObsStart = bObsStart; }
    public String getBObsEnd() { return bObsEnd; }
    public void setBObsEnd(String bObsEnd) { this.bObsEnd = bObsEnd; }
    public String getBObsNote() { return bObsNote; }
    public void setBObsNote(String bObsNote) { this.bObsNote = bObsNote; }
    public String getPName() { return pName; }
    public void setPName(String pName) { this.pName = pName; }
    public String getDName() { return dName; }
    public void setDName(String dName) { this.dName = dName; }

    @Override
    public String toString() {
        return "Bed{" +
                "bId=" + bId +
                ", pId=" + pId +
                ", dId=" + dId +
                ", bState=" + bState +
                ", bStart='" + bStart + '\'' +
                ", bReason='" + bReason + '\'' +
                ", version=" + version +
                ", bType=" + bType +
                '}';
    }
}
