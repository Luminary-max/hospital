package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("triage_record")
public class TriageRecord {
    @TableId(value = "t_id")
    @JsonProperty("tId")
    private Integer tId;
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
    @JsonProperty("tChiefComplaint")
    private String tChiefComplaint;
    @JsonProperty("tSource")
    private String tSource;

    // Vital signs fields
    @JsonProperty("tTemperature")
    private String tTemperature;
    @JsonProperty("tBloodPressure")
    private String tBloodPressure;
    @JsonProperty("tHeartRate")
    private Integer tHeartRate;
    @JsonProperty("tWeight")
    private String tWeight;

    @TableField(exist = false)
    @JsonProperty("pName")
    private String pName;
    @TableField(exist = false)
    @JsonProperty("dName")
    private String dName;

    public TriageRecord() {}

    public Integer getTId() { return tId; }
    public void setTId(Integer tId) { this.tId = tId; }
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

    public String getTTemperature() { return tTemperature; }
    public void setTTemperature(String tTemperature) { this.tTemperature = tTemperature; }
    public String getTBloodPressure() { return tBloodPressure; }
    public void setTBloodPressure(String tBloodPressure) { this.tBloodPressure = tBloodPressure; }
    public Integer getTHeartRate() { return tHeartRate; }
    public void setTHeartRate(Integer tHeartRate) { this.tHeartRate = tHeartRate; }
    public String getTWeight() { return tWeight; }
    public void setTWeight(String tWeight) { this.tWeight = tWeight; }

    public String getTChiefComplaint() { return tChiefComplaint; }
    public void setTChiefComplaint(String tChiefComplaint) { this.tChiefComplaint = tChiefComplaint; }
    public String getTSource() { return tSource; }
    public void setTSource(String tSource) { this.tSource = tSource; }

    public String getPName() { return pName; }
    public void setPName(String pName) { this.pName = pName; }
    public String getDName() { return dName; }
    public void setDName(String dName) { this.dName = dName; }
}
