package com.guru.aws.s3.controller;

import com.guru.aws.s3.service.S3VersioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/version/documents")
public class S3VersioningController {

    private final S3VersioningService s3VersioningService;

    public S3VersioningController(S3VersioningService s3VersioningService) {
        this.s3VersioningService = s3VersioningService;
    }

    /**
     * Upload a file and return VersionId.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        String versionId = s3VersioningService.uploadFile(file);

        return ResponseEntity.ok(
                "File uploaded successfully. VersionId: " + versionId);
    }

    /**
     * Download latest version of a file.
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadLatestVersion(
            @PathVariable String fileName) {

        byte[] data =
                s3VersioningService.downloadLatestVersion(fileName);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    /**
     * Download a specific version of a file.
     */
    @GetMapping("/download/{fileName}/{versionId}")
    public ResponseEntity<byte[]> downloadVersion(
            @PathVariable String fileName,
            @PathVariable String versionId) {

        byte[] data =
                s3VersioningService.downloadVersion(
                        fileName,
                        versionId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    /**
     * Get all version IDs for a file.
     */
    @GetMapping("/versions/{fileName}")
    public ResponseEntity<List<String>> getVersionIds(
            @PathVariable String fileName) {

        return ResponseEntity.ok(
                s3VersioningService.getVersionIds(fileName));
    }

    /**
     * Get detailed version information.
     */
    @GetMapping("/details/{fileName}")
    public ResponseEntity<List<ObjectVersion>> getVersions(
            @PathVariable String fileName) {

        return ResponseEntity.ok(
                s3VersioningService.getVersions(fileName));
    }
}