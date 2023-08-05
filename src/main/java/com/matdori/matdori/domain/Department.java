package com.matdori.matdori.domain;

import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.NotExistStoreCategoryException;
import com.matdori.matdori.exception.NotExistedDepartmentException;

import java.util.ArrayList;
import java.util.List;

public enum Department {
    BUSINESS_ADMINISTRATION("경영학과"),
    ASIA_PACIFIC_SCHOOL_OF_LOGISTICS("아태물류학부"),
    GLOBAL_FINANCE_AND_BANKING("글로벌금융학과"),
    INTERNATIONAL_TRADE("국제통상학과"),
    KOREAN_LANGUAGE_EDUCATION("국어교육과"),
    ENGLISH_EDUCATION("영어교육과"),
    SOCIAL_STUDIES_EDUCATION("사회교육과"),
    EDUCATION("교육학과"),
    PHYSICAL_EDUCATION("체육교육과"),
    MATHEMATICS_EDUCATION("수학교육과"),
    KOREAN_LANGUAGE_AND_LITERATURE("한국어문학과"),
    HISTORY("사학과"),
    PHILOSOPHY("철학과"),
    CHINA_STUDIES("중국학과"),
    JAPANESE_STUDIES("일본언어문화학과"),
    ENGLISH_LANGUAGE_AND_LITERATURE("영어영문학과"),
    FRENCH_LANGUAGE_AND_CULTURE("프랑스언어문화학과"),
    CULTURAL_CONTENTS_AND_MANAGEMENT("문화콘텐츠문화경영학과"),
    ECONOMICS("경제학과"),
    PUBLIC_ADMINISTRATION("행정학과"),
    POLITICAL_SCIENCE_AND_INTERNATIONAL_RELATIONS("정치외교학과"),
    MEDIA_AND_COMMUNICATION("미디어커뮤니케이션학과"),
    CONSUMER_SCIENCE("소비자학과"),
    CHILD_STUDIES("아동심리학과"),
    SOCIAL_WELFARE_STUDIES("사회복지학과"),
    FINE_ARTS("조형예술학과"),
    DESIGN_CONVERGENCE("디자인융합학과"),
    KINESIOLOGY("스포츠과학과"),
    THEATER_AND_FILM_STUDIES("연극영화학과"),
    FASHION_DESIGN_AND_TEXTILES("의류디자인학과"),
    MECHANICAL_ENGINEERING("기계공학과"),
    AEROSPACE_ENGINEERING("항공우주공학과"),
    NAVAL_ARCHITECTURE_AND_OCEAN_ENGINEERING("조선해양공학과"),
    INDUSTRIAL_ENGINEERING("산업경영공학과"),
    CHEMICAL_ENGINEERING("화학공학과"),
    BIOLOGICAL_ENGINEERING("생명공학과"),
    POLYMER_SCIENCE_AND_ENGINEERING("고분자공학과"),
    MATERIALS_SCIENCE_AND_ENGINEERING("신소재공학과"),
    CIVIL_ENGINEERING("사회인프라공학과"),
    ENVIRONMENTAL_ENGINEERING("환경공학과"),
    GEOINFORMATIC_ENGINEERING("공간정보공학과"),
    ARCHITECTURE("건축학부"),
    ENERGY_RESOURCE_ENGINEERING("에너지자원공학과"),
    MATHEMATICS("수학과"),
    STATISTICS("통계학과"),
    PHYSICS("물리학과"),
    CHEMISTRY("화학과"),
    BIOLOGICAL_SCIENCE("생명과학과"),
    OCEANOGRAPHY_SCIENCE("해양과학과"),
    FOOD_AND_NUTRITION("식품영양학과"),
    ARTIFICIAL_INTELLIGENCE("인공지능공학과"),
    DATA_SCIENCE("데이터사이언스학과"),
    SMART_MOBILITY_ENGINEERING("스마트모빌리티공학과"),
    DESIGN_TECHNOLOGY("디자인테크놀로지학과"),
    COMPUTER_ENGINEERING("컴퓨터공학과"),
    MEDICINE("의예과, 의학과"),
    NURSING("간호학과"),
    SCHOOL_OF_INTERDISCIPLINARY_STUDIES("자유전공학부");
    private final String name;
    private Department(String name){
        this.name = name;
    }

    public String getName(){ return this.name;}

    // 한글명을 enum 명으로 바꿔주는 함수
    public static Department nameOf(String name){
        for(Department _name : Department.values()){
            if(_name.getName().equals(name))
                return _name;
        }
        throw new NotExistedDepartmentException(ErrorCode.NOT_EXISTED_DEPARTMENT);
    }

    public static List<String> getDepartmentList(){
        List<String> departments = new ArrayList<>();

        for(Department _name : Department.values()){
                departments.add(_name.getName());
        }
        return departments;
    }
}
