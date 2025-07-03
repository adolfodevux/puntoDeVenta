package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private static NotificationManager instance;
    private List<Notification> notifications;
    private List<NotificationListener> listeners;
    
    private NotificationManager() {
        this.notifications = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }
    
    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }
    
    public void addNotification(String title, String message, NotificationType type) {
        Notification notification = new Notification(title, message, type);
        notifications.add(0, notification); // Agregar al inicio
        
        // Limitar a 50 notificaciones máximo
        if (notifications.size() > 50) {
            notifications.remove(notifications.size() - 1);
        }
        
        // Notificar a los listeners
        notifyListeners();
        
        // Mostrar toast
        showToast(notification);
    }
    
    public void addSuccessNotification(String message) {
        addNotification("Éxito", message, NotificationType.SUCCESS);
    }
    
    public void addInfoNotification(String message) {
        addNotification("Información", message, NotificationType.INFO);
    }
    
    public void addWarningNotification(String message) {
        addNotification("Advertencia", message, NotificationType.WARNING);
    }
    
    public void addErrorNotification(String message) {
        addNotification("Error", message, NotificationType.ERROR);
    }
    
    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }
    
    public int getUnreadCount() {
        return (int) notifications.stream().filter(n -> !n.isRead()).count();
    }
    
    public void markAsRead(Notification notification) {
        notification.setRead(true);
        notifyListeners();
    }
    
    public void markAllAsRead() {
        notifications.forEach(n -> n.setRead(true));
        notifyListeners();
    }
    
    public void clearAll() {
        notifications.clear();
        notifyListeners();
    }
    
    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (NotificationListener listener : listeners) {
            listener.onNotificationsChanged();
        }
    }
    
    private void showToast(Notification notification) {
        SwingUtilities.invokeLater(() -> {
            ToastNotification toast = new ToastNotification(notification);
            toast.show();
        });
    }
    
    public enum NotificationType {
        SUCCESS("✅", new Color(39, 174, 96)),
        INFO("ℹ️", new Color(52, 152, 219)),
        WARNING("⚠️", new Color(241, 196, 15)),
        ERROR("❌", new Color(231, 76, 60));
        
        private final String icon;
        private final Color color;
        
        NotificationType(String icon, Color color) {
            this.icon = icon;
            this.color = color;
        }
        
        public String getIcon() { return icon; }
        public Color getColor() { return color; }
    }
    
    public static class Notification {
        private String title;
        private String message;
        private NotificationType type;
        private LocalDateTime timestamp;
        private boolean read;
        
        public Notification(String title, String message, NotificationType type) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.timestamp = LocalDateTime.now();
            this.read = false;
        }
        
        // Getters y Setters
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public NotificationType getType() { return type; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
        
        public String getFormattedTime() {
            return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        
        public String getFormattedDate() {
            return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    }
    
    public interface NotificationListener {
        void onNotificationsChanged();
    }
    
    private static class ToastNotification {
        private JWindow window;
        private Notification notification;
        private Timer fadeTimer;
        private float opacity = 1.0f;
        
        public ToastNotification(Notification notification) {
            this.notification = notification;
            createWindow();
        }
        
        private void createWindow() {
            window = new JWindow();
            window.setAlwaysOnTop(true);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(notification.getType().getColor());
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(notification.getType().getColor().darker(), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            JLabel iconLabel = new JLabel(notification.getType().getIcon());
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            iconLabel.setForeground(Color.WHITE);
            
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(notification.getType().getColor());
            
            JLabel titleLabel = new JLabel(notification.getTitle());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel messageLabel = new JLabel(notification.getMessage());
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            messageLabel.setForeground(Color.WHITE);
            messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            textPanel.add(titleLabel);
            textPanel.add(messageLabel);
            
            panel.add(iconLabel, BorderLayout.WEST);
            panel.add(Box.createHorizontalStrut(10), BorderLayout.CENTER);
            panel.add(textPanel, BorderLayout.CENTER);
            
            window.add(panel);
            window.pack();
            
            // Posicionar en la esquina superior derecha
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = screenSize.width - window.getWidth() - 20;
            int y = 50;
            window.setLocation(x, y);
        }
        
        public void show() {
            window.setVisible(true);
            
            // Auto-ocultar después de 3 segundos
            Timer hideTimer = new Timer(3000, e -> fadeOut());
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
        
        private void fadeOut() {
            fadeTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity -= 0.1f;
                    if (opacity <= 0) {
                        window.dispose();
                        fadeTimer.stop();
                    } else {
                        window.setOpacity(opacity);
                    }
                }
            });
            fadeTimer.start();
        }
    }
}
