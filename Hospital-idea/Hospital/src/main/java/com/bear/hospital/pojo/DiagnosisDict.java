package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("diagnosis_dict")
public class DiagnosisDict {
    @TableId(value = "dd_id")
    @JsonProperty("ddId")
    private Integer ddId;
    @JsonProperty("ddCode")
    private String ddCode;
    @JsonProperty("ddName")
    private String ddName;
    @JsonProperty("ddDept")
    private String ddDept;
    @JsonProperty("ddSort")
    private Integer ddSort;
    @JsonProperty("ddPinyin")
    private String ddPinyin;

    public DiagnosisDict() {}

    public Integer getDdId() { return ddId; }
    public void setDdId(Integer ddId) { this.ddId = ddId; }
    public String getDdCode() { return ddCode; }
    public void setDdCode(String ddCode) { this.ddCode = ddCode; }
    public String getDdName() { return ddName; }
    public void setDdName(String ddName) { this.ddName = ddName; }
    public String getDdDept() { return ddDept; }
    public void setDdDept(String ddDept) { this.ddDept = ddDept; }
    public Integer getDdSort() { return ddSort; }
    public void setDdSort(Integer ddSort) { this.ddSort = ddSort; }
    public String getDdPinyin() { return ddPinyin; }
    public void setDdPinyin(String ddPinyin) { this.ddPinyin = ddPinyin; }
}
