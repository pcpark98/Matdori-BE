package com.matdori.matdori.service;

import com.matdori.matdori.domain.Store;
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
    public List<Store> findAll() { return storeRepository.findAll(); }
    public Store findOne(Long id) {return storeRepository.findOne(id); }

}
