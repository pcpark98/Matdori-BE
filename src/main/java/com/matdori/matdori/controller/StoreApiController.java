package com.matdori.matdori.controller;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.repositoy.Dto.StoreInformationHeader;
import com.matdori.matdori.repositoy.Dto.StoreListByCategory;
import com.matdori.matdori.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "가게 API", description = "가게와 관련된 API들")
@RestController
@RequiredArgsConstructor
public class StoreApiController {

    private final StoreService storeService;

    /**
     * 가게 정보 탭 조회하기.
     *
     */
    @Operation(summary = "가게 정보 탭 조회 API", description = "가게 정보 탭을 조회합니다.")
    @Parameter(name = "storeIndex", description = "가게 id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "storeIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게에 대한 조회 시도. storeIndex 값이 잘못됨.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/stores/{storeIndex}/information")
    public ResponseEntity<Response<StoreInformationResponse>> readStoreInformation(@PathVariable("storeIndex") Long id){
        Store store = storeService.findOne(id);

        return ResponseEntity.ok().body(
                Response.success(
                        new StoreInformationResponse(
                                store.getOpenHours(),
                                store.getPhoneNumber(),
                                store.getAddress()
                                )));
    }

    /**
     * 메뉴 탭 조회하기.
     *
     */
    @Operation(summary = "메뉴 탭 조회 API", description = "가게 정보의 메뉴 탭을 조회합니다.")
    @Parameter(name = "storeIndex", description = "가게 id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "storeIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게에 대한 조회 시도. storeIndex 값이 잘못됨.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/stores/{storeIndex}/menu")
    public ResponseEntity<Response<List<StoreMenuResponse>>> readStoreMenu(@PathVariable("storeIndex") Long id){
        List<Category> categories = storeService.findAllCategoryWithMenu(id);

        return ResponseEntity.ok().body(Response.success(categories.stream().map(c -> new StoreMenuResponse(c))
                .collect(Collectors.toList())));
    }

    /**
     * 가게 족보 탭 조회하기.
     */
    @Operation(summary = "가게 족보 탭 조회 API", description = "가게 정보의 족보 탭을 조회합니다.")
    @Parameters({
            @Parameter(name = "storeIndex", description = "가게 id"),
            @Parameter(name = "pageCount", description = "페이지")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "storeIndex 또는 pageCount 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게에 대한 조회 시도. storeIndex 값이 잘못됨.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
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
     */
    @Operation(summary = "가게 이름 및 별점 표시 조회 API", description = "가게 정보 상단의 가게 이름 및 별점 표시 부분을 조회합니다.")
    @Parameter(name = "storeIndex", description = "가게 id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "storeIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게에 대한 조회 시도. storeIndex 값이 잘못됨.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/stores/{storeIndex}/info-header")
    public ResponseEntity<Response<StoreInformationHeader>> readStoreInformationHeader(@PathVariable("storeIndex") Long storeIndex){
        StoreInformationHeader storeInformationHeader = storeService.readStoreInformationHeader(storeIndex);

        return ResponseEntity.ok().body(Response.success(storeInformationHeader));
    }

    @GetMapping("/stores")
    public ResponseEntity<Response<List<StoreListByCategory>>> readStoresByCategory(@RequestParam("category")String category,
                                                                                    @RequestParam("pageCount")int startIndex){
        List<StoreListByCategory> stores = storeService.findByCategory(category, startIndex);

        return ResponseEntity.ok().body(Response.success(stores));
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
        private String price;
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
