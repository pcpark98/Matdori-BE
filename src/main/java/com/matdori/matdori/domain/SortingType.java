package com.matdori.matdori.domain;

import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.NotExistedDepartmentException;

public enum SortingType {
    LASTEST("최신순"),
    HIGHEST_RATING("별점 높은 순"),
    MOST_JOKBOS("족보 많은 순"),
    MOST_FAVORITES("좋아요 많은 순");
    private final String name;
    public String getName() { return this.name;}
    SortingType(String name) { this.name = name;}

    public static SortingType nameOf(String name){
        for(SortingType _name : SortingType.values()){
            if(_name.getName().equals(name))
                return _name;
        }
        throw new NotExistedDepartmentException(ErrorCode.NOT_EXISTED_SORTING_TYPE);
    }

}
