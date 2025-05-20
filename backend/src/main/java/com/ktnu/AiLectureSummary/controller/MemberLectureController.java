package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.dto.lecture.LectureDetailResponse;
import com.ktnu.AiLectureSummary.dto.lecture.LectureListItemResponse;
import com.ktnu.AiLectureSummary.dto.lecture.LectureResponse;
import com.ktnu.AiLectureSummary.dto.lecture.PersonalNoteRequest;
import com.ktnu.AiLectureSummary.security.principal.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.MemberLectureService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-lectures")
public class MemberLectureController {

    private final MemberLectureService memberLectureService;


    /**
     * 로그인한 사용자가 등록한 강의들의 제목 및 간략 정보를 반환합니다.
     *
     * @param user 로그인한 사용자 정보
     * @return 사용자가 등록한 강의목록과 200 ok 상태 코드
     */
    @GetMapping("/dashboard")
    @Operation(
            summary = "내 강의 목록 조회",
            description = "로그인한 사용자가 등록한 강의들의 제목 및 간략 정보를 반환합니다."
    )
    public ResponseEntity<List <LectureListItemResponse>> dashBoard(@AuthenticationPrincipal CustomUserDetails user){
        // 현재 로그인한 사용자의 강의 목록 조회
        List<LectureListItemResponse> userLectureList = memberLectureService.getUserLectureList(user);
        // 응답 상태코드 200 OK로 반환
        return ResponseEntity.ok(userLectureList);
    }

    /**
     * 로그인한 사용자가 등록한 특정 강의의 전체 요약 내용을 조회합니다. (제목, 원문, AI 요약, 사용자 메모 포함)
     *
     * @param user 로그인한 사용자 정보
     * @param lectureId
     * @return 사용자가 등록한 강의 상세 정보
     */
    @GetMapping("/{lectureId}")
    @Operation(summary = "내 특정 강의 상세 조희",description = "로그인한 사용자가 등록한 특정 강의의 전체 요약 내용을 조회합니다. (제목, 원문, AI 요약, 사용자 메모 포함)")
    public ResponseEntity<LectureDetailResponse> getLectureDetail(@AuthenticationPrincipal CustomUserDetails user,@PathVariable Long lectureId){
        LectureDetailResponse lectureDetailResponse = memberLectureService.getLectureDetail(user, lectureId);
        return ResponseEntity.ok(lectureDetailResponse);
    }


    /**
     * 로그인한 사용자가 등록한 특정 강의의 개인 메모를 작성합니다.
     *
     * @param user 로그인한 사용자 정보
     * @param lectureId
     * @param request
     * @return 사용자가 등록한 강의 상세 정보
     */
    @Transactional
    @PostMapping("/{lectureId}/my-note")
    @Operation(summary = "내 특정 강의 개인 메모",description = "로그인한 사용자가 등록한 특정 강의의 개인 메모를 작성합니다.")
    public ResponseEntity<LectureDetailResponse> writePersonalNote(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long lectureId, @RequestBody @Valid PersonalNoteRequest request
    ){
        LectureDetailResponse lectureDetailResponse = memberLectureService.writePersonalNote(user, lectureId, request.getNote());

        return ResponseEntity.ok(lectureDetailResponse);
    }
}
