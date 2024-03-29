package com.matdori.matdori.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.matdori.matdori.domain.*;
import com.matdori.matdori.exception.*;
import com.matdori.matdori.repositoy.*;
import com.matdori.matdori.repositoy.Dto.JokboRichStore;
import com.matdori.matdori.repositoy.Dto.MatdoriPick;
import com.matdori.matdori.repositoy.Dto.StoreListByDepartment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JokboService {

    private final JokboRepository jokboRepository;
    private final JokboImgRepository jokboImgRepository;
    private final JokboCommentRepository jokboCommentRepository;
    private final StoreRepository storeRepository;
    private final JokboFavoriteRepository jokboFavoriteRepository;

    private final AmazonS3 amazonS3;

    @Value("matdori-repo/jokbo")
    private String bucket;

    /**
     * 족보 작성하기.
     */
    @Transactional
    public void createJokbo(Jokbo jokbo, List<MultipartFile> images) throws IOException {

        List<String> uploadedFileName = new ArrayList<>();
        try {
            // 족보 생성하기.
            jokboRepository.save(jokbo);


            // S3에 이미지 업로드하기
            List<String> imageUrls = new ArrayList<>();
            if(!CollectionUtils.isEmpty(images)) {

                // 파일의 확장자 체크
                for(MultipartFile image : images) {
                    String contentType = image.getContentType();

                    // 확장자가 존재하지 않는 경우.
                    if(ObjectUtils.isEmpty(contentType)) {
                        throw new NotExistedFileExtensionException(ErrorCode.NOT_EXISTED_FILE_EXTENSION);
                    }

                    else {
                        // 확장자가 jpg, jpeg, png의 세 가지 중 하나인 경우에만 업로드 가능.
                        if(!contentType.contains("image/jpg") && !contentType.contains("image/jpeg") && !contentType.contains("image/png")) {
                            throw new UnsupportedFileExtensionException(ErrorCode.UNSUPPORTED_FILE_EXTENSION);
                        }
                    }
                }


                // 이미지 파일 S3에 업로드하여 이미지 url 리스트 받아오기.
                for(MultipartFile image : images) {

                    // FileName의 중복을 방지하기 위해 UUID를 이용해 파일에 새로 붙일 랜덤 이름을 생성.
                    String originalFileName = image.getOriginalFilename();
                    String uniqueFileName = UUID.randomUUID() + originalFileName;

                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(image.getSize());
                    metadata.setContentType(image.getContentType());

                    amazonS3.putObject(bucket, uniqueFileName, image.getInputStream(), metadata);

                    imageUrls.add(amazonS3.getUrl(bucket, uniqueFileName).toString());
                    uploadedFileName.add(uniqueFileName);
                }
            }


            // 족보 이미지 테이블에 S3로 부터 받아온 url 리스트 넣기.
            if(!CollectionUtils.isEmpty(imageUrls)) {
                for(String imgUrl : imageUrls) {
                    jokboImgRepository.save(
                            new JokboImg(
                                    jokbo,
                                    imgUrl
                            )
                    );
                }
            }
        } catch (Exception e) {
            // 족보 작성 중간에 문제가 발생했을 때 S3에 저장한 이미지 삭제
            if(!CollectionUtils.isEmpty(uploadedFileName)) {
                for(String uniqueFileName : uploadedFileName) {
                    amazonS3.deleteObject(bucket, uniqueFileName);
                }
            }
        }
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
     * 선택된 id들에 해당하는 족보 조회하기
     */
    public List<Jokbo> findAllById(List<Long> jokboIdList) {

        List<Jokbo> selectedJokboList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(jokboIdList)) {
            for(Long jokboId : jokboIdList) {
                Optional<Jokbo> jokbo = jokboRepository.findOne(jokboId);
                if(!jokbo.isPresent()) throw new NotExistedJokboException(ErrorCode.NOT_EXISTED_JOKBO);

                selectedJokboList.add(jokbo.get());
            }
        }

        // 선택된 족보가 없는 경우
        if(CollectionUtils.isEmpty(selectedJokboList)) throw new NotExistedSelectedJokboException(ErrorCode.NOT_EXISTED_SELECTED_JOKBO);

        return selectedJokboList;
    }

    /**
     * 삭제하기 위해 선택된 족보들에 딸린 이미지들 조회하기
     */
    public List<JokboImg> findAllImgById(List<Jokbo> jokboList) {

        List<JokboImg> jokboImgs = new ArrayList<>();
        if(!CollectionUtils.isEmpty(jokboList)) {
            for(Jokbo jokbo : jokboList) {
                List<JokboImg> currentImgs = jokbo.getJokboImgs();
                jokboImgs.addAll(currentImgs);
            }
        }
        return jokboImgs;
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
     * 선택된 족보들 삭제하기
     */
    @Transactional
    public void deleteJokbo(Long userId, List<Jokbo> selectedJokboList, List<String> imgUrls) {

        if(!CollectionUtils.isEmpty(selectedJokboList)) {
            for(Jokbo jokbo : selectedJokboList) {
                if(!jokbo.getUser().getId().equals(userId)) {
                    // 다른 사람이 작성한 족보를 삭제하려고 하는 경우.
                    throw new InsufficientPrivilegesException(ErrorCode.INSUFFICIENT_PRIVILEGES);
                }

                // 족보에 매핑된 좋아요들 삭제
                jokboFavoriteRepository.deleteAllByJokboId(jokbo.getId());

                // 족보에 매핑된 댓글들의 좋아요들 삭제

                // 족보에 매핑된 댓글들 삭제
                jokboCommentRepository.deleteAllByJokboId(jokbo.getId());

                // 족보 삭제
                jokboRepository.delete(jokbo.getId());
            }
        }


        // S3에 저장된 족보 이미지 삭제.
        if(!CollectionUtils.isEmpty(imgUrls)) {

            // 이미지 url을 파싱해서 이미지의 UniqueFileName을 얻어옴.
            List<String> uniqueFileNames = new ArrayList<>();
            for(String imgUrl : imgUrls) {
                String[] splitedUrl = imgUrl.split("/");
                uniqueFileNames.add(splitedUrl[splitedUrl.length-1]);
            }

            // S3에서 해당 파일을 삭제.
            for(String uniqueFileName : uniqueFileNames) {
                amazonS3.deleteObject(bucket, uniqueFileName);
            }
        }
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
    public List<JokboComment> getAllJokboComments(Long jokboId, Long cursor) {

        // 없는 족보에 대한 댓글을 조회하려고 하는 경우
        Optional<Jokbo> jokbo = jokboRepository.findOne(jokboId);
        if(!jokbo.isPresent()) throw new NotExistedJokboException(ErrorCode.NOT_EXISTED_JOKBO);

        if(cursor == null) {
            return jokboCommentRepository.findAllJokboComments(jokboId);
        }
        return jokboCommentRepository.findCommentsAtJokboDescendingById(jokboId, cursor);
    }

    /**
     * 족보에 달린 댓글 하나 조회하기.
     */
    public JokboComment getAJokboComment(Long id) {

        Optional<JokboComment> jokboComment = jokboCommentRepository.findOne(id);
        if(!jokboComment.isPresent() || jokboComment.get().getIsDeleted() == true) throw new NotExistedJokboCommentException(ErrorCode.NOT_EXISTED_JOKBO_COMMENT);

        return jokboComment.get();
    }

    /**
     * 선택된 댓글들 조회하기.
     */
    public List<JokboComment> getSelectedJokboComments(Long userId, List<Long> jokboCommentIdList) {

        List<JokboComment> selectedJokboCommentList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(jokboCommentIdList)) {
            for(Long jokboCommentId : jokboCommentIdList) {
                Optional<JokboComment> jokboComment = jokboCommentRepository.findOne(jokboCommentId);
                if(!jokboComment.isPresent() || jokboComment.get().getIsDeleted()) throw new NotExistedJokboCommentException(ErrorCode.NOT_EXISTED_JOKBO_COMMENT);

                selectedJokboCommentList.add(jokboComment.get());
            }
        }

        // 선택된 댓글이 없는 경우
        if(CollectionUtils.isEmpty(selectedJokboCommentList)) throw new NotExistedSelectedJokboCommentException(ErrorCode.NOT_EXISTED_SELECTED_JOKBO_COMMENT);

        return selectedJokboCommentList;
    }

    /**
     * 선택된 댓글들 삭제하기. -> 진짜 삭제하지는 않고 is_deleted를 true로 바꿈.
     */
    @Transactional
    public void deleteJokboComment(Long userId, List<JokboComment> selectedJokboCommentList) {

        if(!CollectionUtils.isEmpty(selectedJokboCommentList)) {
            for(JokboComment jokboComment : selectedJokboCommentList) {
                if(!jokboComment.getUser().getId().equals(userId)) {
                    // 다른 사람이 작성한 댓글을 삭제하려고 하는 경우.
                    throw new InsufficientPrivilegesException(ErrorCode.INSUFFICIENT_PRIVILEGES);
                }
            }
        }

        if(!CollectionUtils.isEmpty(selectedJokboCommentList)) {
            for(JokboComment jokboComment : selectedJokboCommentList) {
                jokboComment.setIsDeleted(true);
            }
        }
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
    public List<Store> getStoreListByDepartment(String department) {

        return storeRepository.getStoreListByDepartment(Department.nameOf(department));
    }

    /**
     * 맛도리 픽 조회하기.
     */
    public List<MatdoriPick> getMatdoriPick() {

        List<MatdoriPick> matdoriPick = new ArrayList<>();

        List<Store> randomStores = storeRepository.getMatdoriPick();
        if(!CollectionUtils.isEmpty(randomStores)) {
            for(Store store : randomStores) {
                matdoriPick.add(new MatdoriPick(store.getId(), store.getName(), store.getImgUrl()));
            }
        }

        return matdoriPick;
    }

    /**
     * 족보 부자 가게 리스트 조회하기.
     */
    public List<JokboRichStore> getJokboRichStores() {

        return storeRepository.getJokboRichStores();
    }
}
