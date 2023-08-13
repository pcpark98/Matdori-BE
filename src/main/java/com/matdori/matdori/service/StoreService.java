package com.matdori.matdori.service;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.NotExistStoreException;
import com.matdori.matdori.repositoy.Dto.StoreInformationHeader;
import com.matdori.matdori.repositoy.JokboRepository;
import com.matdori.matdori.repositoy.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final JokboRepository jokboRepository;
    public List<Store> findAll() { return storeRepository.findAll(); }
    public Store findOne(Long id) {
        Store store = storeRepository.findOne(id);
        if(store == null) throw new NotExistStoreException(ErrorCode.NOT_EXISTED_STORE);
        return store;
    }

    public List<Category> findAllCategoryWithMenu (Long id) { return storeRepository.findAllCategoryWithMenu(id);}

    public List<Jokbo> findAllJokbo(Long storeId, int startIndex) { return jokboRepository.findByStoreIndex(storeId, startIndex);}

    public com.matdori.matdori.repositoy.Dto.StoreInformationHeader readStoreInformationHeader(Long storeId) {
        Optional<StoreInformationHeader> storeInformationHeader = storeRepository.readStoreInformationHeader(storeId);
        if(storeInformationHeader.isEmpty())
            throw new NotExistStoreException(ErrorCode.NOT_EXISTED_STORE);
        return storeInformationHeader.get();
    }

    /**
     * 해당 가게의 별점 평균 구하기
     */
    public Double getTotalRating(Long id) {
        Store store = storeRepository.findOne(id);
        return storeRepository.getTotalRating(store);
    }

    public List<com.matdori.matdori.repositoy.Dto.StoreListByCategory> findByCategory(String category, int startIndex) { return storeRepository.findByCategory(StoreCategory.nameOf(category), startIndex);}

    /**
     * 가게에 매핑된 족보의 총 개수 조회하기.
     */
    public int countAllJokbos(Long id) {

        return jokboRepository.countAllAtStore(id);
    }

    /**
     * 해당 가게의 모든 별점별 평균 구하기
     */
    public com.matdori.matdori.repositoy.Dto.StoreRatings getAllRatings(Store store) {

        return storeRepository.getAllRatings(store);
    }

    /**
     * 카테고리 별 총 가게 수
     */
    public Long CountStoresByCategory(String category) { return storeRepository.countStoresByCategory(StoreCategory.nameOf(category));}
}
