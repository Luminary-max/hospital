package com.bear.hospital.service;

import com.bear.hospital.pojo.Notification;
import java.util.HashMap;
import java.util.List;

public interface NotificationService {
    List<Notification> findByPatient(Integer pId);
    HashMap<String, Object> findAll(int pageNumber, int size);
    Boolean markRead(Integer nId);
    int unreadCount(Integer pId);
}
