-- ========================================
-- 补充更多病历模板和处方模板
-- ========================================

-- ========== 病历模板（已有7个，新增8个）==========
INSERT INTO emr_template (et_name, et_dept, d_id, et_chief_complaint, et_present_illness, et_past_history, et_physical_exam, et_diagnosis, et_treatment_plan, et_create_time) VALUES
('高血压急诊模板', '心血管内科', '202601', '剧烈头痛、视物模糊、恶心呕吐{天数}小时', '患者{天数}小时前突发剧烈头痛，视物模糊，伴恶心呕吐{次数}次。自测血压高达{收缩压}/{舒张压}mmHg。', '既往高血压病史{年数}年，最高血压{最高血压}mmHg。', 'BP {血压}mmHg，神清，瞳孔等大等圆。心肺听诊无明显异常。', '高血压急症', '1.立即降压治疗，硝苯地平10mg舌下含服。2.心电监护。3.完善头颅CT排除脑出血。4.收入院治疗。', NOW()),
('冠心病急诊模板', '心血管内科', '202601', '持续性胸痛{分钟}分钟，向左上肢放射', '患者{分钟}分钟前无明显诱因出现胸骨后压榨样疼痛，向左上肢放射，伴大汗淋漓、面色苍白。含服硝酸甘油不缓解。', '既往冠心病史{年数}年，高血压病史。吸烟{烟龄}年。', 'BP {血压}mmHg，HR {心率}次/分。心音低钝，可闻及奔马律。', '急性冠脉综合征（STEMI待排）', '1.立即心电监护。2.吸氧4L/min。3.阿司匹林300mg嚼服+替格瑞洛180mg负荷。4.急诊PCI术前准备。', NOW()),
('慢性阻塞性肺疾病模板', '呼吸内科', '202601', '反复咳痰喘{年数}年，加重{天数}天', '患者{年数}年前开始反复出现咳嗽咳痰，每年发作{次数}次。近{天数}天上述症状加重，伴气促、呼吸困难，稍活动即感喘息。', 'COPD病史{年数}年，吸烟{烟龄}年，每日{支数}支。', 'T {体温}℃，R {呼吸}次/分，SpO2 {氧饱和度}%。桶状胸，双肺呼吸音低，可闻及湿啰音和哮鸣音。', '慢性阻塞性肺疾病急性加重', '1.吸氧（低流量1-2L/min）。2.支气管舒张剂（沙丁胺醇雾化）。3.糖皮质激素静脉滴注。4.抗生素抗感染治疗。5.祛痰止咳。', NOW()),
('急性胰腺炎模板', '消化内科', '202601', '上腹部剧烈疼痛{小时}小时，向后背放射', '患者{小时}小时前进食油腻食物后出现上腹部持续性剧痛，向后背放射，伴恶心呕吐，腹胀，无排便排气。', '既往胆结石病史{年数}年，否认高血压糖尿病。', 'T {体温}℃，腹膨隆，上腹部压痛明显，反跳痛(+)，Murphy征可疑。肠鸣音减弱。', '急性胰腺炎（水肿型？）', '1.禁食水、胃肠减压。2.抑制胰酶分泌（生长抑素）。3.液体复苏、纠正电解质紊乱。4.镇痛。5.完善CT、血尿淀粉酶检查。', NOW()),
('脑梗死模板', '神经内科', '202601', '突发口齿不清、右侧肢体无力{小时}小时', '患者{小时}小时前安静状态下突发口齿不清，右侧肢体无力，持物不能，行走困难。无恶心呕吐，无意识障碍。', '既往高血压{年数}年，糖尿病{年数}年，房颤史{年数}年。', 'BP {血压}mmHg，神清，言语含糊。右侧鼻唇沟变浅，伸舌右偏。右侧上下肢肌力{肌力}级，右侧巴氏征(+)。', '急性脑梗死', '1.急诊头颅CT排除出血。2.完善NIHSS评分。3.若在时间窗内(4.5h)评估溶栓。4.抗血小板治疗。5.他汀类药物。6.康复治疗。', NOW()),
('泌尿系感染模板', '泌尿外科', '202601', '尿频、尿急、尿痛{天数}天', '患者{天数}天前出现尿频（每日{次数}余次）、尿急、尿痛，伴下腹部坠胀感。无发热，无肉眼血尿。', '既往体健，否认药物过敏史。', 'T {体温}℃，双肾区无叩击痛，耻骨上轻压痛。', '急性下尿路感染', '1.抗生素治疗（左氧氟沙星0.5g qd×7天）。2.多饮水，保持尿量。3.碱化尿液。4.完善尿常规+中段尿培养。', NOW()),
('颈椎病模板', '骨科', '202601', '颈肩部疼痛伴上肢麻木{天数}天', '患者{天数}天前无明显诱因出现颈肩部酸痛，放射至右上臂及前臂，伴手指麻木。低头工作后加重。', '既往长期伏案工作{年数}年。', '颈椎活动受限，颈{节段}棘突旁压痛，压顶试验(+)，右侧臂丛神经牵拉试验(+)。', '颈椎病（神经根型）', '1.颈椎制动，避免长时间低头。2.非甾体抗炎药（塞来昔布）。3.甲钴胺营养神经。4.物理治疗（牵引、理疗）。5.必要时MRI检查。', NOW()),
('湿疹模板', '皮肤科', '202601', '全身皮疹伴瘙痒{天数}天', '患者{天数}天前无明显诱因出现躯干、四肢红斑、丘疹，伴剧烈瘙痒，搔抓后出现渗液。反复发作。', '既往过敏史：花粉、尘螨过敏。', '躯干四肢可见对称分布红斑、丘疹、抓痕，部分融合成片，有少量渗液。', '湿疹（急性期）', '1.外用糖皮质激素药膏。2.口服抗组胺药（氯雷他定10mg qd）。3.避免搔抓和热水烫洗。4.保湿护肤。5.忌食辛辣刺激食物。', NOW());

