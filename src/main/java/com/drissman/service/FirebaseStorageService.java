package com.drissman.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FirebaseStorageService {

    @Value("${firebase.storage.bucket-name:drissman-default-bucket.appspot.com}")
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
            if (!resource.exists()) {
                log.warn("Le fichier firebase-service-account.json n'a pas été trouvé. Firebase Storage ne fonctionnera pas correctement.");
                return;
            }

            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(bucketName)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application initialized");
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation de Firebase: {}", e.getMessage());
        }
    }

    public Mono<String> uploadFile(FilePart filePart) {
        String fileName = UUID.randomUUID().toString() + "_" + filePart.filename();
        
        return filePart.content().collectList()
                .flatMap(dataBuffers -> Mono.fromCallable(() -> {
                    int size = dataBuffers.stream().mapToInt(org.springframework.core.io.buffer.DataBuffer::readableByteCount).sum();
                    byte[] bytes = new byte[size];
                    int offset = 0;
                    for (org.springframework.core.io.buffer.DataBuffer dataBuffer : dataBuffers) {
                        int length = dataBuffer.readableByteCount();
                        dataBuffer.read(bytes, offset, length);
                        offset += length;
                    }
                    
                    Storage storage = StorageClient.getInstance().bucket().getStorage();
                    BlobId blobId = BlobId.of(bucketName, fileName);
                    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(filePart.headers().getContentType().toString()).build();
                    
                    storage.create(blobInfo, bytes);
                    
                    // On retourne une URL signée valide pour un certain temps, ou une URL publique si le bucket est public
                    // Pour simplifier et pour un usage permanent, on peut générer une URL signée longue durée ou configurer le bucket en public.
                    // Ici on utilise une URL signée d'un an pour l'exemple.
                    return storage.signUrl(blobInfo, 365, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toString();
                }).subscribeOn(Schedulers.boundedElastic()));
    }
}
