package com.matdori.matdori.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * AWS S3 버킷에 파일을 업로드.
     */
    public List<String> uploadFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<String> imageUrls = new ArrayList<>();
        if(!CollectionUtils.isEmpty(multipartFiles)) {
            for(MultipartFile multipartFile : multipartFiles) {
                // 파일의 확장자 추출
                String originalFileExtension;
                String contentType = multipartFile.getContentType();
                if(ObjectUtils.isEmpty(contentType)) {
                    // 확장자 명이 존재하지 않는 경우
                    break;
                }
                else {
                    // 확장자가 jpg, jpeg, png의 세 가지 중 하나인 경우에만 업로드 가능.
                    if(contentType.contains("image/jpg")) {
                        originalFileExtension = ".jpg";
                    }
                    else if(contentType.contains("image/jpeg")) {
                        originalFileExtension = "./jpg";
                    }
                    else if(contentType.contains("image/png")) {
                        originalFileExtension = "./png";
                    }
                    else break;
                }

                // 이미지 리사이징을 추후에 추가해야 할 수 있음.

                String originalFileName = multipartFile.getOriginalFilename();
                String uniqueFileName = changeFileName(originalFileName);

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(multipartFile.getSize());
                metadata.setContentType(multipartFile.getContentType());

                amazonS3.putObject(bucket, uniqueFileName, multipartFile.getInputStream(), metadata);

                imageUrls.add(amazonS3.getUrl(bucket, uniqueFileName).toString());
            }
        }
        else {

        }
        return imageUrls;
    }

    /**
     * AWS S3 버킷에서 originalFileName의 파일명을 갖는 파일을 삭제.
     */
    public void deleteFile(String originalFileName) {
        amazonS3.deleteObject(bucket, originalFileName);
    }

    /**
     * FileName의 중복을 방지하기 위해 UUID를 이용해 파일에 새로 붙일 랜덤 이름을 생성.
     */
    private String changeFileName(String originalFileName) {
        String uniqueFileName = UUID.randomUUID().toString() + originalFileName;
        return uniqueFileName;
    }
}
