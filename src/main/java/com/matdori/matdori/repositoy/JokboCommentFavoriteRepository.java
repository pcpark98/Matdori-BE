package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboCommentFavorite;
import com.matdori.matdori.domain.JokboFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JokboCommentFavoriteRepository {

    private final EntityManager em;

    /**
     * 족보 댓글 좋아요 정보 저장하기.
     */
    public Long save(JokboCommentFavorite jokboCommentFavorite) {
        em.persist(jokboCommentFavorite);
        return jokboCommentFavorite.getId();
    }

    /**
     * 족보 댓글 좋아요 id로 댓글 좋아요 조회하기
     */
    public Optional<JokboCommentFavorite> findOne(Long id) {

        JokboCommentFavorite jokboCommentFavorite = em.find(JokboCommentFavorite.class, id);
        return Optional.ofNullable(jokboCommentFavorite);
    }

    /**
     * 댓글 좋아요 취소하기.
     */
    public void delete(Long id) {
        em.remove(em.find(JokboCommentFavorite.class, id));
    }
}
