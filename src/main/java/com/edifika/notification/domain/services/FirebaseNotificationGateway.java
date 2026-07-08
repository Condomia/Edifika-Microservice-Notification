package com.edifika.notification.domain.services;

/**
 * Puerto (interfaz de dominio) para el envío de notificaciones push via Firebase.
 * La implementación concreta vive en la capa de infraestructura.
 */
public interface FirebaseNotificationGateway {

    /**
     * Envía una notificación push a un dispositivo mediante su FCM token.
     *
     * @param deviceToken token FCM del dispositivo destino
     * @param title       título de la notificación
     * @param body        cuerpo/contenido de la notificación
     */
    void sendPushNotification(String deviceToken, String title, String body);
}
