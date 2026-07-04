package com.bear.hospital.controller;

import com.bear.hospital.mapper.OrderMapper;
import com.bear.hospital.pojo.Orders;
import com.bear.hospital.pojo.Patient;
import com.bear.hospital.service.DoctorService;
import com.bear.hospital.service.OrderService;
import com.bear.hospital.service.PatientService;
import com.bear.hospital.service.QueueService;
import com.bear.hospital.utils.JwtUtil;
import com.bear.hospital.utils.PdfUtil;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("patient")
public class PatientController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private QueueService queueService;
    @Autowired
    private JedisPool jedisPool;
    @Resource
    private OrderMapper orderMapper;

    /**
     * 登录数据验证
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData login(@RequestParam(value = "pId") int pId, @RequestParam(value = "pPassword") String pPassword) {
        Patient patient = this.patientService.login(pId, pPassword);
        if (patient != null) {
            Map<String,String> map = new HashMap<>();
            map.put("pName", patient.getPName());
            map.put("pId", String.valueOf(patient.getPId()));
            map.put("pCard", patient.getPCard());
            String token = JwtUtil.getToken(map);
            map.put("token", token);
            //response.setHeader("token", token);
            return ResponseData.success("登录成功", map);
        } else {
            return ResponseData.fail("登录失败，密码或账号错误");
        }
    }
    /**
     * 根据科室查询所有医生信息
     */
    @RequestMapping("findDoctorBySection")
    public ResponseData findDoctorBySection(@RequestParam(value = "dSection") String dSection){
        return ResponseData.success("根据科室查询所有医生信息成功", this.doctorService.findDoctorBySection(dSection));
    }
    /**
     * 增加挂号信息
     */
    @RequestMapping("addOrder")
    @ResponseBody
    public ResponseData addOrder(Orders order, String arId){
        System.out.println(arId);
        if (this.orderService.addOrder(order, arId)) {
            // 挂号成功后自动取号
            try {
                String queueIndex = queueService.takeNumber(order.getOId(), order.getPId(), order.getdId());
                return ResponseData.success("挂号成功，您的排队序号：" + queueIndex);
            } catch(Exception e) {
                return ResponseData.success("插入挂号信息成功");
            }
        }
        return ResponseData.fail("插入挂号信息失败");
    }
    /**
     * 根据pId查询挂号
     */
    @RequestMapping("findOrderByPid")
    public ResponseData findOrderByPid(@RequestParam(value = "pId") int pId){
        return ResponseData.success("返回挂号信息成功", this.orderService.findOrderByPid(pId)) ;
    }

    /**
     * 增加患者信息
     */
    @RequestMapping("addPatient")
    @ResponseBody
    public ResponseData addPatient(Patient patient) {
        Boolean bo = this.patientService.addPatient(patient);
        if (bo) {
            return ResponseData.success("注册成功");
        }
        return ResponseData.fail("注册失败！账号或邮箱已被占用");
    }
    @GetMapping("/pdf")
    public void downloadPDF(HttpServletRequest request, HttpServletResponse response, int oId) throws Exception {
        Orders order = this.orderMapper.findOrderByOid(oId);
        PdfUtil.ExportPdf(request, response, order);
    }
    /**
     * 统计患者男女人数
     */
    @RequestMapping("patientAge")
    public ResponseData patientAge(){
        return  ResponseData.success("统计患者男女人数成功", this.patientService.patientAge());

    }

    /**
     * 设置黑名单
     */
    @GetMapping("/setBlacklist")
    public ResponseData setBlacklist(@RequestParam int pId, @RequestParam int blacklisted) {
        if (this.patientService.setBlacklist(pId, blacklisted == 1)) {
            return ResponseData.success("设置成功");
        }
        return ResponseData.fail("设置失败");
    }

    /**
     * 按标签查询患者
     */
    @GetMapping("/findByTag")
    public ResponseData findByTag(@RequestParam(required = false) String tag) {
        return ResponseData.success("查询成功", this.patientService.findByTag(tag));
    }

    /**
     * Feature 8: 分诊护士现场快速创建患者
     */
    @PostMapping("quickCreate")
    @ResponseBody
    public ResponseData quickCreate(@RequestParam String pName, @RequestParam String pGender,
        @RequestParam String pPhone) {
        Patient patient = new Patient();
        patient.setPName(pName);
        patient.setPGender(pGender);
        patient.setPPhone(pPhone);
        // Auto-generate pId: timestamp based + use existing max pId to avoid collision
        int pId;
        try {
            pId = (int) (System.currentTimeMillis() % 100000);
            if (pId < 10000) pId += 10000;
            // Ensure id is not taken (retry if needed)
            while (patientService.findPatientById(pId) != null) {
                pId = (pId + 1) % 100000;
                if (pId < 10000) pId += 10000;
            }
        } catch(Exception e) {
            pId = (int) (System.nanoTime() % 89999) + 10000;
        }
        patient.setPId(pId);
        // Default password 123456
        patient.setPPassword("123456");
        patient.setPState(1);
        Boolean bo = this.patientService.addPatient(patient);
        if (bo) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("pId", patient.getPId());
            map.put("pName", pName);
            return ResponseData.success("快速创建患者成功", map);
        }
        return ResponseData.fail("快速创建患者失败");
    }
}
