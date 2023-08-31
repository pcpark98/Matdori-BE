package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboCommentFavorite;
import com.matdori.matdori.domain.JokboFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

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
}
