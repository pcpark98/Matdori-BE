package com.matdori.matdori.service;

import com.matdori.matdori.domain.Category;
import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.Menu;
import com.matdori.matdori.domain.Store;
import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.NotExistStoreException;
import com.matdori.matdori.repositoy.JokboRepository;
import com.matdori.matdori.repositoy.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public com.matdori.matdori.repositoy.Dto.StoreInformationHeader readStoreInformationHeader(Long storeId) { return storeRepository.readStoreInformationHeader(storeId);}

    /**
     * 해당 가게의 별점 평균 구하기
     */
    public Double getTotalRating(Long id) {
        Store store = storeRepository.findOne(id);
        return storeRepository.getTotalRating(store);
    }

}
