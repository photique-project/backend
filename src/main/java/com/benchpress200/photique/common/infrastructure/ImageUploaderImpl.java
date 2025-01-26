package com.benchpress200.photique.common.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.benchpress200.photique.common.exception.ImageUploaderException;
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
public class ImageUploaderImpl implements ImageUploader {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    @Override
    public String upload(MultipartFile image, String path) {
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
            amazonS3.deleteObject(bucket, imagePath);
        }
    }

    private File convert(MultipartFile image, String uniqueImageName) {
        File convertFile = new File(uniqueImageName); // 빈 파일 만들기

        try {
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(image.getBytes());
                } catch (IOException e) {
                    throw new ImageUploaderException(e.getMessage(),
                            "Image conversion failed: IOException {" + image.getOriginalFilename() + "}");
                }

                return convertFile;
            }

            throw new ImageUploaderException(
                    "Image conversion failed: Duplicated Image {" + image.getOriginalFilename() + "}");

        } catch (IOException e) {
            throw new ImageUploaderException(e.getMessage(),
                    "Image conversion failed: IOException {" + image.getOriginalFilename() + "}");
        }
    }

    private String putS3(File uploadImage, String imageName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, imageName, uploadImage);
        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(bucket, imageName).toString();
    }

    private void removeNewFile(File image) {
        if (!image.delete()) {
            throw new ImageUploaderException("Temporary image remove failed [" + image.getName() + "]");
        }
    }

    private String createS3ImageName(MultipartFile image) {
        String originalFileName = image.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        return uuid + "_" + originalFileName.replaceAll("\\s", "_");
    }
}
