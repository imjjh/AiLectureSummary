package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.dto.lecture.LectureListItemResponse;
import com.ktnu.AiLectureSummary.dto.lecture.LectureResponse;
import com.ktnu.AiLectureSummary.security.principal.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.MemberLectureService;
import com.ktnu.AiLectureSummary.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-lectures")
public class MemberLectureController {

    private final MemberLectureService memberLectureService;

    @GetMapping("/dashboard")
    @Operation(
            summary = "내 강의 목록 조회",
            description = "로그인한 사용자가 등록한 강의들의 제목 및 간략 정보를 반환합니다."
    )
    public ResponseEntity<List <LectureListItemResponse>> dashBoard(@AuthenticationPrincipal CustomUserDetails user){
        // s현재 로그인한 사용자의 강의 목록 조회
        List<LectureListItemResponse> userLectureList = memberLectureService.getUserLectureList(user);
        // 응답 상태코드 200 OK로 반환
        return ResponseEntity.ok(userLectureList);
    }
}
