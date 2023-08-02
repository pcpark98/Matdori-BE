package com.matdori.matdori.service;

import com.matdori.matdori.domain.Notice;
import com.matdori.matdori.domain.TermsOfService;
import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.NotExistedNoticeException;
import com.matdori.matdori.repositoy.NoticeRepository;
import com.matdori.matdori.repositoy.TermsOfServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EtcService {

    private final NoticeRepository noticeRepository;
    private final TermsOfServiceRepository termsOfServiceRepository;

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

        Optional<Notice> notice = noticeRepository.findOne(id);
        if(!notice.isPresent()) throw new NotExistedNoticeException(ErrorCode.NOT_EXISTED_NOTICE);

        return notice.get();
    }

    /**
     * 이용약관 리스트 받기
     */
    public List<TermsOfService> findAllTerms() {

        return termsOfServiceRepository.findAllTerms();
    }
}
