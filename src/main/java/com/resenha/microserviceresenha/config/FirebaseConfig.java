package com.resenha.microserviceresenha.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Autowired
    private Environment environment;

    @Primary
    @Bean
    public void firebaseInit(){

        try{
            InputStream inputStream = null;
            String firebaseConfig = environment.getProperty("firebase.config");

            Base64.Decoder decoder = Base64.getDecoder();
            final byte[] decode = decoder.decode(firebaseConfig);
            inputStream = new ByteArrayInputStream(decode);

            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();
            if(FirebaseApp.getApps().isEmpty()){
                FirebaseApp.initializeApp(firebaseOptions);
            }
            log.info("Firebase initialize");
        } catch (IOException e) {
            log.error("FirebaseConfig Error:: ", e.getLocalizedMessage());
        }
    }
}
