package com.matdori.matdori.controller;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.repositoy.Dto.StoreInformationHeader;
import com.matdori.matdori.service.JokboService;
import com.matdori.matdori.service.StoreService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StoreApiController {

    private final StoreService storeService;

    /**
     * 가게 정보 탭 조회하기.
     *
     * 고쳐야 할 부분.
     * 1. findOne에 id가 유효한지 확인
     */
    @GetMapping("/stores/{storeIndex}/information")
    public ResponseEntity<Response<StoreInformationResponse>> readStoreInformation(@PathVariable("storeIndex") Long id){
        Store store = storeService.findOne(id);

        return ResponseEntity.ok().body(
                Response.success(
                        new StoreInformationResponse(
                                store.getOpenHours(),
                                store.getPhoneNumber(),
                                store.getAddress(),
                                store.getComment())));
    }

    /**
     * 메뉴 탭 조회하기.
     *
     * 고쳐야 할 부분
     * 1. Categories 카멜 케이스로 수정.
     *
     */
    @GetMapping("/stores/{storeIndex}/menu")
    public ResponseEntity<Response<List<StoreMenuResponse>>> readStoreMenu(@PathVariable("storeIndex") Long id){
        List<Category> Categories = storeService.findAllCategoryWithMenu(id);

        return ResponseEntity.ok().body(Response.success(Categories.stream().map(c -> new StoreMenuResponse(c))
                .collect(Collectors.toList())));
    }

    /**
     * 가게 족보 탭 조회하기.
     */
    @GetMapping("/stores/{storeIndex}/jokbos")
    public ResponseEntity<Response<List<JokboResponse>>> readAllJokbo(@PathVariable("storeIndex") Long storeIndex,
                                                                      @RequestParam int pageCount){
        List<Jokbo> jokbos = storeService.findAllJokbo(storeIndex, pageCount);

        return ResponseEntity.ok().body(Response.success(jokbos.stream()
                .map(j -> new JokboResponse(
                        j.getId(),
                        j.getTitle(),
                        j.getContents(),
                        j.getJokboImgs(),
                        j.getJokboFavorites().size() ,
                        j.getJokboComments().size()))
                .collect(Collectors.toList())));
    }

    /**
     * 상단 가게 이름 및 별점 표시 부분 조회하기.
     *
     * 고쳐야 할 부분
     * 1. storeIndex가 유효한지 확인.
     */
    @GetMapping("/stores/{storeIndex}/info-header")
    public ResponseEntity<Response<StoreInformationHeader>> readStoreInformationHeader(@PathVariable("storeIndex") Long storeIndex){
        StoreInformationHeader storeInformationHeader = storeService.readStoreInformationHeader(storeIndex);

        return ResponseEntity.ok().body(Response.success(storeInformationHeader));
    }

    @Data
    @AllArgsConstructor
    static class StoreMenuResponse{
        String name;
        List<MenuDto> menus = new ArrayList<>();
        public StoreMenuResponse(Category category) {
            this.name = category.getName();
            this.menus = category.getMenus().stream()
                    .map(c -> new MenuDto(c))
                    .collect(Collectors.toList());
        }
    }

    @Data
    @AllArgsConstructor
     static class MenuDto{
        private String name;
        private Integer price;
        private String imgUrl;

        public MenuDto(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
            this.imgUrl = menu.getImgUrl();
        }
    }

    @Data
    @AllArgsConstructor
    static class StoreInformationResponse{
        OpenHours time;
        String phoneNumber;
        String address;
        String comment;
    }

    @Data
    @AllArgsConstructor
    static class JokboResponse{
        private Long jokboId;
        private String title;
        private String contents;
        private String imgUrl;
        private int favoriteCnt;
        private int commentCnt;

        public JokboResponse(Long jokboId, String title, String contents, List<JokboImg> imgUrl, int favoriteCnt, int commentCnt) {
            this.jokboId = jokboId;
            this.title = title;
            this.contents = contents;
            if(imgUrl.size() != 0)
                this.imgUrl = imgUrl.get(0).getImgUrl();
            this.favoriteCnt = favoriteCnt;
            this.commentCnt = commentCnt;
        }
    }
}
