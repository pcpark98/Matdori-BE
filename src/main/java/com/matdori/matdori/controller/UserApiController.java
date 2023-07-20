package com.matdori.matdori.controller;


import com.matdori.matdori.domain.*;
import com.matdori.matdori.service.AuthorizationService;
import com.matdori.matdori.service.MailService;
import com.matdori.matdori.service.UserService;
import com.matdori.matdori.service.UserSha256;
import com.matdori.matdori.util.SessionUtil;
import com.matdori.matdori.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "유저 API", description = "유저와 관련된 API들")
@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final MailService mailService;

    /**
     * 회원 가입
     *
     * 고쳐야 할 부분
     * 1. 학과 입력 받기 + setDepartment
     */
    @Operation(summary = "회원 가입 API", description = "회원 가입을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락 또는 이메일, 비밀번호 형식 안 맞음.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "중복 회원가입(이미 존재하는 유저)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
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

    /**
     * 가게 좋아요 누르기
     */
    @Operation(summary = "가게 좋아요 누르기 API", description = "유저가 가게에 대해 좋아요를 눌러 저장합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 혹은 storeIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/users/{userIndex}/favorite-store")
    public ResponseEntity<Response<Void>> createFavoriteStore(@PathVariable("userIndex") @NotNull Long userId,
                                    @RequestBody @Valid CreateFavoriteStoreRequest request){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        userService.createFavoriteStore(request.storeId, userId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 내가 좋아요 누른 가게 리스트 조회하기.
     *
     * 고쳐야 할 부분
     * 1. FavoriteStores 카멜 케이스로 바꾸기.
     * 2. DTO에 Builder 사용해보기.
     */
    @Operation(summary = "내가 좋아요 누른 가게 리스트 조회 API", description = "유저가 좋아요를 누른 가게의 리스트를 조회합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "pageCount", description = "페이지")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/favorite-stores")
    public ResponseEntity<Response<List<readFavoriteStoresResponse>>> readFavoriteStores(
            @PathVariable("userIndex") Long userId,
            @RequestParam int pageCount){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        // 카멜 케이스로 바꾸기
        List<StoreFavorite> FavoriteStores = userService.findAllFavoriteStore(userId, pageCount);
        return ResponseEntity.ok().body(Response.success(FavoriteStores.stream()
                .map(s -> new readFavoriteStoresResponse(
                        s.getId(),
                        s.getStore().getId(),
                        s.getStore().getName(),
                        s.getStore().getCategory(),
                        s.getStore().getImgUrl()))
                .collect(Collectors.toList())));
    }

    /**
     * 내가 좋아요한 가게 삭제하기.
     *
     * 고쳐야 할 부분
     * 1. deleteFavoriteStore에 넘길 storeId가 존재하는 id인지 예외처리하기.
     */
    @Operation(summary = "내가 좋아요 누른 가게 삭제 API", description = "유저가 가게에 좋아요 누른 것을 취소합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "favoriteStoreIndex", description = "좋아요한 가게 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 또는 favoriteStoreIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/users/{userIndex}/favorite-stores/{favoriteStoreIndex}")
    public ResponseEntity<Response<Void>> deleteFavoriteStore(
            @PathVariable("userIndex") Long userId,
            @PathVariable("favoriteStoreIndex") Long storeId){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        // storeId가 유효한지 검증 필요.
        userService.deleteFavoriteStore(storeId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 내가 좋아요한 족보 삭제하기.
     *
     * 고쳐야 할 부분
     * 1.jokboId가 유효한지 검증 필요
     */
    @Operation(summary = "내가 좋아요 누른 족보 삭제 API", description = "유저가 족보에 좋아요 누른 것을 취소합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "jokboIndex", description = "족보 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 또는 jokboIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 족보, 또는 좋아요 하지 않은 족보에 대한 좋아요 취소 시도. jokboIndex 값이 잘못됨.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/users/{userIndex}/favorite-jokbos/{jokboIndex}")
    public ResponseEntity<Response<Void>> deleteFavoriteJokbo(
            @PathVariable("userIndex") Long userId,
            @PathVariable("jokboIndex") Long jokboId){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        // jokboId가 유효한지 검증 필요.
        userService.deleteFavoriteJokbo(jokboId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인 API", description = "로그인을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "email 또는 password 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@Valid @RequestBody LoginRequest request) throws NoSuchAlgorithmException {
        // 존재하는 유저를 받아옴.
        User user = authorizationService.login(request.email, UserSha256.encrypt(request.password));

        // 쿠키를 발급하기 위해 uuid를 하나 생성.
        String uuid = UUID.randomUUID().toString();

        // uuid를 key로, userIndex를 value로 세션에 저장.
        SessionUtil.setAttribute(uuid, String.valueOf(user.getId()));

        // 응답에 header("set-cookie") 해주고 "sessionId="+uuid 이렇게 키=벨류 형태로 줌. -> 응답 헤더에 쿠키가 실려서 감.
        // 보내진 쿠키를 프론트에서 저장해서 다음 요청에 함께 보냄.
        return ResponseEntity.ok()
                .header("set-cookie","sessionId="+uuid)
                .body(Response.success(new LoginResponse(new LoginResult(user.getId(), user.getNickname(), user.getDepartment()))));
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃 API", description = "로그아웃을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout() {

        // 저장해뒀던 유저의 세션을 지워버림.
        AuthorizationService.logout();

        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 이메일 인증 - 인증 메일 보내기.
     */
    @Operation(summary = "인증 메일 전송 API", description = "이메일 인증을 위한 인증 메일을 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "email 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "중복 회원가입(이미 존재하는 유저)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
    @PostMapping("/email-authentication")
    public ResponseEntity<Response<Void>> authenticateEmail(@RequestBody @Valid AuthenticateEmailRequest request){
        mailService.sendAuthorizationMail(request.email);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 이메일 인증 - 인증 번호 체크
     */
    @Operation(summary = "이메일 인증 번호 확인 API", description = "이메일 인증을 위해 입력된 인증 번호를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "number 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패(잘못된 인증번호)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/authentication-number")
    public ResponseEntity<Response<Void>> authenticateNumber(@RequestBody @Valid AuthenticateNumberRequest request){
        AuthorizationService.checkAuthorizationNumber(request.number, request.type);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }


    /**
     * 족보에 좋아요 누르기
     */
    @Operation(summary = "족보에 좋아요 누르기 API", description = "유저가 족보에 대해 좋아요를 눌러 저장합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 또는 jokboId 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/users/{userIndex}/favorite-jokbo")
    public ResponseEntity<Response<Void>> createFavoriteJokbo(@RequestBody @Valid CreateFavoriteJokboRequest request,
                                                              @PathVariable("userIndex") Long userId){
        // 세션 체크
        AuthorizationService.checkSession(userId);

        userService.createFavoriteJokbo(request.jokboId, userId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 내가 좋아요한 족보 리스트 조회하기
     */
    @Operation(summary = "내가 좋아요 한 족보 리스트 조회 API", description = "유저가 좋아요를 눌러 저장한 족보들을 조회합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "pageCount", description = "페이지")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/favorite-jokbos")
    public ResponseEntity<Response<List<ReadFavoriteJokbosResponse>>> readFavoriteJokbos(
            @PathVariable("userIndex") Long userId,
            @RequestParam int pageCount){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        List<JokboFavorite> jokboFavorites = userService.findAllFavoriteJokbo(userId, pageCount);

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

    /**
     * 비밀번호 변경하기
     */
    @Operation(summary = "비밀번호 변경 API", description = "유저의 비밀번호 변경을 수행합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 또는 password 누락 OR 비밀번호 형식 안 맞음.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PutMapping("/users/{userIndex}/password")
    public ResponseEntity<Response<Void>> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                                         @PathVariable("userIndex") Long userId) throws NoSuchAlgorithmException{
        // 세션 체크
        AuthorizationService.checkSession(userId);

        userService.updatePassword(userId,request.password);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 닉네임 변경하기
     */
    @Operation(summary = "닉네임 변경 API", description = "닉네임 변경을 수행합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 또는 nickname 누락 OR 닉네임 최대 길이 초과.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PutMapping("/users/{userIndex}/nickname")
    public ResponseEntity<Response<Void>> updateNickname(@RequestBody @Valid UpdateNicknameRequest request,
                                                         @PathVariable("userIndex") Long userId){
        // 세션 체크
        AuthorizationService.checkSession(userId);

        userService.updateNickname(userId, request.nickname);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 내가 쓴 모든 족보 조회하기.
     */
    @Operation(summary = "내가 쓴 모든 족보 조회 API", description = "내가 작성한 모든 족보를 조회합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "pageCount", description = "페이지")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/jokbos")
    public ResponseEntity<Response<List<AllMyJokboResponse>>> readAllMyJokbo(@PathVariable("userIndex") Long userId,
                                                                             @RequestParam int pageCount){
        // 세션 체크
        AuthorizationService.checkSession(userId);

        List<Jokbo> jokbos = userService.readAllMyJokbo(userId, pageCount);
        return ResponseEntity.ok().body(Response.success(jokbos.stream()
                .map(j ->{
                    Double totalRating = (double) (j.getCleanRating() + j.getFlavorRating() + j.getUnderPricedRating())/3;
                    return new AllMyJokboResponse(
                            j.getId(),
                            j.getTitle(),
                            j.getContents(),
                            totalRating,
                            j.getJokboImgs(),
                            j.getJokboComments().size(),
                            j.getJokboFavorites().size());
                })
                .collect(Collectors.toList())));
    }

    /**
     * 내가 쓴 모든 족보 댓글 조회하기.
     */
    @Operation(summary = "내가 쓴 모든 댓글 조회 API", description = "내가 족보에 작성한 모든 댓글들을 조회합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE, required = true),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "pageCount", description = "페이지")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "userIndex 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "쿠키에 들어있는 유저 정보와, 프론트에서 보낸 userIndex가 다름.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/comments")
    public ResponseEntity<Response<List<AllMyJokboCommentResponse>>> readAllMyJokboComment(@PathVariable("userIndex") Long userId,
                                                                                           @RequestParam int pageCount){
        // 세션 체크
        AuthorizationService.checkSession(userId);

        List<JokboComment> comments = userService.readAllMyJokboComment(userId, pageCount);

        return ResponseEntity.ok().body(Response.success(comments.stream()
                .map(c -> new AllMyJokboCommentResponse(
                        c.getJokbo().getId(),
                        c.getJokbo().getStore().getName(),
                        c.getContents(),
                        c.getCreatedAt()))
                .collect(Collectors.toList())));
    }

    /**
     * 비밀번호 찾기(로그인 안 된 상태)
     */
    @Operation(summary = "비밀번호 찾기 API", description = "로그인이 되지 않은 상태에서 비밀번호 찾기를 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "email 또는 password의 누락 혹은 형식 안 맞음.", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PutMapping("/password")
    public ResponseEntity<Response<Void>> updatePasswordWithoutLogin(@RequestBody updatePasswordWithoutLoginRequest request) throws NoSuchAlgorithmException {
        userService.updatePasswordWithoutLogin(request.email, request.password);

        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 닉네임 중복 체크
     */
    @Operation(summary = "닉네임 중복 체크 API", description = "입력된 닉네임이 이미 존재하는지를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "nickname 누락", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
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

        public ReadFavoriteJokbosResponse(Long favoriteJokboId,
                                          Long jokboId,
                                          String title,
                                          Double totalRating,
                                          List<JokboImg> imgUrl,
                                          String contents,
                                          int commentCnt,
                                          int favoriteCnt) {

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

        public AllMyJokboResponse(Long jokboId,
                                  String title,
                                  String contents,
                                  Double totalRating,
                                  List<JokboImg> imgUrl,
                                  int commentCnt,
                                  int favoriteCnt) {
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