-- ========== 处方模板（已有3个，新增10个）==========
INSERT INTO prescription_template (pt_name, d_id, pt_diagnosis, pt_dept, pt_content, pt_create_time) VALUES
('上呼吸道感染处方（儿科）', '202601', '上呼吸道感染', '儿科',
 '[{"drId":"D008","drName":"头孢克肟分散片","drPrice":22.00,"pdUsage":"冲服","pdDosage":"25mg","pdFrequency":"每日2次","pdDays":5,"pdQuantity":2,"pdRoute":"冲服","pdTiming":"餐后"},{"drId":"D005","drName":"连花清瘟胶囊","drPrice":14.80,"pdUsage":"口服","pdDosage":"4粒","pdFrequency":"每日3次","pdDays":5,"pdQuantity":1,"pdRoute":"口服","pdTiming":"餐后"}]', NOW()),
('高血压联合用药处方', '202601', '高血压病', '心血管内科',
 '[{"drId":"D003","drName":"硝苯地平控释片","drPrice":35.00,"pdUsage":"口服","pdDosage":"30mg","pdFrequency":"每日1次","pdDays":30,"pdQuantity":2,"pdRoute":"口服","pdTiming":"晨起"},{"drId":"D008","drName":"阿托伐他汀钙片","drPrice":42.00,"pdUsage":"口服","pdDosage":"20mg","pdFrequency":"每晚1次","pdDays":30,"pdQuantity":1,"pdRoute":"口服","pdTiming":"睡前"},{"drId":"D00017","drName":"阿司匹林肠溶片","drPrice":6.30,"pdUsage":"口服","pdDosage":"100mg","pdFrequency":"每日1次","pdDays":30,"pdQuantity":1,"pdRoute":"口服","pdTiming":"晨起"}]', NOW()),
('糖尿病综合处方', '202601', '2型糖尿病', '内分泌科',
 '[{"drId":"D004","drName":"盐酸二甲双胍片","drPrice":8.50,"pdUsage":"口服","pdDosage":"0.5g","pdFrequency":"每日3次","pdDays":30,"pdQuantity":3,"pdRoute":"口服","pdTiming":"餐前"},{"drId":"D00007","drName":"胰岛素注射液","drPrice":45.60,"pdUsage":"皮下注射","pdDosage":"6IU","pdFrequency":"每日2次","pdDays":30,"pdQuantity":2,"pdRoute":"皮下注射","pdTiming":"餐前"},{"drId":"D00011","drName":"维生素C片","drPrice":1.20,"pdUsage":"口服","pdDosage":"0.2g","pdFrequency":"每日3次","pdDays":30,"pdQuantity":2,"pdRoute":"口服","pdTiming":"餐后"}]', NOW()),
('冠心病二级预防处方', '202601', '冠状动脉粥样硬化性心脏病', '心血管内科',
 '[{"drId":"D00017","drName":"阿司匹林肠溶片","drPrice":6.30,"pdUsage":"口服","pdDosage":"100mg","pdFrequency":"每日1次","pdDays":30,"pdQuantity":2,"pdRoute":"口服","pdTiming":"晨起"},{"drId":"D00018","drName":"硫酸氢氯吡格雷片","drPrice":28.50,"pdUsage":"口服","pdDosage":"75mg","pdFrequency":"每日1次","pdDays":30,"pdQuantity":2,"pdRoute":"口服","pdTiming":"晨起"},{"drId":"D008","drName":"阿托伐他汀钙片","drPrice":42.00,"pdUsage":"口服","pdDosage":"20mg","pdFrequency":"每晚1次","pdDays":30,"pdQuantity":2,"pdRoute":"口服","pdTiming":"睡前"},{"drId":"D00020","drName":"硝酸异山梨酯片","drPrice":19.80,"pdUsage":"舌下含服","pdDosage":"5mg","pdFrequency":"必要时服用","pdDays":30,"pdQuantity":1,"pdRoute":"舌下含服","pdTiming":"胸闷时"}]', NOW()),
