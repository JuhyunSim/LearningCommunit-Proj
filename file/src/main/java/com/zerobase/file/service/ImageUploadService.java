package com.zerobase.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final AmazonS3Client s3Client;

    @Value("${s3.bucket}")
    private String buckets;

    public String upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileName = changeFileName(originalFilename);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        s3Client.putObject(buckets, fileName, file.getInputStream(), objectMetadata);
        return s3Client.getUrl(buckets, fileName).toString();
    }

    private String changeFileName(String originalFilename) {
        // 확장자를 추출.
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(dotIndex);
        }

        // UUID를 사용하여 고유한 파일명을 생성 (원본파일명은 깨짐)
        String uuid = UUID.randomUUID().toString();
        return uuid + extension;
    }
}
