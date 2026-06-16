package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("notification")
public class Notification {
    @TableId(value = "n_id")
    @JsonProperty("nId")
    private Integer nId;
    @JsonProperty("pId")
    private Integer pId;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("nType")
    private String nType;
    @JsonProperty("nTitle")
    private String nTitle;
    @JsonProperty("nContent")
    private String nContent;
    @JsonProperty("nIsRead")
    private Integer nIsRead;
    @JsonProperty("nCreateTime")
    private String nCreateTime;

    public Notification() {}

    public Integer getNId() { return nId; }
    public void setNId(Integer nId) { this.nId = nId; }
    public Integer getPId() { return pId; }
    public void setPId(Integer pId) { this.pId = pId; }
    public String getDId() { return dId; }
    public void setDId(String dId) { this.dId = dId; }
    public String getNType() { return nType; }
    public void setNType(String nType) { this.nType = nType; }
    public String getNTitle() { return nTitle; }
    public void setNTitle(String nTitle) { this.nTitle = nTitle; }
    public String getNContent() { return nContent; }
    public void setNContent(String nContent) { this.nContent = nContent; }
    public Integer getNIsRead() { return nIsRead; }
    public void setNIsRead(Integer nIsRead) { this.nIsRead = nIsRead; }
    public String getNCreateTime() { return nCreateTime; }
    public void setNCreateTime(String nCreateTime) { this.nCreateTime = nCreateTime; }
}
