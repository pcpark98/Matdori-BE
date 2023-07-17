package com.matdori.matdori.controller;

import com.matdori.matdori.domain.Notice;
import com.matdori.matdori.domain.Response;
import com.matdori.matdori.service.EtcService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
