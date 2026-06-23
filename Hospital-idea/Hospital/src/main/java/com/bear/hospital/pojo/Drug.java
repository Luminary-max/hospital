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
    @JsonProperty("drGenericName")
    private String drGenericName;
    @JsonProperty("drPinyin")
    private String drPinyin;
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
    @JsonProperty("drSubtype")
    private String drSubtype;
    @JsonProperty("drRxType")
    private String drRxType;
    @JsonProperty("drInsuranceType")
    private String drInsuranceType;
    @JsonProperty("drAntibioticLevel")
    private String drAntibioticLevel;
    @JsonProperty("drControlled")
    private Integer drControlled;
    @JsonProperty("drEssential")
    private Integer drEssential;
    @JsonProperty("drMinStock")
    private Integer drMinStock;

    // 新增字段
    @JsonProperty("drMaxStock")
    private Integer drMaxStock;
    @JsonProperty("drWarnDays")
    private Integer drWarnDays;
    @JsonProperty("drImage")
    private String drImage;
    @JsonProperty("drSpec")
    private String drSpec;
    @JsonProperty("drApprovalNo")
    private String drApprovalNo;
    @JsonProperty("drForm")
    private String drForm;
    @JsonProperty("drManufacturer")
    private String drManufacturer;
    @JsonProperty("drStorage")
    private String drStorage;
    @JsonProperty("drIndication")
    private String drIndication;
    @JsonProperty("drContraindication")
    private String drContraindication;
    @JsonProperty("drAdverseReaction")
    private String drAdverseReaction;
    @JsonProperty("drTcmNature")
    private String drTcmNature;
    @JsonProperty("drTcmFlavor")
    private String drTcmFlavor;
    @JsonProperty("drTcmMeridian")
    private String drTcmMeridian;
    @JsonProperty("drDecoctionMethod")
    private String drDecoctionMethod;
    @JsonProperty("drDisabled")
    private Integer drDisabled;

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
    public String getDrGenericName() { return drGenericName; }
    public void setDrGenericName(String drGenericName) { this.drGenericName = drGenericName; }
    public String getDrPinyin() { return drPinyin; }
    public void setDrPinyin(String drPinyin) { this.drPinyin = drPinyin; }
    public double getDrPrice() { return drPrice; }
    public void setDrPrice(double drPrice) { this.drPrice = drPrice; }
    public int getDrNumber() { return drNumber; }
    public void setDrNumber(int drNumber) { this.drNumber = drNumber; }
    public String getDrUnit() { return drUnit; }
    public void setDrUnit(String drUnit) { this.drUnit = drUnit; }
    public String getDrPublisher() { return drPublisher; }
    public void setDrPublisher(String drPublisher) { this.drPublisher = drPublisher; }
    public String getDrSubtype() { return drSubtype; }
    public void setDrSubtype(String drSubtype) { this.drSubtype = drSubtype; }
    public String getDrRxType() { return drRxType; }
    public void setDrRxType(String drRxType) { this.drRxType = drRxType; }
    public String getDrInsuranceType() { return drInsuranceType; }
    public void setDrInsuranceType(String drInsuranceType) { this.drInsuranceType = drInsuranceType; }
    public String getDrAntibioticLevel() { return drAntibioticLevel; }
    public void setDrAntibioticLevel(String drAntibioticLevel) { this.drAntibioticLevel = drAntibioticLevel; }
    public Integer getDrControlled() { return drControlled; }
    public void setDrControlled(Integer drControlled) { this.drControlled = drControlled; }
    public Integer getDrEssential() { return drEssential; }
    public void setDrEssential(Integer drEssential) { this.drEssential = drEssential; }
    public Integer getDrMinStock() { return drMinStock; }
    public void setDrMinStock(Integer drMinStock) { this.drMinStock = drMinStock; }
    public Integer getDrMaxStock() { return drMaxStock; }
    public void setDrMaxStock(Integer drMaxStock) { this.drMaxStock = drMaxStock; }
    public Integer getDrWarnDays() { return drWarnDays; }
    public void setDrWarnDays(Integer drWarnDays) { this.drWarnDays = drWarnDays; }
    public String getDrImage() { return drImage; }
    public void setDrImage(String drImage) { this.drImage = drImage; }

    // 新增 getter/setter
    public String getDrSpec() { return drSpec; }
    public void setDrSpec(String drSpec) { this.drSpec = drSpec; }
    public String getDrApprovalNo() { return drApprovalNo; }
    public void setDrApprovalNo(String drApprovalNo) { this.drApprovalNo = drApprovalNo; }
    public String getDrForm() { return drForm; }
    public void setDrForm(String drForm) { this.drForm = drForm; }
    public String getDrManufacturer() { return drManufacturer; }
    public void setDrManufacturer(String drManufacturer) { this.drManufacturer = drManufacturer; }
    public String getDrStorage() { return drStorage; }
    public void setDrStorage(String drStorage) { this.drStorage = drStorage; }
    public String getDrIndication() { return drIndication; }
    public void setDrIndication(String drIndication) { this.drIndication = drIndication; }
    public String getDrContraindication() { return drContraindication; }
    public void setDrContraindication(String drContraindication) { this.drContraindication = drContraindication; }
    public String getDrAdverseReaction() { return drAdverseReaction; }
    public void setDrAdverseReaction(String drAdverseReaction) { this.drAdverseReaction = drAdverseReaction; }
    public String getDrTcmNature() { return drTcmNature; }
    public void setDrTcmNature(String drTcmNature) { this.drTcmNature = drTcmNature; }
    public String getDrTcmFlavor() { return drTcmFlavor; }
    public void setDrTcmFlavor(String drTcmFlavor) { this.drTcmFlavor = drTcmFlavor; }
    public String getDrTcmMeridian() { return drTcmMeridian; }
    public void setDrTcmMeridian(String drTcmMeridian) { this.drTcmMeridian = drTcmMeridian; }
    public String getDrDecoctionMethod() { return drDecoctionMethod; }
    public void setDrDecoctionMethod(String drDecoctionMethod) { this.drDecoctionMethod = drDecoctionMethod; }

    public Integer getDrDisabled() { return drDisabled; }
    public void setDrDisabled(Integer drDisabled) { this.drDisabled = drDisabled; }

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
