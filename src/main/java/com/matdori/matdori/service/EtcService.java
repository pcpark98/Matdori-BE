package com.matdori.matdori.service;

import com.matdori.matdori.domain.Notice;
import com.matdori.matdori.repositoy.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EtcService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 리스트 조회하기
     */
    public List<Notice> findAllNotice() {
        return noticeRepository.findAll();
    }

    /**
     * 공지사항 글 조회하기
     */
    public Notice findANotice(Long id) {
        // 없는 공지사항 id에 대한 조회 예외처리 필요.
        return noticeRepository.findOne(id);
    }
}
