package com.zerobase.challenge.domain.dto;

import com.zerobase.challenge.domain.enums.Category;

import java.time.LocalDate;

public interface ChangeChallengeForm {

    Long getUserId();
    Category getCategory();
    String getGoal();
    LocalDate getStartDate();
    LocalDate getDueDate();
    String getDescription();
}
