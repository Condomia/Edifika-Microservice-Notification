package com.edifika.notification.infrastructure.external.firebase.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Configuración de Firebase Admin SDK.
 * Inicializa el FirebaseApp al arrancar Spring Boot usando las credenciales
 * del Service Account definidas en application.properties o mediante una variable de entorno.
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials:}")
    private String firebaseCredentialsJson;

    @Value("${firebase.service-account-path}")
    private String serviceAccountPath;

    private final ResourceLoader resourceLoader;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Evitar inicialización doble si ya existe una instancia (útil en tests o hot-reload)
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        GoogleCredentials credentials;

        // 1. Intentar cargar desde la variable de entorno que contiene el JSON de credenciales directamente
        if (firebaseCredentialsJson != null && !firebaseCredentialsJson.trim().isEmpty()) {
            try (InputStream stream = new ByteArrayInputStream(firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8))) {
                credentials = GoogleCredentials.fromStream(stream);
            }
        } else {
            // 2. Si no hay variable con el JSON, intentar cargar desde la ruta configurada (archivo/classpath)
            Resource resource = resourceLoader.getResource(serviceAccountPath);
            if (!resource.exists()) {
                throw new IOException("No se encontró el archivo de credenciales de Firebase en la ruta: " + serviceAccountPath + 
                        ". Por favor configure la variable de entorno FIREBASE_CREDENTIALS con el contenido del JSON o asegúrese de que el archivo exista.");
            }
            try (InputStream stream = resource.getInputStream()) {
                credentials = GoogleCredentials.fromStream(stream);
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
