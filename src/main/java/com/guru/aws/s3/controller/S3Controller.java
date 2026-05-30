package com.guru.aws.s3.controller;

import com.guru.aws.s3.service.S3Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Upload File
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            String fileName = s3Service.uploadFile(file);

            return ResponseEntity.ok(
                    "File uploaded successfully : " + fileName
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Upload failed : " + e.getMessage());
        }
    }

    /**
     * Update / Replace File
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateFile(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            String fileName = s3Service.uploadFile(file);

            return ResponseEntity.ok(
                    "File updated successfully : " + fileName
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Update failed : " + e.getMessage());
        }
    }

    /**
     * Download File
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String fileName
    ) {

        try {

            byte[] fileData =
                    s3Service.downloadFile(fileName);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + fileName
                    )
                    .contentType(
                            MediaType.APPLICATION_OCTET_STREAM
                    )
                    .body(fileData);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .build();
        }
    }
}