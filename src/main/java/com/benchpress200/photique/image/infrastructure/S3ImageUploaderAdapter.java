package com.benchpress200.photique.image.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.benchpress200.photique.image.domain.ImageUploaderPort;
import com.benchpress200.photique.image.domain.exception.ImageUploaderFileWriteException;
import com.benchpress200.photique.image.domain.exception.S3DeleteException;
import com.benchpress200.photique.image.domain.exception.S3UploadException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageUploaderAdapter implements ImageUploaderPort {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    @Override
    public String upload(
            MultipartFile image,
            String path
    ) {
        String uniqueImageName = createS3ImageName(image);
        String uniqueImagePath = path + "/" + uniqueImageName;
        File uploadImage = convert(image, uniqueImageName);
        String uploadImageUrl = putS3(uploadImage, uniqueImagePath);

        removeNewFile(uploadImage);

        return uploadImageUrl;
    }


    @Override
    public String update(
            MultipartFile newImage,
            String oldPath,
            String newPath
    ) {
        delete(oldPath);

        return upload(newImage, newPath);
    }

    @Override
    public void delete(String path) {
        if (path != null) {
            String imagePath = path.substring(path.indexOf("com/") + 4);

            try {
                amazonS3.deleteObject(bucket, imagePath);
            } catch (RuntimeException e) {
                throw new S3DeleteException(e.getMessage(), imagePath);
            }

        }
    }

    private File convert(
            MultipartFile image,
            String uniqueImageName
    ) {
        File convertFile = new File(uniqueImageName); // 빈 파일 만들기

        try {
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(image.getBytes());
                } catch (IOException e) {
                    throw new ImageUploaderFileWriteException();
                }

                return convertFile;
            }

            throw new ImageUploaderFileWriteException();

        } catch (IOException e) {
            throw new ImageUploaderFileWriteException();
        }
    }

    private String putS3(
            File uploadImage,
            String imageName
    ) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, imageName, uploadImage);
        try {
            amazonS3.putObject(putObjectRequest);
            return amazonS3.getUrl(bucket, imageName).toString();

        } catch (RuntimeException e) { // S3 업로드 예외 캐치 후 GlobalExceptionHandler에서 처리
            throw new S3UploadException(e.getMessage());
        }
    }

    private void removeNewFile(File image) {
        int count = 0;

        // 로컬환경에 임시로 저장된 이미지 삭제에 실패하면 5번까지 재시도
        while (!image.delete() && count < 5) {
            count++;
        }

        // 5번의 재시도도 실패한다면 로깅
        log.error("Image cleanup failed");
    }

    private String createS3ImageName(MultipartFile image) {
        String originalFileName = image.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        return uuid + "_" + originalFileName.replaceAll("\\s", "_");
    }
}
