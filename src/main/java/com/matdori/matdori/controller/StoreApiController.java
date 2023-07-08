package com.matdori.matdori.controller;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.service.JokboService;
import com.matdori.matdori.service.StoreService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StoreApiController {

    private final StoreService storeService;
    private final JokboService jokboService;


    // 별점을 가져와야 하므로
    // 족보가 완성되면 작성
    //@GetMapping("/stores/{storeIndex}/info-header")
    //public

    // 정보 탭 조회하기
    @GetMapping("/stores/{storeIndex}/information")
    public ResponseEntity<Response<StoreInformationResponse>> readStoreInformation(@PathVariable("storeIndex") Long id){
        Store store = storeService.findOne(id);
        return ResponseEntity.ok().body(Response.success(new StoreInformationResponse(store.getOpenHours(), store.getPhoneNumber(), store.getAddress(), store.getComment())));
    }

    // 메뉴 탭 조회하기
    @GetMapping("/stores/{storeIndex}/menu")
    public ResponseEntity<Response<List<StoreMenuResponse>>> readStoreMenu(@PathVariable("storeIndex") Long id){
        List<Category> Categories = storeService.findAllCategoryWithMenu(id);

        return ResponseEntity.ok().body(Response.success(Categories.stream().map(c -> new StoreMenuResponse(c))
                .collect(Collectors.toList())));
    }

    // pagecount 추가 필요
    // 족보 탭 조회하기
    @GetMapping("/stores/{storeIndex}/jokbos")
    public ResponseEntity<Response<List<JokboResponse>>> readAllJokbo(@PathVariable("storeIndex") Long storeIndex){
        List<Jokbo> jokbos = storeService.findAllJokbo(storeIndex);

        return ResponseEntity.ok().body(Response.success(jokbos.stream()
                .map(j -> new JokboResponse(j.getId(), j.getTitle(), j.getContents(),j.getJokboImgs(), j.getJokboComments().size()))
                .collect(Collectors.toList())));

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
        private String img_url;
        public MenuDto(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
            this.img_url = menu.getImg_url();
        }
    }

    @Data
    @AllArgsConstructor
    static class StoreInformationResponse{
        OpenHours time;
        String phone_number;
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
        private int commentCnt;

        public JokboResponse(Long jokboId, String title, String contents, List<JokboImg> imgUrl, int commentCnt) {
            this.jokboId = jokboId;
            this.title = title;
            this.contents = contents;
            if(imgUrl.size() != 0)
                this.imgUrl = imgUrl.get(0).getImgUrl();
            this.commentCnt = commentCnt;
        }
    }
}
