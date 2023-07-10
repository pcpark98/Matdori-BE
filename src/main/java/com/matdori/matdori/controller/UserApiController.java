package com.matdori.matdori.controller;


import com.matdori.matdori.domain.*;
import com.matdori.matdori.service.AuthorizationService;
import com.matdori.matdori.service.MailService;
import com.matdori.matdori.service.UserService;
import com.matdori.matdori.service.UserSha256;
import com.matdori.matdori.util.SessionUtil;
import com.matdori.matdori.util.UserUtil;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final MailService mailService;
    // 회원 가입
    @PostMapping("/sign-up")
    public ResponseEntity<Response<Void>> createUser(@RequestBody @Valid CreateUserRequest request) throws NoSuchAlgorithmException {
        // 이메일 인증여부 확인
        AuthorizationService.checkEmailVerificationCompletion(request.email, EmailAuthorizationType.SIGNUP);
        User user = new User();
        user.setEmail(request.email);
        user.setDepartment("학과 parsing 필요");
        user.setPassword(request.password);
        user.setNickname(UserUtil.getRandomNickname());
        // 약관 동의 추가하는 로직 필요
        userService.signUp(user);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    // 가게 좋아요 누르기
    @PostMapping("/users/{userIndex}/favorite-store")
    public ResponseEntity<Response<Void>> createFavoriteStore(@PathVariable("userIndex") @NotNull Long userId,
                                    @RequestBody @Valid CreateFavoriteStoreRequest request){

        AuthorizationService.checkSession(userId);
        userService.createFavoriteStore(request.storeId, userId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    // 내가 좋아요한 가게 리스트 조회
    @GetMapping("/users/{userIndex}/favorite-stores")
    public ResponseEntity<Response<List<readFavoriteStoresResponse>>> readFavoriteStores(
            @PathVariable("userIndex") Long userId,
            @RequestParam Long pageCount){

        AuthorizationService.checkSession(userId);
        List<StoreFavorite> FavoriteStores = userService.findAllFavoriteStore(userId);
        return ResponseEntity.ok().body(Response.success(FavoriteStores.stream()
                .map(s -> new readFavoriteStoresResponse(s.getId(), s.getStore().getId(), s.getStore().getName(),s.getStore().getCategory() ,s.getStore().getImg_url()))
                .collect(Collectors.toList())));
    }

    // 내가 좋아요한 가게 삭제
    @DeleteMapping("/users/{userIndex}/favorite-stores/{favoriteStoreIndex}")
    public ResponseEntity<Response<Void>> deleteFavoriteStore(
            @PathVariable("userIndex") Long userId,
            @PathVariable("favoriteStoreIndex") Long storeId){

        AuthorizationService.checkSession(userId);
        userService.deleteFavoriteStore(storeId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    // 내가 좋아요한 족보 삭제
    @DeleteMapping("/users/{userIndex}/favorite-jokbos/{jokboIndex}")
    public ResponseEntity<Response<Void>> deleteFavoriteJokbo(
            @PathVariable("userIndex") Long userId,
            @PathVariable("jokboIndex") Long jokboId){

        AuthorizationService.checkSession(userId);
        userService.deleteFavoriteJokbo(jokboId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@Valid @RequestBody LoginRequest request) throws NoSuchAlgorithmException {
        User user = authorizationService.login(request.email, UserSha256.encrypt(request.password));
        String uuid = UUID.randomUUID().toString();

        SessionUtil.setAttribute(uuid, String.valueOf(user.getId()));
        return ResponseEntity.ok()
                .header("set-cookie","sessionId="+uuid)
                .body(Response.success(new LoginResponse(new LoginResult(user.getId(), user.getNickname(), user.getDepartment()))));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout() {
        AuthorizationService.logout();
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    // 이메일 인증
    @PostMapping("/email-authentication")
    public ResponseEntity<Response<Void>> authenticateEmail(@RequestBody @Valid AuthenticateEmailRequest request){

        mailService.sendAuthorizationMail(request.email);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }
    // 인증번호 체크
    @PostMapping("/authentication-number")
    public ResponseEntity<Response<Void>> authenticateNumber(@RequestBody @Valid AuthenticateNumberRequest request){
        AuthorizationService.checkAuthorizationNumber(request.number, request.type);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }


    // 족보 좋아요 누르기
    @PostMapping("/users/{userIndex}/favorite-jokbo")
    public ResponseEntity<Response<Void>> createFavoriteJokbo(@RequestBody @Valid CreateFavoriteJokboRequest request,
                                                              @PathVariable("userIndex") Long userId){
        AuthorizationService.checkSession(userId);
        userService.createFavoriteJokbo(request.jokboId, userId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    // 내가 좋아요한 족보 리스트 조회
    @GetMapping("/users/{userIndex}/favorite-jokbos")
    public ResponseEntity<Response<List<ReadFavoriteJokbosResponse>>> readFavoriteJokbos(
            @PathVariable("userIndex") Long userId,
            @RequestParam Long pageCount){

        AuthorizationService.checkSession(userId);
        List<JokboFavorite> jokboFavorites = userService.findAllFavoriteJokbo(userId);

        return ResponseEntity.ok().body(
                Response.success(jokboFavorites.stream()
                        .map(j -> {
                            Jokbo jokbo = j.getJokbo();
                            Double totalRating = (double) ((jokbo.getCleanRating() + jokbo.getFlavorRating() + jokbo.getUnderPricedRating())/3);
                            return new ReadFavoriteJokbosResponse(
                                    j.getId(),
                                    jokbo.getId(),
                                    jokbo.getTitle(),
                                    totalRating,
                                    jokbo.getJokboImgs(),
                                    jokbo.getContents(),
                                    jokbo.getJokboFavorites().size(),
                                    jokbo.getJokboComments().size());
                        })
                        .collect(Collectors.toList())));

    }

    // 비밀번호 변경하기
    @PutMapping("/users/{userIndex}/password")
    public ResponseEntity<Response<Void>> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                                         @PathVariable("userIndex") Long userId) throws NoSuchAlgorithmException{
        AuthorizationService.checkSession(userId);
        userService.updatePassword(userId,request.password);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }
    // 닉네임 변경하기
    @PutMapping("/users/{userIndex}/nickname")
    public ResponseEntity<Response<Void>> updateNickname(@RequestBody @Valid UpdateNicknameRequest request,
                                                         @PathVariable("userIndex") Long userId){
        AuthorizationService.checkSession(userId);
        userService.updateNickname(userId, request.nickname);
        // 예외처리 필요
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    @GetMapping("/users/{userIndex}/jokbos")
    //pageCount 넣어야됨
    // favoriteCnt 넣어야됨
    public ResponseEntity<Response<List<AllMyJokboResponse>>> readAllMyJokbo(@PathVariable("userIndex") Long userId){
        AuthorizationService.checkSession(userId);
        List<Jokbo> jokbos = userService.readAllMyJokbo(userId);
        return ResponseEntity.ok().body(Response.success(jokbos.stream()
                .map(j ->{
                    Double totalRating = (double) (j.getCleanRating() + j.getFlavorRating() + j.getUnderPricedRating())/3;
                    return new AllMyJokboResponse(j.getId(), j.getTitle(), j.getContents(),totalRating ,j.getJokboImgs(), j.getJokboComments().size(), j.getJokboFavorites().size());
                })
                .collect(Collectors.toList())));
    }

    @GetMapping("/users/{userIndex}/comments")
    //pageCount 넣어야됨
    public ResponseEntity<Response<List<AllMyJokboCommentResponse>>> readAllMyJokboComment(@PathVariable("userIndex") Long userId){
        AuthorizationService.checkSession(userId);
        List<JokboComment> comments = userService.readAllMyJokboComment(userId);
        return ResponseEntity.ok().body(Response.success(comments.stream()
                .map(c -> new AllMyJokboCommentResponse(c.getJokbo().getId(), c.getJokbo().getStore().getName(), c.getContents(), c.getCreatedAt()))
                .collect(Collectors.toList())));
    }

    // 비밀번호 찾기
    @PutMapping("/password")
    public ResponseEntity<Response<Void>> updatePasswordWithoutLogin(@RequestBody updatePasswordWithoutLoginRequest request) throws NoSuchAlgorithmException {
        userService.updatePasswordWithoutLogin(request.email, request.password);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    // 중복 닉네임 체크
    @GetMapping("/nickname")
    public ResponseEntity<Response<Void>> checkNicknameExistence(@RequestBody @Valid checkNicknameRequest request){
        userService.checkNicknameExistence(request.nickname);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    @Data
    static class LoginRequest{
        @NotNull
        private String email;
        @NotNull
        private String password;
    }

    @Data
    @AllArgsConstructor
    static class LoginResponse<T>{
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class LoginResult{
        private Long userId;
        private String nickname;
        private String department;
    }
    @Data
    static class CreateFavoriteStoreRequest{
        @NotNull
        private Long storeId;
    }

    @Data
    @AllArgsConstructor
    static class readFavoriteStoresResponse{
        private Long favoriteStoreId;
        private Long storeId;
        private String name;
        private String category;
        private String imgUrl;
    }

    @Data
    @NoArgsConstructor
    static class ReadFavoriteJokbosResponse{
        private Long favoriteJokboId;
        private Long jokboId;
        private String title;
        private Double totalRating;
        private String imgUrl;
        private String contents;
        private int commentCnt;
        private int favoriteCnt;

        public ReadFavoriteJokbosResponse(Long favoriteJokboId, Long jokboId, String title, Double totalRating, List<JokboImg> imgUrl, String contents, int commentCnt, int favoriteCnt) {
            this.favoriteJokboId = favoriteJokboId;
            this.jokboId = jokboId;
            this.title = title;
            this.totalRating = totalRating;
            if(imgUrl.size() != 0)
                this.imgUrl = imgUrl.get(0).getImgUrl();
            this.contents = contents;
            this.commentCnt = commentCnt;
            this.favoriteCnt = favoriteCnt;
        }
    }


    @Data
    static class CreateUserRequest{
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        //private int[] termIndex;
    }

    @Data
    static class AuthenticateEmailRequest{
        @NotBlank
        private String email;
    }
    @Data
    static class AuthenticateNumberRequest{
        @NotBlank
        private String number;
        @NotNull
        private EmailAuthorizationType type;
    }

    @Data
    static class CreateFavoriteJokboRequest{
        @NotNull
        private Long jokboId;
    }

    @Data
    static class UpdatePasswordRequest{
        @NotBlank
        private String password;
    }

    @Data
    static class UpdateNicknameRequest{
        @NotBlank
        private String nickname;
    }

    @Data
    @AllArgsConstructor
    static class AllMyJokboResponse{
        private Long jokboId;
        private String title;
        private String contents;
        private Double totalRating;
        private String imgUrl;
        private int commentCnt;
        private int favoriteCnt;

        public AllMyJokboResponse(Long jokboId, String title, String contents, Double totalRating,List<JokboImg> imgUrl, int commentCnt, int favoriteCnt) {
            this.jokboId = jokboId;
            this.title = title;
            this.contents = contents;
            this.totalRating = totalRating;
            if(imgUrl.size() != 0)
                this.imgUrl = imgUrl.get(0).getImgUrl();
            this.commentCnt = commentCnt;
            this.favoriteCnt = favoriteCnt;
        }
    }
    @Data
    @AllArgsConstructor
    static class AllMyJokboCommentResponse{
        private Long jokboId;
        private String storeName;
        private String contents;
        private LocalDateTime writtenAt;
    }

    @Data
    static class updatePasswordWithoutLoginRequest{
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    static class checkNicknameRequest{
        @NotBlank
        private String nickname;
    }
}