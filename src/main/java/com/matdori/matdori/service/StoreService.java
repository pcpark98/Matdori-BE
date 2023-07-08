package com.matdori.matdori.service;

import com.matdori.matdori.domain.Category;
import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.Menu;
import com.matdori.matdori.domain.Store;
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
    public Store findOne(Long id) {return storeRepository.findOne(id); }

    public List<Category> findAllCategoryWithMenu (Long id) { return storeRepository.findAllCategoryWithMenu(id);}

    public List<Jokbo> findAllJokbo(Long storeId) { return jokboRepository.findByStoreIndex(storeId);}

}
