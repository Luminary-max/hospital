package com.bear.hospital.controller;

import com.bear.hospital.mapper.OrderMapper;
import com.bear.hospital.mapper.PrescriptionMapper;
import com.bear.hospital.pojo.Orders;
import com.bear.hospital.pojo.OutpatientEmr;
import com.bear.hospital.pojo.PrescriptionDetail;
import com.bear.hospital.service.EmrService;
import com.bear.hospital.utils.ResponseData;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/emr")
public class EmrController {
    @Autowired
    private EmrService emrService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @PostMapping("/save")
    public ResponseData save(@RequestBody OutpatientEmr emr) {
        OutpatientEmr result = emrService.saveEmr(emr);
        return result != null ? ResponseData.success("保存成功", result) : ResponseData.fail("保存失败");
    }

    @GetMapping("/findByOrder")
    public ResponseData findByOrder(@RequestParam int oId) {
        OutpatientEmr emr = emrService.findByOrderId(oId);
        return ResponseData.success("查询成功", emr);
    }

    @GetMapping("/findByPatient")
    public ResponseData findByPatient(@RequestParam int pId) {
        List<OutpatientEmr> list = emrService.findByPatientId(pId);
        return ResponseData.success("查询成功", list);
    }

    @GetMapping("/copyFromHistory")
    public ResponseData copyFromHistory(@RequestParam int emrId, @RequestParam int newOId) {
        OutpatientEmr result = emrService.copyFromHistory(emrId, newOId);
        return result != null ? ResponseData.success("复制成功", result) : ResponseData.fail("复制失败");
    }

