package com.matdori.matdori.controller;

import com.matdori.matdori.domain.Notice;
import com.matdori.matdori.domain.Response;
import com.matdori.matdori.domain.TermsOfService;
import com.matdori.matdori.service.EtcService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class EtcApiController {

    private final EtcService etcService;

    /**
     * 공지사항 리스트 조회하기
     */
    @GetMapping("/notice")
    public ResponseEntity<Response<List<findAllNoticeResponse>>> readAllNotice() {

        List<Notice> allNotice = etcService.findAllNotice();
        List<findAllNoticeResponse> responseList = allNotice.stream()
                .map(n -> new findAllNoticeResponse(
                       n.getId(),
                       n.getTitle(),
                       n.getContents(),
                       n.getCreatedAt()
                )).collect(Collectors.toList());

        return ResponseEntity.ok().body(
                Response.success(
                        responseList
                )
        );
    }

    /**
     * 공지사항 글 조회하기
     *
     * 고쳐야 할 부분
     * 1. noticeIndex 유효한지 확인.
     */
    @GetMapping("/notice/{noticeIndex}")
    public ResponseEntity<Response<Notice>> readANotice(
            @PathVariable("noticeIndex") Long noticeIndex) {

        Notice notice = etcService.findANotice(noticeIndex);
        return ResponseEntity.ok().body(
                Response.success(
                        notice
                )
        );
    }

    /**
     * 이용약관 리스트 받기
     */
    @GetMapping("/terms-of-service")
    public ResponseEntity<Response<List<TermsOfService>>> readAllTerms() {

        List<TermsOfService> terms = etcService.findAllTerms();
        return ResponseEntity.ok().body(
                Response.success(
                        terms
                )
        );
    }


    /**
     * 공지사항 리스트 조회하기의 응답을 위한 DTO
     */
    @Data
    @AllArgsConstructor
    static class findAllNoticeResponse {
        private Long noticeIndex;
        private String title;
        private String contents;
        private LocalDateTime createdAt;
    }
}
