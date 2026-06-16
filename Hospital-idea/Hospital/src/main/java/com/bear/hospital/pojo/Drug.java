package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("drug")
public class Drug {
    @TableId(value = "dr_id")
    @JsonProperty("drId")
    private String drId;
    @JsonProperty("drName")
    private String drName;
    @JsonProperty("drPrice")
    private double drPrice;
    @JsonProperty("drNumber")
    private int drNumber;
    @JsonProperty("drUnit")
    private String drUnit;
    @JsonProperty("drPublisher")
    private String drPublisher;
    @JsonProperty("drType")
    private Integer drType;

    // 新增字段
    @JsonProperty("drSpec")
    private String drSpec;
    @JsonProperty("drApprovalNo")
    private String drApprovalNo;
    @JsonProperty("drForm")
    private String drForm;
    @JsonProperty("drManufacturer")
    private String drManufacturer;

    public Drug() {
    }

    public Drug(String drId, String drName, double drPrice, int drNumber, String drUnit, String drPublisher, Integer drType, String drSpec, String drApprovalNo, String drForm, String drManufacturer) {
        this.drId = drId;
        this.drName = drName;
        this.drPrice = drPrice;
        this.drNumber = drNumber;
        this.drUnit = drUnit;
        this.drPublisher = drPublisher;
        this.drType = drType;
        this.drSpec = drSpec;
        this.drApprovalNo = drApprovalNo;
        this.drForm = drForm;
        this.drManufacturer = drManufacturer;
    }

    // 原有 getter/setter
    public Integer getDrType() { return drType; }
    public void setDrType(Integer drType) { this.drType = drType; }
    public String getDrId() { return drId; }
    public void setDrId(String drId) { this.drId = drId; }
    public String getDrName() { return drName; }
    public void setDrName(String drName) { this.drName = drName; }
    public double getDrPrice() { return drPrice; }
    public void setDrPrice(double drPrice) { this.drPrice = drPrice; }
    public int getDrNumber() { return drNumber; }
    public void setDrNumber(int drNumber) { this.drNumber = drNumber; }
    public String getDrUnit() { return drUnit; }
    public void setDrUnit(String drUnit) { this.drUnit = drUnit; }
    public String getDrPublisher() { return drPublisher; }
    public void setDrPublisher(String drPublisher) { this.drPublisher = drPublisher; }

    // 新增 getter/setter
    public String getDrSpec() { return drSpec; }
    public void setDrSpec(String drSpec) { this.drSpec = drSpec; }
    public String getDrApprovalNo() { return drApprovalNo; }
    public void setDrApprovalNo(String drApprovalNo) { this.drApprovalNo = drApprovalNo; }
    public String getDrForm() { return drForm; }
    public void setDrForm(String drForm) { this.drForm = drForm; }
    public String getDrManufacturer() { return drManufacturer; }
    public void setDrManufacturer(String drManufacturer) { this.drManufacturer = drManufacturer; }

    @Override
    public String toString() {
        return "Drug{" +
                "drId=" + drId +
                ", drName='" + drName + '\'' +
                ", drPrice=" + drPrice +
                ", drNumber=" + drNumber +
                ", drUnit='" + drUnit + '\'' +
                ", drPublisher='" + drPublisher + '\'' +
                ", drType=" + drType +
                ", drSpec='" + drSpec + '\'' +
                ", drApprovalNo='" + drApprovalNo + '\'' +
                ", drForm='" + drForm + '\'' +
                ", drManufacturer='" + drManufacturer + '\'' +
                '}';
    }
}
