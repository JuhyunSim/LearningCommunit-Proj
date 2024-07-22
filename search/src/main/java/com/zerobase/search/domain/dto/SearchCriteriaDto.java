package com.zerobase.search.domain.dto;

import com.zerobase.search.domain.enums.Category;
import com.zerobase.search.domain.enums.ChallengeStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchCriteriaDto {
    private String title;
    private String username;
    private ChallengeStatus status;
    private Category category;
}
