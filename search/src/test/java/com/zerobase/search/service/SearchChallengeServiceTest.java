package com.zerobase.search.service;

import com.zerobase.search.domain.dto.ChallengeResponseDto;
import com.zerobase.search.domain.entity.ChallengeEntity;
import com.zerobase.search.domain.enums.ChallengeStatus;
import com.zerobase.search.domain.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SearchChallengeServiceTest {
    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private SearchChallengeService searchChallengeService;

    private ChallengeEntity challengeEntity1;
    private ChallengeEntity challengeEntity2;
    private List<ChallengeEntity> challengeEntities;

    @BeforeEach
    void setUp() {
        challengeEntity1 = ChallengeEntity.builder()
                .id(1L)
                .title("Run")
                .userNickName("JohnDoe")
                .status(ChallengeStatus.ONGOING)
                .build();

        challengeEntity2 = ChallengeEntity.builder()
                .id(2L)
                .title("Swim")
                .userNickName("JaneDoe")
                .status(ChallengeStatus.COMPLETED)
                .build();

        challengeEntities = List.of(challengeEntity1, challengeEntity2);
    }

    //검색어가 없을 때
    @Test
    void searchChallenges_NoSearchCriteria() {
        //given
        challengeEntity1 = ChallengeEntity.builder()
                .id(1L)
                .title("Eng")
                .userNickName("Kim")
                .status(ChallengeStatus.ONGOING)
                .build();

        challengeEntity2 = ChallengeEntity.builder()
                .id(2L)
                .title("Swim")
                .userNickName("Kim")
                .status(ChallengeStatus.COMPLETED)
                .build();

        challengeEntities = List.of(challengeEntity1, challengeEntity2);

        Page<ChallengeEntity> page = new PageImpl<>(challengeEntities);
        given(challengeRepository.findAll(any(Pageable.class))).willReturn(page);
        //when
        List<ChallengeResponseDto.ChallengeSimpleDto> result =
                searchChallengeService.searchChallenges(
                        null, null, null, 1, 10);
        //then
        assertEquals(2, result.size());
        assertEquals("Eng", result.get(0).getTitle());
        assertEquals("Swim", result.get(1).getTitle());
    }

    //타이틀 검색
    @Test
    void testSearchChallenges_Title() {
        //given
        challengeEntity1 = ChallengeEntity.builder()
                .id(1L)
                .title("Eng")
                .userNickName("Kim")
                .status(ChallengeStatus.ONGOING)
                .build();

        challengeEntity2 = ChallengeEntity.builder()
                .id(2L)
                .title("Swim")
                .userNickName("Kim")
                .status(ChallengeStatus.COMPLETED)
                .build();

        challengeEntities = List.of(challengeEntity1, challengeEntity2);

        given(challengeRepository.searchChallenges(anyString(), any(), any()))
                .willReturn(List.of(challengeEntity1));

        // when
        List<ChallengeResponseDto.ChallengeSimpleDto> result =
                searchChallengeService.searchChallenges("eng", null, null, 1, 10);

        // then
        assertEquals(1, result.size());
        assertEquals("Eng", result.get(0).getTitle());
    }

    @Test
    void testSearchChallengesWithUserNickName() {
        //given
        challengeEntity1 = ChallengeEntity.builder()
                .id(1L)
                .title("Eng")
                .userNickName("Kim")
                .status(ChallengeStatus.ONGOING)
                .build();

        challengeEntity2 = ChallengeEntity.builder()
                .id(2L)
                .title("Swim")
                .userNickName("Kim")
                .status(ChallengeStatus.COMPLETED)
                .build();

        challengeEntities = List.of(challengeEntity1, challengeEntity2);

        given(challengeRepository.searchChallenges(any(), anyString(), any()))
                .willReturn(List.of(challengeEntity1));
        //when
        List<ChallengeResponseDto.ChallengeSimpleDto> result =
                searchChallengeService.searchChallenges(
                        null, "Kim", null, 1, 10);

        //then
        assertEquals(1, result.size());
        assertEquals("Kim", result.get(0).getUserNickName());
    }

    @Test
    void testSearchChallengesWithStatus() {
        //given
        challengeEntity1 = ChallengeEntity.builder()
                .id(1L)
                .title("Eng")
                .userNickName("Kim")
                .status(ChallengeStatus.ONGOING)
                .build();

        challengeEntity2 = ChallengeEntity.builder()
                .id(2L)
                .title("Swim")
                .userNickName("Kim")
                .status(ChallengeStatus.COMPLETED)
                .build();

        challengeEntities = List.of(challengeEntity1, challengeEntity2);

        given(challengeRepository.searchChallenges(any(), any(), any(ChallengeStatus.class)))
                .willReturn(List.of(challengeEntity2));
        //when
        List<ChallengeResponseDto.ChallengeSimpleDto> result =
                searchChallengeService.searchChallenges(
                        null, null, ChallengeStatus.COMPLETED, 1, 10);
        //then
        assertEquals(1, result.size());
        assertEquals(ChallengeStatus.COMPLETED, result.get(0).getStatus());
    }
}