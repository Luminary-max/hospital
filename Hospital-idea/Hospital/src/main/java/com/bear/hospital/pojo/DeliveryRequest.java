package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("delivery_request")
public class DeliveryRequest {
    @TableId(value = "dl_id")
    @JsonProperty("dlId")
    private Integer dlId;
    @JsonProperty("oId")
    private Integer oId;
    @JsonProperty("pId")
    private Integer pId;
    @JsonProperty("dlAgentName")
    private String dlAgentName;
    @JsonProperty("dlAgentIdCard")
    private String dlAgentIdCard;
    @JsonProperty("dlAgentPhone")
    private String dlAgentPhone;
    @JsonProperty("dlPickupCode")
    private String dlPickupCode;
    @JsonProperty("dlStatus")
    private Integer dlStatus;
    @JsonProperty("dlCreateTime")
    private String dlCreateTime;
    @JsonProperty("dlPickupTime")
    private String dlPickupTime;

    public DeliveryRequest() {}

    public Integer getDlId() { return dlId; }
    public void setDlId(Integer dlId) { this.dlId = dlId; }
    public Integer getOId() { return oId; }
    public void setOId(Integer oId) { this.oId = oId; }
    public Integer getPId() { return pId; }
    public void setPId(Integer pId) { this.pId = pId; }
    public String getDlAgentName() { return dlAgentName; }
    public void setDlAgentName(String dlAgentName) { this.dlAgentName = dlAgentName; }
    public String getDlAgentIdCard() { return dlAgentIdCard; }
    public void setDlAgentIdCard(String dlAgentIdCard) { this.dlAgentIdCard = dlAgentIdCard; }
    public String getDlAgentPhone() { return dlAgentPhone; }
    public void setDlAgentPhone(String dlAgentPhone) { this.dlAgentPhone = dlAgentPhone; }
    public String getDlPickupCode() { return dlPickupCode; }
    public void setDlPickupCode(String dlPickupCode) { this.dlPickupCode = dlPickupCode; }
    public Integer getDlStatus() { return dlStatus; }
    public void setDlStatus(Integer dlStatus) { this.dlStatus = dlStatus; }
    public String getDlCreateTime() { return dlCreateTime; }
    public void setDlCreateTime(String dlCreateTime) { this.dlCreateTime = dlCreateTime; }
    public String getDlPickupTime() { return dlPickupTime; }
    public void setDlPickupTime(String dlPickupTime) { this.dlPickupTime = dlPickupTime; }
}
