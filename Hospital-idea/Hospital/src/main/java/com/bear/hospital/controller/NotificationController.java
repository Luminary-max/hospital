package com.bear.hospital.controller;

import com.bear.hospital.service.NotificationService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("findByPatient")
    public ResponseData findByPatient(@RequestParam int pId) {
        return ResponseData.success("查询成功", this.notificationService.findByPatient(pId));
    }

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size) {
        return ResponseData.success("查询成功", this.notificationService.findAll(pageNumber, size));
    }

    @RequestMapping("markRead")
    public ResponseData markRead(@RequestParam int nId) {
        if (this.notificationService.markRead(nId))
            return ResponseData.success("标记已读成功");
        return ResponseData.fail("标记失败");
    }

    @RequestMapping("unreadCount")
    public ResponseData unreadCount(@RequestParam int pId) {
        return ResponseData.success("查询成功", this.notificationService.unreadCount(pId));
    }
}
