package com.matdori.matdori.controller;


import com.matdori.matdori.domain.*;
import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.repositoy.Dto.FavoriteStore;
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
     */
    @Operation(summary = "회원 가입 API", description = "회원 가입을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 유효하지 않은 이메일 형식(INVALID_EMAIL_FORMAT) <br> 유효하지 않은 비밀번호 형식(INVALID_PASSWORD_FORMAT) <br> 유효하지 않은 학과(NOT_EXISTED_DEPARTMENT)"),
            @ApiResponse(responseCode = "401", description = "이메일 인증 누락 (INCOMPLETE_EMAIL_VERIFICATION)"),
            @ApiResponse(responseCode = "409", description = "중복 회원가입(DUPLICATED_USER)"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @Parameters({
            @Parameter(name = "email", description = "사용자 이메일"),
            @Parameter(name = "password", description = "비밀번호" ),
            @Parameter(name = "department", description = "학과명")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<Response<Void>> createUser(@RequestBody @Valid CreateUserRequest request) throws NoSuchAlgorithmException {
        // 이메일 인증여부 확인
        AuthorizationService.checkEmailVerificationCompletion(request.email, EmailAuthorizationType.SIGNUP);
        User user = new User();
        user.setEmail(request.email);
        user.setDepartment(Department.nameOf(request.department));
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
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE, required = false),
            @Parameter(name = "userIndex", description = "유저 id", required = true),
            @Parameter(name = "storeId", description = "가게 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게(NOT_EXISTED_STORE)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/users/{userIndex}/favorite-store")
    public ResponseEntity<Response<CreateFavoriteStoreResponse>> createFavoriteStore(@PathVariable("userIndex") @NotNull Long userId,
                                    @RequestBody @Valid CreateFavoriteStoreRequest request){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        Long favoriteStoreIndex  = userService.createFavoriteStore(request.storeId, userId);
        return ResponseEntity.ok()
                .body(Response.success(new CreateFavoriteStoreResponse(favoriteStoreIndex)));
    }

    /**
     * 내가 좋아요 누른 가게 리스트 조회하기.
     *
     * 고쳐야 할 부분
     * 1. DTO에 Builder 사용해보기.
     */
    @Operation(summary = "내가 좋아요 누른 가게 리스트 조회 API", description = "유저가 좋아요를 누른 가게의 리스트를 조회합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "cursor", description = "마지막 row의 favoriteStoreId")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/favorite-stores")
    public ResponseEntity<Response<ReadFavoriteStoresResponse>> readFavoriteStores(
            @PathVariable("userIndex") Long userId,
            @RequestParam(required = false) Long cursor){

        // 세션 체크
        AuthorizationService.checkSession(userId);
        Boolean hasNext = true;

        List<FavoriteStore> favoriteStores = userService.findAllFavoriteStore(userId, cursor);


        if(favoriteStores.size() != 14)
            hasNext = false;

        return ResponseEntity.ok().body(Response.success(
                new ReadFavoriteStoresResponse(hasNext, favoriteStores)
        ));
    }

    /**
     * 내가 좋아요한 가게 삭제하기.
     */
    @Operation(summary = "내가 좋아요 누른 가게 삭제 API", description = "유저가 가게에 좋아요 누른 것을 취소합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id", required = true),
            @Parameter(name = "favoriteStoreIndex", description = "좋아요한 가게 id", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게(NOT_EXISTED_STORE)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/users/{userIndex}/favorite-stores/{favoriteStoreIndex}")
    public ResponseEntity<Response<Void>> deleteFavoriteStore(
            @PathVariable("userIndex") Long userId,
            @PathVariable("favoriteStoreIndex") Long favoriteStoreId){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        // storeId가 유효한지 검증 필요.
        userService.deleteFavoriteStore(favoriteStoreId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 내가 좋아요한 족보 삭제하기.
     */
    @Operation(summary = "내가 좋아요 누른 족보 삭제 API", description = "유저가 족보에 좋아요 누른 것을 취소합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "favoriteJokboIndex", description = "족보 id")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "좋아요 하지 않은 족보에 대한 삭제(NOT_EXISTED_JOKBO_FAVORITE)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/users/{userIndex}/favorite-jokbos")
    public ResponseEntity<Response<Void>> deleteFavoriteJokbo(
            @PathVariable("userIndex") Long userId,
            @RequestBody DeleteFavoriteJokboRequest request){

        // 세션 체크
        AuthorizationService.checkSession(userId);

        userService.deleteFavoriteJokbo(request.favoriteJokbosId, userId);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인 API", description = "로그인을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_EMAIL_FORMAT)"),
            @ApiResponse(responseCode = "401", description = "로그인 실패(INVALID_CREDENTIALS)"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @Parameters({
            @Parameter(name = "email", description = "이메일"),
            @Parameter(name = "password", description = "비밀번호")
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
                .header("set-cookie","sessionId="+uuid +"; Max-Age=2147483647; Path=/; Secure; HttpOnly; SameSite=None")
                .body(Response.success(new LoginResponse(new LoginResult(user.getId(), user.getNickname(), user.getDepartment().getName()))));
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃 API", description = "로그아웃을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_EMAIL_FORMAT)"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @Parameter(name ="세션 id", in = ParameterIn.COOKIE)
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
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_EMAIL_FORMAT)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원(NOT_EXISTED_USER) <type=UPDATEPASSWORD 일 경우"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원(DUPLICATED_USER) <type=SIGNUP 일 경우>"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
    @Parameters({
            @Parameter(name = "email", description = "이메일", required = false),
            @Parameter(name = "type", description = "인증타입 (SIGNUP/UPDATEPASSWORD)", required = false)
    })
    @PostMapping("/email-authentication")
    public ResponseEntity<Response<Void>> authenticateEmail(@RequestBody @Valid AuthenticateEmailRequest request){
        mailService.sendAuthorizationMail(request.email, request.type);
        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 이메일 인증 - 인증 번호 체크
     */
    @Operation(summary = "이메일 인증 번호 확인 API", description = "이메일 인증을 위해 입력된 인증 번호를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM)"),
            @ApiResponse(responseCode = "401", description = "인증 가능 시간 만료(EXPIRED_SESSION) <br> 잘못된 인증번호(WRONG_AUTHENTICATION_NUMBER) : 추가예정 "),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @Parameters({
            @Parameter(name = "number", description = "인증번호", required = false),
            @Parameter(name = "type", description = "인증타입 (SIGNUP/UPDATEPASSWORD)", required = false)
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
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 족보(NOT_EXISTED_JOKBO)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/users/{userIndex}/favorite-jokbo")
    public ResponseEntity<Response<CreateFavoriteJokboResponse>> createFavoriteJokbo(@RequestBody @Valid CreateFavoriteJokboRequest request,
                                                              @PathVariable("userIndex") Long userId){
        // 세션 체크
        AuthorizationService.checkSession(userId);

        Long favoriteJokboId =  userService.createFavoriteJokbo(request.jokboId, userId);
        return ResponseEntity.ok()
                .body(Response.success(new CreateFavoriteJokboResponse(favoriteJokboId)));
    }

    /**
     * 내가 좋아요한 족보 리스트 조회하기
     */
    @Operation(summary = "내가 좋아요 한 족보 리스트 조회 API", description = "유저가 좋아요를 눌러 저장한 족보들을 조회합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "cursor", description = "마지막 row의 favoriteJokboId")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/favorite-jokbos")
    public ResponseEntity<Response<ReadFavoriteJokbosResponse>> readFavoriteJokbos(
            @PathVariable("userIndex") Long userId,
            @RequestParam(required = false) Long cursor){

        Boolean hasNext = true;
        // 세션 체크
        AuthorizationService.checkSession(userId);
        List<JokboFavorite> jokboFavorites = userService.findAllFavoriteJokbo(userId, cursor);
        if(jokboFavorites.size() != 14)
            hasNext = false;

        List<FavoriteJokbosResponse> favoriteJokbos = jokboFavorites.stream()
                .map(j -> {
                    Jokbo jokbo = j.getJokbo();
                    Double totalRating = (double) ((jokbo.getCleanRating() + jokbo.getFlavorRating() + jokbo.getUnderPricedRating()) / 3);
                    return new FavoriteJokbosResponse(
                            j.getId(),
                            jokbo.getId(),
                            jokbo.getTitle(),
                            totalRating,
                            jokbo.getJokboImgs(),
                            jokbo.getContents(),
                            jokbo.getJokboComments().size(),
                            jokbo.getJokboFavorites().size());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(
                Response.success(
                        new ReadFavoriteJokbosResponse(hasNext, favoriteJokbos)
                ));
    }

    /**
     * 비밀번호 변경하기
     */
    @Operation(summary = "비밀번호 변경 API", description = "유저의 비밀번호 변경을 수행합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "쿠키에 들어있는 세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "password", description = "비밀번호")
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
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name ="nickname", description = "닉네임")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> 닉네임 30자 초과(INVALID_NICKNAME_FORMAT)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "중복된 닉네임(DUPLICATED_NICKNAME)", content = @Content(schema = @Schema(implementation = Error.class))),
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
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE, required = false),
            @Parameter(name = "userIndex", description = "유저 id"),
            @Parameter(name = "cursor", description = "마지막 row의 jokboId")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/jokbos")
    public ResponseEntity<Response<AllMyJokboResponse>> readAllMyJokbo(@PathVariable("userIndex") Long userId,
                                                                             @RequestParam(required = false) Long cursor){
        // 세션 체크
        AuthorizationService.checkSession(userId);
        Boolean hasNext = true;
        List<Jokbo> jokbos = userService.readAllMyJokbo(userId, cursor);
        List<JokboResponse> myJokbo = jokbos.stream()
                .map(j -> {
                    Double totalRating = (double) (j.getCleanRating() + j.getFlavorRating() + j.getUnderPricedRating()) / 3;
                    return new JokboResponse(
                            j.getId(),
                            j.getTitle(),
                            j.getContents(),
                            totalRating,
                            j.getJokboImgs(),
                            j.getJokboComments().size(),
                            j.getJokboFavorites().size());
                })
                .collect(Collectors.toList());
        if(jokbos.size() != 14)
            hasNext = false;


        return ResponseEntity.ok().body(Response.success(
                new AllMyJokboResponse(hasNext, myJokbo)
        ));
    }

    /**
     * 내가 쓴 모든 족보 댓글 조회하기.
     */
    @Operation(summary = "내가 쓴 모든 댓글 조회 API", description = "내가 족보에 작성한 모든 댓글들을 조회합니다.")
    @Parameters({
            @Parameter(name = "sessionId", description = "세션 id", in = ParameterIn.COOKIE),
            @Parameter(name = "userIndex", description = "유저 id", required = true),
            @Parameter(name = "cursor", description = "처음 null, 이후 요청부터 마지막 jokboId")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 세션 쿠키 누락(INVALID_REQUIRED_COOKIE)"),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION)"),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/users/{userIndex}/comments")
    public ResponseEntity<Response<UserCommentsResponse>> readAllMyJokboComment(@PathVariable("userIndex") Long userId,
                                                                                           @RequestParam(value = "cursor", required = false) Long cursor){
        // 세션 체크
        AuthorizationService.checkSession(userId);
        Boolean hasNext = true;
        List<JokboComment> jokboComments = userService.readAllMyJokboComment(userId, cursor);
        List<AllMyJokboCommentResponse> comments = jokboComments.stream()
                .map(c -> new AllMyJokboCommentResponse(
                        c.getJokbo().getId(),
                        c.getId(),
                        c.getJokbo().getTitle(),
                        c.getContents(),
                        0,
                        c.getCreatedAt()))
                .collect(Collectors.toList());

        if(jokboComments.size() != 14)
            hasNext = false;


        return ResponseEntity.ok().body(Response.success(
                new UserCommentsResponse(hasNext, comments)
        ));
    }

    /**
     * 비밀번호 찾기(로그인 안 된 상태)
     */
    @Operation(summary = "비밀번호 찾기 API", description = "로그인이 되지 않은 상태에서 비밀번호 찾기를 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_REQUIRED_PARAM) <br> 쿠키 누락(INVALID_REQUIRED_COOKIE) <br> 잘못된 비밀번호 형식(INVALID_PASSWORD_FORMAT) ", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "세션 만료(EXPIRED_SESSION) <br> 이메일 인증 누락(INCOMPLETE_EMAIL_VERIFICATION)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "403", description = "접근할 수 없는 resource(INSUFFICIENT_PRIVILEGES)", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @Parameters({
            @Parameter(name = "email", description = "이메일"),
            @Parameter(name = "password", description = "변경할 비밀번호")
    })
    @PutMapping("/password")
    public ResponseEntity<Response<Void>> updatePasswordWithoutLogin(@RequestBody UpdatePasswordWithoutLoginRequest request) throws NoSuchAlgorithmException {
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
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락(INVALID_EMAIL_FORMAT)"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임(DUPLICATED_NICKNAME)"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
    @Parameter(name = "nickname",description = "변경할 닉네임",required = true)
    @GetMapping("/nickname")
    public ResponseEntity<Response<Void>> checkNicknameExistence(@RequestParam("nickname") String nickname){
        userService.checkNicknameExistence(nickname);

        return ResponseEntity.ok()
                .body(Response.success(null));
    }

    /**
     * 학과 리스트 불러오기
     */
    @Operation(summary = "학과 리스트 불러오기", description = "학과 리스트를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
    @GetMapping("/departments")
    public ResponseEntity<Response<List<String>>> readDepartments(){
        return ResponseEntity.ok().body(Response.success(Department.getDepartmentList()));
    }

    /**
     * 유저 삭제 임시 api
     */
    @Operation(summary = "유저삭제", description = "유저를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
    @Parameter(name = "userIndex", description = "삭제할 유저 index")
    @DeleteMapping("/users/{userIndex}")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable("userIndex") Long userIndex){
        userService.deleteUser(userIndex);
        return ResponseEntity.ok().body(null);
    }

    /**
     * 이메일 조회
     */
    @Operation(summary = "이메일 조회", description = "이메일을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = Error.class))),
    })
    @Parameter(name = "userIndex", description = "유저 index")
    @GetMapping("/users/{userIndex}/email")
    public ResponseEntity<Response<ReadUserEmailResponse>> readUserEmail(@PathVariable("userIndex") Long userIndex){
        AuthorizationService.checkSession(userIndex);
        User user = userService.findOne(userIndex);
        return ResponseEntity.ok().body(Response.success(new ReadUserEmailResponse(user.getEmail())));
    }



    @Data
    static class LoginRequest{
        @NotNull
        @Schema(description = "이메일")
        private String email;

        @NotNull
        @Schema(description = "비밀번호")
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
    @NoArgsConstructor
    static class FavoriteJokbosResponse{
        private Long favoriteJokboId;
        private Long jokboId;
        private String title;
        private Double totalRating;
        private String imgUrl;
        private String contents;
        private int commentCnt;
        private int favoriteCnt;

        public FavoriteJokbosResponse(Long favoriteJokboId,
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

        @NotBlank
        private String department;
    }

    @Data
    static class AuthenticateEmailRequest{
        @NotBlank
        private String email;

        @NotNull
        private EmailAuthorizationType type;
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
    static class JokboResponse{
        private Long jokboId;
        private String title;
        private String contents;
        private Double totalRating;
        private String imgUrl;
        private int commentCnt;
        private int favoriteCnt;

        public JokboResponse(Long jokboId,
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
        private Long commentId;
        private String title;
        private String contents;
        private Integer favoriteCnt;
        private LocalDateTime writtenAt;
    }

    @Data
    static class UpdatePasswordWithoutLoginRequest{
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    static class CheckNicknameRequest{
        @NotBlank
        private String nickname;
    }

    @Data
    @AllArgsConstructor
    static class CreateFavoriteStoreResponse{
        private Long favoriteStoreIndex;
    }

    @Data
    @AllArgsConstructor
    static class ReadUserEmailResponse{
        private String email;
    }

    @Data
    @AllArgsConstructor
    static class UserCommentsResponse{
        private Boolean hasNext;
        private List<AllMyJokboCommentResponse> comments;
    }

    @Data
    @AllArgsConstructor
    static class ReadFavoriteJokbosResponse{
        private Boolean hasNext;
        private List<FavoriteJokbosResponse> favoriteJokbos;
    }

    @Data
    @AllArgsConstructor
    static class ReadFavoriteStoresResponse{
        private Boolean hasNext;
        private List<FavoriteStore> favoriteStores;
    }

    @Data
    @AllArgsConstructor
    static class AllMyJokboResponse{
        private Boolean hasNext;
        private List<JokboResponse> jokbos;
    }

    @Data
    @AllArgsConstructor
    static class CreateFavoriteJokboResponse{
        private Long favoriteJokboId;
    }

    @Data
    static class DeleteFavoriteJokboRequest{
        private List<Long> favoriteJokbosId;
    }
}