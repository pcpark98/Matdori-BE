package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {

    private final EntityManager em;

    /**
     * 공지사항 저장하기.
     */
    public void save(Notice notice) {
        em.persist(notice);
    }

    /**
     * id로 단일 공지사항 검색하기.
     */
    public Notice findOne(Long id) {
        return em.find(Notice.class, id);
    }

    /**
     * 공지사항 삭제하기.
     */
    public void delete(Long id) {
        em.remove(em.find(Notice.class, id));
    }

    /**
     * 모든 공지사항 리스트 조회하기.
     */
    public List<Notice> findAll() {
        return em.createQuery(
                "SELECT n " +
                        "FROM Notice n ", Notice.class)
                .getResultList();
    }
}
