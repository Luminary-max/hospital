# SQL 初始化文件结构检查报告

## 问题原因

此前生成的 `hospital_complete_ai_init.sql` 使用了较旧的 `hospital_complete.sql` 作为基线。该文件缺少当前代码已经依赖的一部分既有字段和表，例如：

- `doctor.d_max_daily`
- `order_check`
- `prescription_master`
- `drug_category`
- `invoice_record`
- `refund_request`
- `delivery_request`
- `diagnosis_dict`
- `inventory_transaction`
- 多个业务字段：药品库存预警字段、病历过敏史/医嘱字段、分诊生命体征字段等

这些不是新增 AI 功能字段，而是系统现有业务功能已经使用的结构，因此完整初始化 SQL 不能基于旧文件继续拼接。

## 修复方式

已将完整初始化文件的基线改为 `hospital_export.sql`，该文件与 `db_schema.sql` 做了表和字段级对比：

- `hospital_export.sql` 覆盖 `db_schema.sql` 的全部表。
- `hospital_export.sql` 覆盖 `db_schema.sql` 的全部字段。
- 在此基础上追加 AI/智慧门诊扩展脚本 `db_optimization_ai_extension.sql`。

## 当前结果

新的完整初始化文件：

- `hospital_complete_ai_init.sql`

结构检查结果：

- 初始化文件表数量：41
- 旧系统设计表缺失：0
- 旧系统设计字段缺失：0
- 含初始化数据的表数量：41
- 缺少初始化数据的表数量：0
- 优化索引调用字段缺失：0
- 新增扩展表已包含：
  - `staff_user`
  - `admin_role_permission`
  - `health_profile`
  - `hospital_announcement`
  - `ai_diagnosis_record`
  - `ai_report_analysis`
  - `referral_record`
  - `insurance_settlement`
- 本次补齐初始化数据的表：
  - `prescribe`
  - `health_profile`
  - `ai_diagnosis_record`
  - `ai_report_analysis`
  - `referral_record`
  - `insurance_settlement`

## 使用建议

后续请优先使用 `hospital_complete_ai_init.sql` 初始化数据库。该文件保留旧功能结构和数据，再追加新增扩展，不会删除既有功能字段。
