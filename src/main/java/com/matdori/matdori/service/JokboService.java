package com.matdori.matdori.service;

import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.JokboComment;
import com.matdori.matdori.domain.JokboImg;
import com.matdori.matdori.exception.*;
import com.matdori.matdori.repositoy.Dto.JokboRichStore;
import com.matdori.matdori.repositoy.Dto.MatdoriPick;
import com.matdori.matdori.repositoy.Dto.StoreListByDepartment;
import com.matdori.matdori.repositoy.JokboCommentRepository;
import com.matdori.matdori.repositoy.JokboImgRepository;
import com.matdori.matdori.repositoy.JokboRepository;
import com.matdori.matdori.repositoy.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JokboService {

    private final JokboRepository jokboRepository;
    private final JokboImgRepository jokboImgRepository;
    private final JokboCommentRepository jokboCommentRepository;
    private final StoreRepository storeRepository;

    /**
     * 족보 작성하기.
     */
    @Transactional
    public void createJokbo(Jokbo jokbo) {
        jokboRepository.save(jokbo);
    }

    /**
     * 족보 이미지 테이블 정보 저장하기.
     */
    @Transactional
    public void createJokboImg(List<JokboImg> jokboImgs) {
        if(!CollectionUtils.isEmpty(jokboImgs)) {
            for(JokboImg jokboImg : jokboImgs) {
                jokboImgRepository.save(jokboImg);
            }
        }
    }

    /**
     * id로 족보 조회하기
     */
    public Jokbo findOne(Long id) {

        Optional<Jokbo> jokbo = jokboRepository.findOne(id);
        if(!jokbo.isPresent()) throw new NotExistedJokboException(ErrorCode.NOT_EXISTED_JOKBO);

        return jokbo.get();
    }

    /**
     * 족보에 매핑된 모든 이미지 url들을 조회하기
     */
    public List<String> getImageUrls(List<JokboImg> jokboImgs) {
        List<String> imgUrls = new ArrayList<>();
        if(!CollectionUtils.isEmpty(jokboImgs)) {
            for(JokboImg jokboImg : jokboImgs) {
                imgUrls.add(jokboImg.getImgUrl());
            }
        }
        return imgUrls;
    }

    /**
     * 족보 삭제하기
     */
    @Transactional
    public void deleteJokbo(Jokbo jokbo, Long userId, List<JokboImg> jokboImgs) {
        if(jokbo.getUser().getId() != userId) {
            // 다른 사람이 작성한 족보를 삭제하려고 하는 경우.
            throw new InsufficientPrivilegesException(ErrorCode.INSUFFICIENT_PRIVILEGES);
        }

        if(!CollectionUtils.isEmpty(jokboImgs)) {
            for(JokboImg jokboImg : jokboImgs) {
                // 족보에 첨부된 이미지들 삭제.
                Optional<JokboImg> currentImage = jokboImgRepository.findOne(jokboImg.getId());

                if(currentImage.isPresent()) jokboImgRepository.delete(jokboImg.getId());
                else throw new NotExistedJokboImgException(ErrorCode.NOT_EXISTED_JOKBO_IMG);
            }
        }

        // 존재하는 족보인지 확인
        Optional<Jokbo> jokboToDelete = jokboRepository.findOne(jokbo.getId());

        if(jokboToDelete.isPresent()) jokboRepository.delete(jokbo.getId()); // 족보 삭제
        else throw new NotExistedJokboException(ErrorCode.NOT_EXISTED_JOKBO);
    }

    /**
     * 족보에 댓글 등록하기.
     */
    @Transactional
    public void createJokboComment(JokboComment jokboComment) {

        jokboCommentRepository.save(jokboComment);
    }

    /**
     * 족보에 달린 모든 댓글 조회하기.
     */
    public List<JokboComment> getAllJokboComments(Long jokboId) {

        // 없는 족보에 대한 댓글을 조회하려고 하는 경우
        Optional<Jokbo> jokbo = jokboRepository.findOne(jokboId);
        if(!jokbo.isPresent()) throw new NotExistedJokboException(ErrorCode.NOT_EXISTED_JOKBO);

        return jokboCommentRepository.findAllJokboComments(jokboId);
    }

    /**
     * 족보에 달린 댓글 하나 조회하기.
     */
    public JokboComment getAJokboComment(Long id) {

        Optional<JokboComment> jokboComment = jokboCommentRepository.findOne(id);
        if(!jokboComment.isPresent()) throw new NotExistedJokboCommentException(ErrorCode.NOT_EXISTED_JOKBO_COMMENT);

        return jokboComment.get();
    }

    /**
     * 댓글 삭제하기.
     */
    @Transactional
    public void deleteJokboComment(JokboComment jokboComment, Long userId) {
        if(jokboComment.getUser().getId() != userId) {
            // 다른 사람이 작성한 댓글을 삭제하려고 하는 경우.
            throw new InsufficientPrivilegesException(ErrorCode.INSUFFICIENT_PRIVILEGES);
        }

        // 여기 보던 중
        jokboCommentRepository.delete(jokboComment.getId());
    }

    /**
     * 족보의 총 개수 조회하기.
     */
    public int countAll() {

        return jokboRepository.countAll();
    }

    /**
     * 해당 학과의 족보가 가장 많은 가게 구하기
     */
    public List<StoreListByDepartment> getStoreListByDepartment(String department) {

        return storeRepository.getStoreListByDepartment(department);
    }

    /**
     * 맛도리 픽 조회하기.
     */
    public List<MatdoriPick> getMatdoriPick(String department) {

        return storeRepository.getMatdoriPick(department);
    }

    /**
     * 족보 부자 가게 리스트 조회하기.
     */
    public List<JokboRichStore> getJokboRichStores() {

        return storeRepository.getJokboRichStores();
    }
}
