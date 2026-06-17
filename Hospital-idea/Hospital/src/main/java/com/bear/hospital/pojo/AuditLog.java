package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("audit_log")
public class AuditLog {
    @TableId(value = "al_id")
    @JsonProperty("alId")
    private Integer alId;
    @JsonProperty("alUserId")
    private String alUserId;
    @JsonProperty("alUserRole")
    private String alUserRole;
    @JsonProperty("alAction")
    private String alAction;
    @JsonProperty("alTarget")
    private String alTarget;
    @JsonProperty("alDetail")
    private String alDetail;
    @JsonProperty("alIp")
    private String alIp;
    @JsonProperty("alCreateTime")
    private String alCreateTime;

    public AuditLog() {}

    public Integer getAlId() { return alId; }
    public void setAlId(Integer alId) { this.alId = alId; }
    public String getAlUserId() { return alUserId; }
    public void setAlUserId(String alUserId) { this.alUserId = alUserId; }
    public String getAlUserRole() { return alUserRole; }
    public void setAlUserRole(String alUserRole) { this.alUserRole = alUserRole; }
    public String getAlAction() { return alAction; }
    public void setAlAction(String alAction) { this.alAction = alAction; }
    public String getAlTarget() { return alTarget; }
    public void setAlTarget(String alTarget) { this.alTarget = alTarget; }
    public String getAlDetail() { return alDetail; }
    public void setAlDetail(String alDetail) { this.alDetail = alDetail; }
    public String getAlIp() { return alIp; }
    public void setAlIp(String alIp) { this.alIp = alIp; }
    public String getAlCreateTime() { return alCreateTime; }
    public void setAlCreateTime(String alCreateTime) { this.alCreateTime = alCreateTime; }
}
