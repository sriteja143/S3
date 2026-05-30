# AWS S3 File Management Service

A Spring Boot application that integrates with AWS S3 for file upload, download, and object versioning.

## Features

* Upload files to AWS S3
* Download files from AWS S3
* S3 Object Versioning Support
* Retrieve specific file versions
* List all available versions of a file
* Spring Boot 3.x
* AWS SDK v2

---

## Prerequisites

* Java 17+
* Gradle
* AWS Account
* S3 Bucket
* AWS Access Key and Secret Key

---

## Configuration

### application.yml

```yaml
aws:
  access-key: YOUR_ACCESS_KEY
  secret-key: YOUR_SECRET_KEY
  region: us-east-1
  s3:
    bucket: your-bucket-name
```

Or

### application.properties

```properties
aws.access-key=YOUR_ACCESS_KEY
aws.secret-key=YOUR_SECRET_KEY
aws.region=us-east-1
aws.s3.bucket=your-bucket-name
```

---

## S3 Bucket Versioning

To preserve file history, enable versioning on the bucket.

### AWS Console

1. Open AWS S3 Console
2. Select Bucket
3. Go to Properties
4. Find Bucket Versioning
5. Click Edit
6. Enable Versioning

### Important

Objects uploaded before enabling versioning will have a VersionId of `null`.

Example:

```text
Versioning Disabled
------------------
test.pdf

Enable Versioning

Versioning Enabled
------------------
test.pdf (VersionId = null)

Upload Again

test.pdf (VersionId = v1)
```

---

## Project Structure

```text
src/main/java
|
+-- controller
|   +-- S3Controller.java
|   +-- S3VersioningController.java
|
+-- service
|   +-- S3Service.java
|   +-- S3VersioningService.java
|
+-- config
|   +-- AwsConfig.java
```

---

## APIs

### Upload File

```http
POST /api/v1/s3/upload
```

Request:

```form-data
file=<file>
```

Response:

```text
File uploaded successfully
```

---

### Download File

```http
GET /api/v1/s3/download/{fileName}
```

Example:

```http
GET /api/v1/s3/download/sample.pdf
```

---

## Versioning APIs

### Upload New Version

```http
POST /api/v1/s3/version/upload
```

Request:

```form-data
file=<file>
```

Response:

```text
VersionId: abc123xyz
```

---

### Download Latest Version

```http
GET /api/v1/s3/version/download/{fileName}
```

Example:

```http
GET /api/v1/s3/version/download/sample.pdf
```

---

### Download Specific Version

```http
GET /api/v1/s3/version/download/{fileName}/{versionId}
```

Example:

```http
GET /api/v1/s3/version/download/sample.pdf/abc123xyz
```

---

### List Version IDs

```http
GET /api/v1/s3/version/versions/{fileName}
```

Example Response:

```json
[
  "abc123xyz",
  "def456xyz",
  "null"
]
```

---

### List Version Details

```http
GET /api/v1/s3/version/details/{fileName}
```

Response:

```json
[
  {
    "versionId": "abc123xyz",
    "latest": true
  },
  {
    "versionId": "def456xyz",
    "latest": false
  }
]
```

---

## Build Project

```bash
./gradlew clean build
```

Generated artifact:

```text
build/libs/aws-s3-demo.jar
```

---

## Run Application

```bash
java -jar build/libs/aws-s3-demo.jar
```

Application starts on:

```text
http://localhost:8080
```

---

## Common Issues

### AccessDenied

Verify:

* IAM User has S3 permissions
* Bucket policy allows access
* Correct AWS credentials are configured

### NoSuchKey

Occurs when the requested file does not exist in the bucket.

### VersionId is null

This is expected for files uploaded before bucket versioning was enabled.

---

## Required IAM Permissions

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject",
        "s3:ListBucket",
        "s3:ListBucketVersions"
      ],
      "Resource": "*"
    }
  ]
}
```

---

## Technology Stack

* Java 17
* Spring Boot 4
* AWS SDK v2
* Gradle
* Amazon S3

---

## Future Enhancements

* Pre-Signed URLs
* Multipart Upload Support
* File Metadata Management
* Lifecycle Policies
* Object Locking
* Encryption using KMS
* CloudFront Integration
