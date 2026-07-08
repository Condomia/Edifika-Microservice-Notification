package com.edifika.notification.infrastructure.external.firebase;

import com.edifika.notification.domain.services.FirebaseNotificationGateway;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementación del gateway de Firebase que usa FCM v1 HTTP API
 * a través del Firebase Admin SDK.
 *
 * Responsabilidades:
 *  - Construir el objeto Message con título, cuerpo y token destino.
 *  - Enviar la notificación de forma síncrona.
 *  - Manejar errores de Firebase sin propagar excepciones al flujo principal,
 *    para que un fallo en el push no impida persistir la notificación en BD.
 */
@Component
public class FirebaseNotificationGatewayImpl implements FirebaseNotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationGatewayImpl.class);

    @Override
    public void sendPushNotification(String deviceToken, String title, String body) {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(deviceToken)
                .build();

        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[Firebase] Push enviado correctamente. MessageId: {}", messageId);
        } catch (FirebaseMessagingException e) {
            // Logueamos el error pero no lo propagamos: la notificación ya fue guardada en BD.
            // Si el token está expirado/inválido (UNREGISTERED), se puede eliminar de BD aquí en el futuro.
            log.error("[Firebase] Error al enviar push notification al token '{}'. Código: {}. Mensaje: {}",
                    deviceToken, e.getMessagingErrorCode(), e.getMessage());
        }
    }
}
