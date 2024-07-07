package com.umc5th.muffler.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Profile({"prod", "local", "mock"})
public class FirebaseConfig {
    @Value("${firebase.key.path}")
    private String firebaseKeyPath;

    @Value("${firebase.key.scope}")
    private String firebaseKeyScope;

    @PostConstruct
    public void init() throws IOException {
        InputStream inputStream = new ClassPathResource(firebaseKeyPath).getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(firebaseKeyScope);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
           FirebaseApp.initializeApp(options);
        }
    }

}