    @GetMapping("/pdf")
    public void downloadPDF(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam int emrId) throws Exception {
        OutpatientEmr emr = emrService.findByOrderId(emrId);
        if (emr == null) {
            response.sendError(404, "病历不存在");
            return;
        }
        Orders order = orderMapper.findOrderByOid(emr.getOId());
        List<PrescriptionDetail> prescriptions = prescriptionMapper.findByOrderId(emr.getOId());

        response.setHeader("content-Type", "application/pdf");
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontTitle = new Font(bfChinese, 20, Font.NORMAL);
        Font fontHeader = new Font(bfChinese, 14, Font.NORMAL);
        Font fontNormal = new Font(bfChinese, 12, Font.NORMAL);
        Font fontSmall = new Font(bfChinese, 10, Font.NORMAL);

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Title
        Paragraph title = new Paragraph("门诊病历", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph printTime = new Paragraph("打印时间：" + com.bear.hospital.utils.TodayUtil.getTodayYmd(), fontSmall);
        printTime.setAlignment(Element.ALIGN_CENTER);
        printTime.setSpacingAfter(15);
        document.add(printTime);

        // Patient info table
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(10);
        float[] infoWidths = {15f, 35f, 15f, 35f};
        infoTable.setWidths(infoWidths);

        String pName = emr.getPName() != null ? emr.getPName() : "";
        String pGender = (order != null && order.getPatient() != null) ? order.getPatient().getPGender() : "";
        String pAge = (order != null && order.getPatient() != null && order.getPatient().getPAge() != null)
                ? String.valueOf(order.getPatient().getPAge()) : "";
        String dName = emr.getDName() != null ? emr.getDName() : "";

        infoTable.addCell(new PdfPCell(new Paragraph("患者姓名", fontNormal))).setBorder(0);
        infoTable.addCell(new PdfPCell(new Paragraph(pName, fontNormal))).setBorder(0);
        infoTable.addCell(new PdfPCell(new Paragraph("性别", fontNormal))).setBorder(0);
        infoTable.addCell(new PdfPCell(new Paragraph(pGender, fontNormal))).setBorder(0);
        infoTable.addCell(new PdfPCell(new Paragraph("年龄", fontNormal))).setBorder(0);
        infoTable.addCell(new PdfPCell(new Paragraph(pAge, fontNormal))).setBorder(0);
        infoTable.addCell(new PdfPCell(new Paragraph("医生", fontNormal))).setBorder(0);
        infoTable.addCell(new PdfPCell(new Paragraph(dName, fontNormal))).setBorder(0);
        document.add(infoTable);

        // Content sections
        addSection(document, "主诉", emr.getChiefComplaint(), fontHeader, fontNormal, bfChinese);
        addSection(document, "现病史", emr.getPresentIllness(), fontHeader, fontNormal, bfChinese);
        addSection(document, "既往史", emr.getPastHistory(), fontHeader, fontNormal, bfChinese);
        addSection(document, "体格检查", emr.getPhysicalExam(), fontHeader, fontNormal, bfChinese);
        addSection(document, "诊断", emr.getDiagnosis(), fontHeader, fontNormal, bfChinese);
        addSection(document, "治疗方案", emr.getTreatmentPlan(), fontHeader, fontNormal, bfChinese);

        if (emr.getAllergyHistory() != null && !emr.getAllergyHistory().isEmpty()) {
            addSection(document, "过敏史", emr.getAllergyHistory(), fontHeader, fontNormal, bfChinese);
        }
        if (emr.getMedicalAdvice() != null && !emr.getMedicalAdvice().isEmpty()) {
            addSection(document, "医嘱", emr.getMedicalAdvice(), fontHeader, fontNormal, bfChinese);
        }
        if (emr.getFollowUpSuggest() != null && !emr.getFollowUpSuggest().isEmpty()) {
            addSection(document, "复诊建议", emr.getFollowUpSuggest(), fontHeader, fontNormal, bfChinese);
        }

        // Prescriptions
        if (prescriptions != null && !prescriptions.isEmpty()) {
            Paragraph prescHeader = new Paragraph("处方明细", fontHeader);
            prescHeader.setSpacingBefore(10);
            prescHeader.setSpacingAfter(5);
            document.add(prescHeader);

            PdfPTable drugTable = new PdfPTable(5);
            drugTable.setWidthPercentage(100);
            float[] drugWidths = {30f, 20f, 15f, 15f, 20f};
            drugTable.setWidths(drugWidths);

            Font cellFont = new Font(bfChinese, 10, Font.NORMAL);
            drugTable.addCell(new Paragraph("药品名称", cellFont));
            drugTable.addCell(new Paragraph("规格", cellFont));
            drugTable.addCell(new Paragraph("数量", cellFont));
            drugTable.addCell(new Paragraph("单价", cellFont));
            drugTable.addCell(new Paragraph("用法", cellFont));

            for (PrescriptionDetail pd : prescriptions) {
                drugTable.addCell(new Paragraph(pd.getDrName() != null ? pd.getDrName() : "", cellFont));
                drugTable.addCell(new Paragraph(pd.getPdDosage() != null ? pd.getPdDosage() : "", cellFont));
                drugTable.addCell(new Paragraph(String.valueOf(pd.getPdQuantity() != null ? pd.getPdQuantity() : 0), cellFont));
                drugTable.addCell(new Paragraph(pd.getPdPrice() != null ? String.valueOf(pd.getPdPrice()) : "", cellFont));
                drugTable.addCell(new Paragraph(pd.getPdUsage() != null ? pd.getPdUsage() : "", cellFont));
            }
            document.add(drugTable);
        }

        // Footer
        PdfContentByte cb = writer.getDirectContent();
        cb.beginText();
        cb.setFontAndSize(bfChinese, 10);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "本报告仅供参考，请以医生诊断为准", 300, 30, 0);
        cb.endText();

        document.close();
    }

    private void addSection(Document document, String title, String content,
                            Font headerFont, Font normalFont, BaseFont bfChinese) throws DocumentException {
        if (content == null || content.isEmpty()) return;
        Paragraph p = new Paragraph(title, headerFont);
        p.setSpacingBefore(8);
        p.setSpacingAfter(3);
        document.add(p);
        Paragraph c = new Paragraph(content, normalFont);
        c.setIndentationLeft(10);
        document.add(c);
    }
}
