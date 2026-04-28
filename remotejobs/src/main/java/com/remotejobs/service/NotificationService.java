package com.remotejobs.service;

import com.remotejobs.entity.Notification;
import com.remotejobs.entity.User;
import com.remotejobs.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(User user, String message, String type, Long referenceId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setReferenceId(referenceId);
        return notificationRepository.save(notification);
    }

    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> findUnreadByUser(User user) {
        return notificationRepository.findByUserAndReadFalse(user);
    }

    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markAllRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public void markRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
