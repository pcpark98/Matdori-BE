package com.matdori.matdori.controller;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.repositoy.Dto.JokboRichStore;
import com.matdori.matdori.repositoy.Dto.MatdoriPick;
import com.matdori.matdori.repositoy.Dto.StoreListByDepartment;
import com.matdori.matdori.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 족보 메인 페이지에 들어갈 모든 api에 이미지 캐싱하기. -> s3에서 갖고오지 말고 ec2에 저장해두기.
 * 랜덤인 맛도리 pick 제외.
 * http 헤더에 since last modified 옵션 넣어서 프론트에서 요청 다시 안 하고 이미 저장해둔거 쓸 수 있게.
 * department를 enum으로 빼자.
 */
@Tag(name = "족보 API", description = "족보와 관련된 API들")
@RestController
@RequiredArgsConstructor
public class JokboApiController {

    private final JokboService jokboService;
    private final StoreService storeService;
    private final UserService userService;

    /**
     * 족보 작성하기.
     */
    @Operation(summary = "족보 작성하기 API", description = "족보를 작성합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id", required = true),
            @Parameter(name = "flavorRating", description = "맛 평점"),
            @Parameter(name = "underPricedRating", description = "가성비 평점"),
            @Parameter(name = "cleanRating", description = "청결 평점"),
            @Parameter(name = "storeIndex", description = "가게 id"),
            @Parameter(name = "title", description = "족보 제목"),
            @Parameter(name = "contents", description = "족보 내용"),
            @Parameter(name = "images", description = "이미지"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게(NOT_EXISTED_STORE)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
    @PostMapping("/users/{userIndex}/jokbo")
    public ResponseEntity<Response<Void>> createJokbo(
            @PathVariable("userIndex") @NotNull Long userIndex,
            @RequestBody @Valid CreateJokboRequest request) throws IOException {

        // 세션 체크하기.
        AuthorizationService.checkSession(userIndex);

        // 족보에 대한 기본 정보 생성
        Jokbo jokbo = new Jokbo();
        User user = userService.findOne(userIndex);
        jokbo.setUser(user);

        jokbo.setFlavorRating(request.getFlavorRating());
        jokbo.setUnderPricedRating(request.getUnderPricedRating());
        jokbo.setCleanRating(request.getCleanRating());

        Store mappingStore = storeService.findOne(request.getStoreIndex());
        jokbo.setStore(mappingStore);

        jokbo.setTitle(request.getTitle());
        jokbo.setContents(request.getContents());

        List<MultipartFile> images = request.getImages();

        jokboService.createJokbo(jokbo, images);

        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 족보 내용 조회하기
     */
    @Operation(summary = "족보 내용 조회하기", description = "단일 족보의 상세 내용을 조회합니다.")
    @Parameter(name = "jokboIndex", description = "족보 id", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 족보(NOT_EXISTED_JOKBO)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/jokbos/{jokboIndex}")
    public ResponseEntity<Response<JokboContentsResponse>>
            readJokbo(
                    @RequestHeader("userIndex") @NotNull Long userId,
                    @PathVariable("jokboIndex") @NotNull Long jokboId) {
        Jokbo jokbo = jokboService.findOne(jokboId);
        Long jokboFavoriteId = userService.getFavoriteJokboId(userId, jokboId);
        com.matdori.matdori.repositoy.Dto.StoreRatings ratings = storeService.getAllRatings(jokbo.getStore());
        List<String> jokboImgUrls = jokboService.getImageUrls(jokbo.getJokboImgs());

        return ResponseEntity.ok().body(
                Response.success(
                        new JokboContentsResponse(
                                jokbo.getStore().getId(),
                                jokbo.getStore().getName(),
                                jokbo.getStore().getImgUrl(),
                                Math.round(ratings.getTotalRating() * 100) / 100.0,
                                Math.round(ratings.getFlavorRating() * 100) / 100.0,
                                Math.round(ratings.getUnderPricedRating() * 100) / 100.0,
                                Math.round(ratings.getCleanRating() * 100) / 100.0,
                                jokbo.getTitle(),
                                jokbo.getUser().getNickname(),
                                jokbo.getContents(),
                                jokboFavoriteId,
                                jokbo.getCreatedAt(),
                                jokboImgUrls
                        )
                )
        );
    }

    /**
     * 내가 쓴 족보 삭제하기
     */
    @Operation(summary = "내가 쓴 족보 삭제 API", description = "족보 게시글을 삭제합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "DeleteJokboRequest", description = "삭제할 족보둘의 id 리스트")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 족보(NOT_EXISTED_JOKBO) <br> 존재하지 않는 족보 이미지(NOT_EXISTED_JOKBO_IMG)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/users/{userIndex}/jokbos")
    public ResponseEntity<Response<Void>> deleteJokbo (
            @PathVariable("userIndex") Long userId,
            @RequestBody @Valid DeleteJokboRequest request) {

        // 세션 체크하기
        AuthorizationService.checkSession(userId);

        List<Jokbo> selectedJokboList = jokboService.findAllById(request.getJokboIdList());
        List<JokboImg> jokboImgs = jokboService.findAllImgById(selectedJokboList);
        List<String> imgUrls = jokboService.getImageUrls(jokboImgs);

        jokboService.deleteJokbo(userId, selectedJokboList, imgUrls);

        return ResponseEntity.ok().body(
                Response.success(null)
        );
    }

    /**
     * 족보에 댓글 등록하기.
     */
    @Operation(summary = "족보 댓글 작성 API", description = "족보에 댓글을 작성합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "jokboIndex", description = "족보 id"),
            @Parameter(name ="userIndex", description = "유저 id"),
            @Parameter(name ="contents", description = "댓글")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 족보(NOT_EXISTED_JOKBO)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/jokbos/{jokboIndex}/comment")
    public ResponseEntity<Response<Void>> createJokboComment(
            @PathVariable("jokboIndex") Long jokboId,
            @RequestBody @Valid CreateJokboCommentRequest request) {

        // 세션 체크하기
        AuthorizationService.checkSession(request.getUserIndex());

        JokboComment jokboComment = new JokboComment();

        Jokbo jokbo = jokboService.findOne(jokboId);
        jokboComment.setJokbo(jokbo);

        User user = userService.findOne(request.getUserIndex());
        jokboComment.setUser(user);

        jokboComment.setContents(request.getContents());
        jokboComment.setIsDeleted(false);

        jokboService.createJokboComment(jokboComment);

        return ResponseEntity.ok().body(
                Response.success(null)
        );
    }

    /**
     * 족보 글에 달린 모든 댓글 조회하기.
     *
     * 고쳐야 할 부분
     * 1. 페이징 처리
     * 2. 정렬 처리 구현 필요
     */
    @Operation(summary = "족보에 달린 모든 댓글 조회 API", description = "족보 게시글에 달린 모든 댓글들을 조회합니다.")
    @Parameters({
            @Parameter(name = "jokboIndex", description = "족보 id", required = true),
            @Parameter(name = "order", description = "정렬값", required = true),
            @Parameter(name = "pageCount", description = "시작페이지 : 1 , 한 페이지 당 15개씩 응답", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 족보(NOT_EXISTED_JOKBO)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/jokbos/{jokboIndex}/comments")
    public ResponseEntity<Response<ReadJokboCommentResponse>> getAllJokboComments (
            @PathVariable("jokboIndex") Long jokboId,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "pageCount") Long cursor) {

        Boolean hasNext = true;
        List<JokboComment> jokboComments = jokboService.getAllJokboComments(jokboId, cursor);
        List<JokboCommentResponse> comment_list = jokboComments.stream()
                .map(c -> new JokboCommentResponse(
                        c.getId(),
                        c.getCreatedAt(),
                        c.getContents(),
                        c.getIsDeleted(),
                        c.getUser().getId(),
                        c.getUser().getNickname()))
                .collect(Collectors.toList());

        if(comment_list.size() != 14) {
            hasNext = false;
        }

        return ResponseEntity.ok()
                .body(Response.success(new ReadJokboCommentResponse(
                        comment_list,
                        comment_list.size(),
                        hasNext
                )));
    }

    /**
     * 내가 쓴 댓글 삭제하기.
     */
    @Operation(summary = "내가 쓴 댓글 삭제 API", description = "유저 본인이 작성한 댓글을 삭제합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id", required = true),
            @Parameter(name = "jokboCommentIdList", description = "삭제할 댓글들의 id 리스트", required = true),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 족보(?) <br> 존재하지 않는 댓글(NOT_EXISTED_JOKBO_COMMENT)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/users/{userIndex}/comments")
    public ResponseEntity<Response<Void>> deleteJokboComment (
            @PathVariable("userIndex") Long userId,
            @RequestBody @Valid DeleteJokboCommentRequest request) {

        // 세션 체크하기.
        AuthorizationService.checkSession(userId);

        List<JokboComment> selectedJokboCommentList = jokboService.getSelectedJokboComments(userId, request.getJokboCommentIdList());

        jokboService.deleteJokboComment(userId, selectedJokboCommentList);

        return ResponseEntity.ok().body(
                Response.success(null)
        );
    }

    /**
     * 총 족보 개수 조회하기.
     */
    @Operation(summary = "총 족보 개수 조회 API", description = "모든 가게에 달린 족보의 총 개수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/jokbo-count")
    public ResponseEntity<Response<CountAllJokboResponse>> countAllJokbos() {
        int count = jokboService.countAll();

        return ResponseEntity.ok()
                .body(Response.success(
                        new CountAllJokboResponse(count)));
    }

    /**
     * 학과별 추천 식당 조회하기
     */
    @Operation(summary = "학과별 추천 식당 조회 API", description = "유저가 소속된 학과의 학생들이 족보를 많이 작성한 식당들을 조회합니다.")
    @Parameter(name = "department", description = "학과", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 학과(NOT_EXISTED_DEPARTMENT)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/stores/department")
    public ResponseEntity<Response<List<DepartmentRecommendationResponse>>> readDepartmentRecommendation(
            @RequestParam(value = "department") String department) {

        List<StoreListByDepartment> storeList = jokboService.getStoreListByDepartment(department);
        List<DepartmentRecommendationResponse> responseList = storeList.stream()
                .map(s -> new DepartmentRecommendationResponse(
                        s.getStoreIndex(),
                        s.getName(),
                        s.getImgUrl(),
                        storeService.getTotalRating(s.getStoreIndex())
                )).collect(Collectors.toList());

        return ResponseEntity.ok().body(
                Response.success(
                        responseList
                )
        );
    }

    /**
     * 맛도리 픽 가게 리스트 조회하기.
     *
     * 고쳐야 할 부분
     * 1. 랜덤이 아님
     */
    @Operation(summary = "맛도리 픽 가게 리스트 조회 API", description = "맛도리 픽이라는 이름으로 학과별 추천으로 선정되지 않은 가게들 중에서 랜덤으로 세 곳을 조회합니다.")
    @Parameter(name = "department", description = "학과", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 학과(NOT_EXISTED_DEPARTMENT)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/stores/matdori-pick")
    public ResponseEntity<Response<List<MatdoriPick>>> readMatdoriPick(
            @RequestParam(value = "department") String department) {

        List<MatdoriPick> matdoriPick = jokboService.getMatdoriPick(department);

        return ResponseEntity.ok().body(
                Response.success(
                        matdoriPick
                )
        );
    }

    /**
     * 족보 부자 가게 리스트 조회하기.
     */
    @Operation(summary = "족보 부자 가게 리스트 조회 API", description = "최근 30일 동안 족보가 가장 많이 달린 가게들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/stores/best")
    public ResponseEntity<Response<List<JokboRichStore>>> readJokboRichStores() {
        List<JokboRichStore> jokboRichStores = jokboService.getJokboRichStores();

        return ResponseEntity.ok().body(
                Response.success(
                        jokboRichStores
                )
        );
    }

    /**
     * 족보 생성을 위한 DTO
     */
    @Data
    static class CreateJokboRequest {
        @NotNull
        private int flavorRating;

        @NotNull
        private int underPricedRating;

        @NotNull
        private int cleanRating;

        @NotNull
        private Long storeIndex;

        @NotBlank
        private String title;

        @NotBlank
        private String contents;

        private List<MultipartFile> images;
    }

    /**
     * 족보 조회하기의 응답을 위한 DTO
     */
    @Data
    @AllArgsConstructor
    static class JokboContentsResponse {
        Long storeIndex;
        String storeName;
        String storeImgUrl;
        double totalRating;
        double flavorRating;
        double underPricedRating;
        double cleanRating;

        String title;
        String nickname;
        String contents;
        Long jokboFavoriteId;
        LocalDateTime createdAt;

        List<String> jokboImgUrlList;
    }

    /**
     * 족보 댓글 작성을 위한 DTO
     */
    @Data
    static class CreateJokboCommentRequest {
        @NotNull
        private Long userIndex;

        @NotBlank
        private String contents;
    }

    /**
     * 족보 댓글의 정보를 조회하기 위한 DTO
     */
    @Data
    @AllArgsConstructor
    static class JokboCommentResponse {
        Long commentIndex;
        LocalDateTime createdAt;
        String contents;
        boolean checkDeleted;

        Long userIndex;
        String nickname;
    }

    /**
     * 족보에 달린 모든 댓글 조회하기의 응답을 위한 DTO
     */
    @Data
    @AllArgsConstructor
    static class ReadJokboCommentResponse {
        List<JokboCommentResponse> commentList;
        int commentCnt;
        private Boolean hasNext;
    }

    /**
     * 족보의 총 개수 조회하기의 응답을 위한 DTO
     */
    @Data
    @AllArgsConstructor
    static class CountAllJokboResponse {
        private int count;
    }

    /**
     * 학과별 추천 식당 조회하기의 응답을 위한 DTO
     */
    @Data
    @AllArgsConstructor
    static class DepartmentRecommendationResponse {
        private Long storeIndex;
        private String name;
        private String imgUrl;
        private Double totalRating;
    }

    /**
     * 족보 삭제 요청을 위한 DTO
     */
    @Data
    static class DeleteJokboRequest {
        private List<Long> jokboIdList;
    }

    /**
     * 족보 댓글 삭제 요청을 위한 DTO
     */
    @Data
    static class DeleteJokboCommentRequest {
        private List<Long> jokboCommentIdList;
    }
}
