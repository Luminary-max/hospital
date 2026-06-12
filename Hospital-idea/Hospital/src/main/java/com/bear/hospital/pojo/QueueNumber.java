package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("queue_number")
public class QueueNumber {
    @TableId(value = "q_id")
    @JsonProperty("qId")
    private int qId;
    @JsonProperty("oId")
    private int oId;
    @JsonProperty("pId")
    private int pId;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("qNumber")
    private String qNumber;
    @JsonProperty("qState")
    private Integer qState;
    @JsonProperty("qCreateTime")
    private String qCreateTime;
    @JsonProperty("qCallTime")
    private String qCallTime;
    @JsonProperty("qFinishTime")
    private String qFinishTime;

    @TableField(exist = false)
    @JsonProperty("pName")
    private String pName;
    @TableField(exist = false)
    @JsonProperty("dName")
    private String dName;
    @TableField(exist = false)
    @JsonProperty("deptName")
    private String deptName;

    public QueueNumber() {}

    public int getQId() { return qId; }
    public void setQId(int qId) { this.qId = qId; }
    public int getOId() { return oId; }
    public void setOId(int oId) { this.oId = oId; }
    public int getPId() { return pId; }
    public void setPId(int pId) { this.pId = pId; }
    public String getDId() { return dId; }
    public void setDId(String dId) { this.dId = dId; }
    public String getQNumber() { return qNumber; }
    public void setQNumber(String qNumber) { this.qNumber = qNumber; }
    public Integer getQState() { return qState; }
    public void setQState(Integer qState) { this.qState = qState; }
    public String getQCreateTime() { return qCreateTime; }
    public void setQCreateTime(String qCreateTime) { this.qCreateTime = qCreateTime; }
    public String getQCallTime() { return qCallTime; }
    public void setQCallTime(String qCallTime) { this.qCallTime = qCallTime; }
    public String getQFinishTime() { return qFinishTime; }
    public void setQFinishTime(String qFinishTime) { this.qFinishTime = qFinishTime; }
    public String getPName() { return pName; }
    public void setPName(String pName) { this.pName = pName; }
    public String getDName() { return dName; }
    public void setDName(String dName) { this.dName = dName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
}
