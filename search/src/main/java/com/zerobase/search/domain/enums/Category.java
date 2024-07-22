package com.zerobase.search.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    LANGUAGE("언어"),
    DEGREE("학위"),
    EXAM("시험"),
    IT("IT"),
    MUSIC("음악"),
    ART("미술"),
    SPORTS("스포츠"),
    LITERATURE("문학"),
    PHOTOGRAPHY("사진"),
    VIDEO("영상"),
    OTHER("기타");

    private final String displayName;
}