('急性肠胃炎处方', '202601', '急性胃肠炎', '消化内科',
 '[{"drId":"D009","drName":"蒙脱石散","drPrice":6.50,"pdUsage":"冲服","pdDosage":"3g","pdFrequency":"每日3次","pdDays":3,"pdQuantity":3,"pdRoute":"冲服","pdTiming":"餐前"},{"drId":"D00002","drName":"阿莫西林胶囊","drPrice":5.75,"pdUsage":"口服","pdDosage":"0.5g","pdFrequency":"每日3次","pdDays":3,"pdQuantity":2,"pdRoute":"口服","pdTiming":"餐后"},{"drId":"D00019","drName":"多潘立酮片","drPrice":11.90,"pdUsage":"口服","pdDosage":"10mg","pdFrequency":"每日3次","pdDays":3,"pdQuantity":1,"pdRoute":"口服","pdTiming":"餐前"}]', NOW()),
('COPD稳定期处方', '202601', '慢性阻塞性肺疾病', '呼吸内科',
 '[{"drId":"D00027","drName":"氨茶碱片","drPrice":3.00,"pdUsage":"口服","pdDosage":"0.1g","pdFrequency":"每日3次","pdDays":14,"pdQuantity":2,"pdRoute":"口服","pdTiming":"餐后"},{"drId":"D00025","drName":"氯雷他定片","drPrice":5.90,"pdUsage":"口服","pdDosage":"10mg","pdFrequency":"每日1次","pdDays":14,"pdQuantity":1,"pdRoute":"口服","pdTiming":"睡前"}]', NOW()),
('消化性溃疡处方', '202601', '消化性溃疡', '消化内科',
 '[{"drId":"D00029","drName":"奥美拉唑肠溶胶囊","drPrice":14.60,"pdUsage":"口服","pdDosage":"20mg","pdFrequency":"每日1次","pdDays":28,"pdQuantity":2,"pdRoute":"口服","pdTiming":"晨起空腹"}]', NOW()),
('湿疹外用药处方', '202601', '湿疹', '皮肤科',
 '[{"drId":"D00025","drName":"氯雷他定片","drPrice":5.90,"pdUsage":"口服","pdDosage":"10mg","pdFrequency":"每日1次","pdDays":7,"pdQuantity":1,"pdRoute":"口服","pdTiming":"睡前"}]', NOW()),
('痛风急性期处方', '202601', '痛风性关节炎', '骨科',
 '[{"drId":"D00003","drName":"布洛芬缓释胶囊","drPrice":3.20,"pdUsage":"口服","pdDosage":"0.3g","pdFrequency":"每日2次","pdDays":7,"pdQuantity":2,"pdRoute":"口服","pdTiming":"餐后"},{"drId":"D00016","drName":"地塞米松磷酸钠注射液","drPrice":4.80,"pdUsage":"肌肉注射","pdDosage":"5mg","pdFrequency":"必要时使用","pdDays":3,"pdQuantity":1,"pdRoute":"肌肉注射","pdTiming":"疼痛时"}]', NOW()),
('营养支持处方', '202601', '营养不良', '内科',
 '[{"drId":"D00010","drName":"复方氨基酸注射液","drPrice":36.20,"pdUsage":"静脉滴注","pdDosage":"250ml","pdFrequency":"每日1次","pdDays":7,"pdQuantity":1,"pdRoute":"静脉滴注","pdTiming":"上午"}]', NOW()),
('失眠处方', '202601', '失眠症', '内科',
 '[{"drId":"D00011","drName":"维生素C片","drPrice":1.20,"pdUsage":"口服","pdDosage":"0.2g","pdFrequency":"每日3次","pdDays":14,"pdQuantity":1,"pdRoute":"口服","pdTiming":"餐后"},{"drId":"D00024","drName":"复方丹参滴丸","drPrice":25.40,"pdUsage":"舌下含服","pdDosage":"10粒","pdFrequency":"每日3次","pdDays":14,"pdQuantity":2,"pdRoute":"舌下含服","pdTiming":"三餐后"}]', NOW());
