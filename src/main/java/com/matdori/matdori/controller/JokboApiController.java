package com.matdori.matdori.controller;

import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.JokboImg;
import com.matdori.matdori.domain.Store;
import com.matdori.matdori.domain.User;
import com.matdori.matdori.service.JokboService;
import com.matdori.matdori.service.S3UploadService;
import com.matdori.matdori.service.StoreService;
import com.matdori.matdori.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class JokboApiController {

    private final JokboService jokboService;
    private final StoreService storeService;
    private final UserService userService;
    private final S3UploadService s3UploadService;

    /**
     * 족보 작성하기.
     */
    @PostMapping("/users/{userIndex}/jokbo")
    public void createJobko(@PathVariable("userIndex") Long userIndex,
                            @Valid CreateJokboRequest request) throws IOException {
        Jokbo jokbo = new Jokbo();
        User user = userService.findOne(userIndex);
        jokbo.setUser(user);

        jokbo.setTotalRating(request.getTotal_rating());
        jokbo.setFlavorRating(request.getFlavor_rating());
        jokbo.setUnderPricedRating(request.getUnder_priced_rating());
        jokbo.setCleanRating(request.getClean_rating());

        Store mappingStore = storeService.findOne(request.getStore_index());
        jokbo.setStore(mappingStore);

        jokbo.setTitle(request.getTitle());
        jokbo.setContents(request.getContents());

        jokboService.createJokbo(jokbo);


        List<MultipartFile> images = request.getImages();
        List<String> imageUrls = s3UploadService.uploadFiles(images);

        if(!CollectionUtils.isEmpty(imageUrls)) {
            List<JokboImg> jokboImgs = new ArrayList<>();
            for(String imgUrl : imageUrls) {
                JokboImg jokboImg = new JokboImg();
                jokboImg.setJokbo(jokbo);
                jokboImg.setImgUrl(imgUrl);

                jokboImgs.add(jokboImg);
            }
            jokboService.createJokboImg(jokboImgs);
        }
    }

    /**
     * 총 족보 개수 조회하기.
     */
    @GetMapping("/jokbo-count")
    public int countAllJokbos() {
        int count = jokboService.countAll();
        return count;
    }

    /**
     * 족보 생성을 위한 Dto
     */
    @Data
    static class CreateJokboRequest {
        private int total_rating;
        private int flavor_rating;
        private int under_priced_rating;
        private int clean_rating;

        private Long store_index;

        private String title;
        private String contents;

        private List<MultipartFile> images;
    }
}
