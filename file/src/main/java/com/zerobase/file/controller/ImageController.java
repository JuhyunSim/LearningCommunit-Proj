package com.zerobase.file.controller;

import com.zerobase.file.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageUploadService imageUploadService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestBody MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(imageUploadService.upload(file));
    }
}
