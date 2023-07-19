package com.matdori.matdori.controller;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.repositoy.Dto.JokboRichStore;
import com.matdori.matdori.repositoy.Dto.MatdoriPick;
import com.matdori.matdori.repositoy.Dto.StoreListByDepartment;
import com.matdori.matdori.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 족보 메인 페이지에 들어갈 모든 api에 이미지 캐싱하기. -> s3에서 갖고오지 말고 ec2에 저장해두기.
 * 랜덤인 맛도리 pick 제외.
 * http 헤더에 since last modified 옵션 넣어서 프론트에서 요청 다시 안 하고 이미 저장해둔거 쓸 수 있게.
 * department를 enum으로 빼자.
 */
@RestController
@RequiredArgsConstructor
public class JokboApiController {

    private final JokboService jokboService;
    private final StoreService storeService;
    private final UserService userService;
    private final S3UploadService s3UploadService;

    /**
     * 족보 작성하기.
     *
     * 고쳐야 할 부분
     * 1. 세션 체크하고 시작하기.
     * 2. storeIndex 유효한지 확인.
     * 3. 프론트랑 연결할 때, request에 @RequestBody 붙이기.
     * 4. 족보 테이블에 넣고 이미지를 넣을 차례에 이미지를 넣다가 에러가 발생한 경우, 족보 테이블 롤백이 필요하다.
     * 5. S3에 이미지 넣고, 족보 이미지 테이블에 넣을 차례에 에러가 발생하면, 족보 이미지 테이블에 롤백이 필요하다.
     * 6. 족보 테이블에 저장, S3에 이미지 저장, 족보 이미지 테이블에 저장 -> 이 세가지를 한 트랜젝션에 묶어라.
     */
    @PostMapping("/users/{userIndex}/jokbo")
    public void createJokbo(@PathVariable("userIndex") Long userIndex,
                            @Valid CreateJokboRequest request) throws IOException {

        // 세션 체크하기.

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

        jokboService.createJokbo(jokbo);

        // 족보 테이블에 넣고 이미지를 넣을 차례에 이미지를 넣다가 에러가 발생한 경우, 족보 테이블 롤백이 필요하다.

        List<MultipartFile> images = request.getImages();
        List<String> imageUrls = s3UploadService.uploadFiles(images);

        // S3에 이미지 넣고, 족보 이미지 테이블에 넣을 차례에 에러가 발생하면, 족보 이미지 테이블에 롤백이 필요하다.

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
     * 족보 내용 조회하기
     *
     * 고쳐야 할 부분
     * 1. 족보 id 유효한지 확인
     * 2. 이미지 url들 조회할 때, service 한 번 더 호출하지 말고, jokbo.get 해서 가져오기.
     */
    @GetMapping("/jokbos/{jokboIndex}")
    public ResponseEntity<Response<JokboContentsResponse>>
            readJokbo(@PathVariable("jokboIndex") Long id) {
        Jokbo jokbo = jokboService.findOne(id);
        List<String> jokboImgUrls = jokboService.getImageUrls(jokbo.getJokboImgs());

        return ResponseEntity.ok().body(
                Response.success(
                        new JokboContentsResponse(
                                jokbo.getStore().getId(),
                                jokbo.getStore().getName(),
                                jokbo.getStore().getImgUrl(),
                                jokbo.getTitle(),
                                jokbo.getUser().getNickname(),
                                jokbo.getContents(),
                                jokboImgUrls
                        )
                )
        );
    }

    /**
     * 내가 쓴 족보 삭제하기
     *
     * 고쳐야 할 부분
     * 1. jokboId가 유효한지 확인
     * 2. imgUrls 가져올 때, jokboService 호출하지 말고, jokboImgs.get(index).~ 로 가져오기.
     * 3. 족보를 삭제한 이후에, S3에 저장된 이미지를 삭제하려다가 오류가 발생하면 롤백이 필요함. -> 한 트랜젝션으로 묶기
     */
    @DeleteMapping("/users/{userIndex}/jokbos/{jokboIndex}")
    public ResponseEntity<Response<Void>> deleteJokbo (
            @PathVariable("userIndex") Long userId,
            @PathVariable("jokboIndex") Long jokboId) {

        // 세션 체크하기
        AuthorizationService.checkSession(userId);

        Jokbo jokbo = jokboService.findOne(jokboId);
        List<JokboImg> jokboImgs = jokbo.getJokboImgs();
        List<String> imgUrls = jokboService.getImageUrls(jokboImgs);

        jokboService.deleteJokbo(jokbo, userId, jokboImgs);

        // 족보를 삭제한 이후에, S3에 저장된 이미지를 삭제하려다가 오류가 발생하면 롤백이 필요함.

        s3UploadService.deleteFile(imgUrls);

        return ResponseEntity.ok().body(
                Response.success(null)
        );
    }

    /**
     * 족보에 댓글 등록하기.
     *
     * 고쳐야 할 부분
     * 1. jokboId가 유효한지 확인.
     */
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
     * 3. jokboId가 존재하는 족보의 id인지 유효성 체크 필요.
     */
    @GetMapping("/jokbos/{jokboIndex}/comments")
    public ResponseEntity<Response<ReadJokboCommentResponse>> getAllJokboComments (
            @PathVariable("jokboIndex") Long jokboId,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "pageCount", required = false) Long pageCount) {

        List<JokboComment> jokboComments = jokboService.getAllJokboComments(jokboId);
        List<JokboCommentResponse> comment_list = jokboComments.stream()
                .map(c -> new JokboCommentResponse(
                        c.getId(),
                        c.getCreatedAt(),
                        c.getContents(),
                        c.getIsDeleted(),
                        c.getUser().getId(),
                        c.getUser().getNickname()))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(Response.success(new ReadJokboCommentResponse(
                        comment_list,
                        comment_list.size()
                )));
    }

    /**
     * 내가 쓴 댓글 삭제하기.
     *
     * 고쳐야 할 부분
     * 1. commentId가 존재하는 족보 댓글을 가리키는지 확인 필요.
     */
    @DeleteMapping("/jokbos/{jokboIndex}/comments/{commentIndex}")
    public ResponseEntity<Response<Void>> deleteJokboComment (
            @PathVariable("jokboIndex") Long jokboId,
            @PathVariable("commentIndex") Long commentId,
            @RequestBody @Valid DeleteJokboCommentRequest request) {

        // 세션 체크하기.
        AuthorizationService.checkSession(request.getUserIndex());

        JokboComment jokboComment = jokboService.getAJokboComment(commentId);
        jokboService.deleteJokboComment(jokboComment, request.getUserIndex());

        return ResponseEntity.ok().body(
                Response.success(null)
        );
    }

    /**
     * 총 족보 개수 조회하기.
     */
    @GetMapping("/jokbo-count")
    public ResponseEntity<Response<CountAllJokboResponse>> countAllJokbos() {
        int count = jokboService.countAll();

        return ResponseEntity.ok()
                .body(Response.success(
                        new CountAllJokboResponse(count)));
    }

    /**
     * 학과별 추천 식당 조회하기
     *
     * 고쳐야 할 부분
     * 1. department가 유효한지 확인하기.
     */
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
     * 1. department가 유효한지 확인.
     */
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

        String title;
        String nickname;
        String contents;

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
    }

    /**
     * 족보에 달린 댓글을 삭제하기 위한 정보를 받을 DTO
     */
    @Data
    static class DeleteJokboCommentRequest {
        @NotNull
        private Long userIndex;
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
}
