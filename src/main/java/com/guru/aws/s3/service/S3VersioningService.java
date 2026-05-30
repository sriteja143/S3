package com.guru.aws.s3.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class S3VersioningService {
    private static final Logger log =
            LoggerFactory.getLogger(S3VersioningService.class);
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3VersioningService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload file and return generated VersionId.
     */
    public String uploadFile(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

        log.info("Uploading file '{}' to bucket '{}'",
                fileName, bucketName);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        PutObjectResponse response = s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );

        String versionId = response.versionId();

        log.info(
                "Successfully uploaded file '{}'. VersionId={}",
                fileName,
                versionId
        );

        return versionId;
    }

    /**
     * Download latest version of a file.
     */
    public byte[] downloadLatestVersion(String fileName) {

        log.info(
                "Downloading latest version of '{}' from bucket '{}'",
                fileName,
                bucketName
        );

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseBytes<GetObjectResponse> response =
                s3Client.getObjectAsBytes(request);

        return response.asByteArray();
    }

    /**
     * Download a specific version.
     */
    public byte[] downloadVersion(
            String fileName,
            String versionId) {

        log.info(
                "Downloading file '{}' version '{}'",
                fileName,
                versionId
        );

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .versionId(versionId)
                .build();

        ResponseBytes<GetObjectResponse> response =
                s3Client.getObjectAsBytes(request);

        return response.asByteArray();
    }

    /**
     * List all version IDs for a file.
     */
    public List<String> getVersionIds(String fileName) {

        ListObjectVersionsRequest request =
                ListObjectVersionsRequest.builder()
                        .bucket(bucketName)
                        .prefix(fileName)
                        .build();

        ListObjectVersionsResponse response =
                s3Client.listObjectVersions(request);

        return response.versions()
                .stream()
                .filter(v -> fileName.equals(v.key()))
                .map(ObjectVersion::versionId)
                .collect(Collectors.toList());
    }

    /**
     * Get full version details.
     */
    public List<ObjectVersion> getVersions(String fileName) {

        ListObjectVersionsRequest request =
                ListObjectVersionsRequest.builder()
                        .bucket(bucketName)
                        .prefix(fileName)
                        .build();

        ListObjectVersionsResponse response =
                s3Client.listObjectVersions(request);

        return response.versions()
                .stream()
                .filter(v -> fileName.equals(v.key()))
                .collect(Collectors.toList());
    }
}