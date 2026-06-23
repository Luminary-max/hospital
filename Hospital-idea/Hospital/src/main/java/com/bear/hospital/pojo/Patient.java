package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("patient")
public class Patient {
    @TableId(value = "p_id")
    @JsonProperty("pId")
    private int pId;
    @JsonProperty("pPassword")
    private String pPassword;
    @JsonProperty("pName")
    private String pName;
    @JsonProperty("pGender")
    private String pGender;
    @JsonProperty("pCard")
    private String pCard;
    @JsonProperty("pEmail")
    private String pEmail;
    @JsonProperty("pPhone")
    private String pPhone;
    @JsonProperty("pState")
    private Integer pState;
    @JsonProperty("pBirthday")
    private String pBirthday;
    @JsonProperty("pAge")
    private Integer pAge;

    // 新增字段
    @JsonProperty("pInsuranceId")
    private String pInsuranceId;
    @JsonProperty("pInsuranceType")
    private String pInsuranceType;
    @JsonProperty("pContactPerson")
    private String pContactPerson;
    @JsonProperty("pContactPhone")
    private String pContactPhone;
    @JsonProperty("pAddress")
    private String pAddress;
    @JsonProperty("pNation")
    private String pNation;
    @JsonProperty("pMaritalStatus")
    private String pMaritalStatus;
    @JsonProperty("pBloodType")
    private String pBloodType;
    @JsonProperty("pBlacklisted")
    private Integer pBlacklisted;
    @JsonProperty("pTags")
    private String pTags;

    public Patient() {
    }

    public Patient(int pId, String pPassword, String pName, String pGender, String pCard, String pEmail, String pPhone, Integer pState, String pBirthday, Integer pAge, String pInsuranceId, String pInsuranceType, String pContactPerson, String pContactPhone, String pAddress, String pNation, String pMaritalStatus, String pBloodType, Integer pBlacklisted, String pTags) {
        this.pId = pId;
        this.pPassword = pPassword;
        this.pName = pName;
        this.pGender = pGender;
        this.pCard = pCard;
        this.pEmail = pEmail;
        this.pPhone = pPhone;
        this.pState = pState;
        this.pBirthday = pBirthday;
        this.pAge = pAge;
        this.pInsuranceId = pInsuranceId;
        this.pInsuranceType = pInsuranceType;
        this.pContactPerson = pContactPerson;
        this.pContactPhone = pContactPhone;
        this.pAddress = pAddress;
        this.pNation = pNation;
        this.pMaritalStatus = pMaritalStatus;
        this.pBloodType = pBloodType;
        this.pBlacklisted = pBlacklisted;
        this.pTags = pTags;
    }

    public int getPId() { return pId; }
    public void setPId(int pId) { this.pId = pId; }
    public String getPPassword() { return pPassword; }
    public void setPPassword(String pPassword) { this.pPassword = pPassword; }
    public String getPName() { return pName; }
    public void setPName(String pName) { this.pName = pName; }
    public String getPGender() { return pGender; }
    public void setPGender(String pGender) { this.pGender = pGender; }
    public String getPCard() { return pCard; }
    public void setPCard(String pCard) { this.pCard = pCard; }
    public String getPEmail() { return pEmail; }
    public void setPEmail(String pEmail) { this.pEmail = pEmail; }
    public String getPPhone() { return pPhone; }
    public void setPPhone(String pPhone) { this.pPhone = pPhone; }
    public Integer getPState() { return pState; }
    public void setPState(Integer pState) { this.pState = pState; }
    public String getPBirthday() { return pBirthday; }
    public void setPBirthday(String pBirthday) { this.pBirthday = pBirthday; }
    public Integer getPAge() { return pAge; }
    public void setPAge(Integer pAge) { this.pAge = pAge; }

    public String getPInsuranceId() { return pInsuranceId; }
    public void setPInsuranceId(String pInsuranceId) { this.pInsuranceId = pInsuranceId; }
    public String getPInsuranceType() { return pInsuranceType; }
    public void setPInsuranceType(String pInsuranceType) { this.pInsuranceType = pInsuranceType; }
    public String getPContactPerson() { return pContactPerson; }
    public void setPContactPerson(String pContactPerson) { this.pContactPerson = pContactPerson; }
    public String getPContactPhone() { return pContactPhone; }
    public void setPContactPhone(String pContactPhone) { this.pContactPhone = pContactPhone; }
    public String getPAddress() { return pAddress; }
    public void setPAddress(String pAddress) { this.pAddress = pAddress; }
    public String getPNation() { return pNation; }
    public void setPNation(String pNation) { this.pNation = pNation; }
    public String getPMaritalStatus() { return pMaritalStatus; }
    public void setPMaritalStatus(String pMaritalStatus) { this.pMaritalStatus = pMaritalStatus; }
    public String getPBloodType() { return pBloodType; }
    public void setPBloodType(String pBloodType) { this.pBloodType = pBloodType; }

    public Integer getPBlacklisted() { return pBlacklisted; }
    public void setPBlacklisted(Integer pBlacklisted) { this.pBlacklisted = pBlacklisted; }
    public String getPTags() { return pTags; }
    public void setPTags(String pTags) { this.pTags = pTags; }

    @Override
    public String toString() {
        return "Patient{" +
                "pId=" + pId +
                ", pName='" + pName + '\'' +
                ", pGender='" + pGender + '\'' +
                ", pCard='" + pCard + '\'' +
                ", pPhone='" + pPhone + '\'' +
                ", pBirthday='" + pBirthday + '\'' +
                ", pAge=" + pAge +
                ", pInsuranceId='" + pInsuranceId + '\'' +
                ", pInsuranceType='" + pInsuranceType + '\'' +
                ", pContactPerson='" + pContactPerson + '\'' +
                ", pContactPhone='" + pContactPhone + '\'' +
                ", pAddress='" + pAddress + '\'' +
                ", pNation='" + pNation + '\'' +
                ", pMaritalStatus='" + pMaritalStatus + '\'' +
                ", pBloodType='" + pBloodType + '\'' +
                ", pBlacklisted=" + pBlacklisted +
                ", pTags='" + pTags + '\'' +
                '}';
    }
}
