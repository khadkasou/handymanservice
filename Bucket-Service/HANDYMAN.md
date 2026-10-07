# Bucket-Service in handyman-solution

Copied from erp-solution for file uploads (MinIO/S3). Package: `com.nepbrain.technology`.

- Port: **8006**
- Gateway: `/api/v2/bucket/**`, `/api/v2/public/bucket/**`
- Used for profile images, helpdesk attachments, service images (`remove-file`, multipart uploads via Handyman-Service or direct bucket APIs).

Optional: set `MINIO_BUCKET_NAME=handyman` when pointing at the local MinIO from `Docker/docker-compose.yml`.
