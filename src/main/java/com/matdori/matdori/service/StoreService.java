package com.matdori.matdori.service;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.NotExistStoreException;
import com.matdori.matdori.repositoy.Dto.MatdoriTop3;
import com.matdori.matdori.repositoy.Dto.RecommendedMenu;
import com.matdori.matdori.repositoy.Dto.RecommendedStore;
import com.matdori.matdori.repositoy.Dto.StoreInformationHeader;
import com.matdori.matdori.repositoy.JokboFavoriteRepository;
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
    private final JokboFavoriteRepository jokboFavoriteRepository;
    public List<Store> findAll() { return storeRepository.findAll(); }
    public Store findOne(Long id) {
        Store store = storeRepository.findOne(id);
        if(store == null) throw new NotExistStoreException(ErrorCode.NOT_EXISTED_STORE);
        return store;
    }

    public List<Category> findAllCategoryWithMenu (Long id) { return storeRepository.findAllCategoryWithMenu(id);}

    public List<Jokbo> findAllJokbo(Long storeId, Double cursor, String sortingType, Long jokboIndex) {
        if(cursor == null)
            return jokboRepository.findByStoreIndex(storeId, SortingType.nameOf(sortingType));

        return jokboRepository.findJokbosDescendingById(storeId, cursor, SortingType.nameOf(sortingType) , jokboIndex);
    }

    public Long countStoreJokbo(Long storeId) { return storeRepository.countStoreJokbo(storeId);}

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

    public List<com.matdori.matdori.repositoy.Dto.StoreListByCategory> findByCategory(String category, Double cursor, String sortingType, Long storeIndex) {
        if(cursor == null) // 커서값이 없는 경우 
            return storeRepository.getCategoryStoresDescendingById(StoreCategory.nameOf(category), SortingType.nameOf(sortingType));
        else // 커서값이 있는 경우
            return storeRepository.findByCategory(StoreCategory.nameOf(category), cursor, SortingType.nameOf(sortingType), storeIndex);
    }

    /**
     * 가게에 매핑된 족보의 총 개수 조회하기.
     */
    public int countAllJokbos(Long id) {

        return jokboRepository.countAllAtStore(id);
    }



    /**
     * 가게에서 가장 인기있는 족보 조회
     */
    public Optional<Jokbo> readPopularJokboAtStore(Long storeId) {return jokboRepository.readPopularJokboatStore(storeId);}

    public Long readFavoriteStoreIndex(Long userId, Long storeId) {
        Optional<Long> favoriteStoreIndex = jokboFavoriteRepository.readFavoriteStoreIndex(userId, storeId);
        if(favoriteStoreIndex.isPresent())
            return favoriteStoreIndex.get();
        else
            return null;
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

    /**
     * 가게 추천 받기
     */
    public List<RecommendedStore> getRecommendedStore(){ return storeRepository.getRecommendedStore();}

    /**
     * 메뉴 추천 받기
     */
    public List<RecommendedMenu> getRecommendedMenu() {return storeRepository.getRecommendedMenu();}

    /**
     * 맛도리 top 3
     */

    public List<MatdoriTop3> getMatdoriTop3(String order) { return storeRepository.getMatdoriTop3(order);}
}
