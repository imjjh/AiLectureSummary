package com.ktnu.AiLectureSummary.controller;
import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.CustomTitleRequest;
import com.ktnu.AiLectureSummary.dto.memberLecture.LectureDetailResponse;
import com.ktnu.AiLectureSummary.dto.lecture.PersonalNoteRequest;
import com.ktnu.AiLectureSummary.dto.memberLecture.MemberLectureListResponse;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.MemberLectureService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<MemberLectureListResponse>>dashBoard(@AuthenticationPrincipal CustomUserDetails user){
        // 현재 로그인한 사용자의 강의 목록 조회
        MemberLectureListResponse memberLectureListResponse = memberLectureService.getUserLectureList(user);
        // 응답 상태코드 200 OK로 반환
        return ResponseEntity.ok(ApiResponse.success("내 강의 목록 조회 성공", memberLectureListResponse));
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
    public ResponseEntity<ApiResponse<LectureDetailResponse>> getLectureDetail(@AuthenticationPrincipal CustomUserDetails user,@PathVariable Long lectureId){
        LectureDetailResponse lectureDetailResponse = memberLectureService.getLectureDetail(user, lectureId);
        return ResponseEntity.ok(ApiResponse.success("강의 상세 조회 성공", lectureDetailResponse));
    }


    /**
     * 로그인한 사용자가 등록한 특정 강의의 개인 메모를 저장합니다.
     * 기존 메모가 있을 경우 덮어씁니다.
     *
     * @param user 로그인한 사용자 정보
     * @param lectureId 강의 ID
     * @param request 메모 요청 본문
     * @return 수정된 강의 상세 정보
     */
    @Transactional
    @PatchMapping("/{lectureId}/memo")
    @Operation(summary = "내 특정 강의 메모 저장", description = "로그인한 사용자가 등록한 특정 강의의 메모를 저장합니다. 기존 메모가 있다면 수정합니다.")
    public ResponseEntity<ApiResponse<LectureDetailResponse>> saveMemo(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long lectureId,
            @RequestBody @Valid PersonalNoteRequest request
    ) {
        LectureDetailResponse lectureDetailResponse = memberLectureService.saveMemo(user, lectureId, request.getNote());
        return ResponseEntity.ok(ApiResponse.success("개인 메모 저장 성공", lectureDetailResponse));
    }


    /**
     * 로그인한 사용자가 등록한 특정 강의의 개인 메모를 삭제합니다.
     *
     * @param user 로그인한 사용자 정보
     * @param lectureId 강의 ID
     * @return 수정된 강의 상세 정보
     */
    @Transactional
    @DeleteMapping("/{lectureId}/memo")
    @Operation(summary = "내 특정 강의 메모 삭제", description = "로그인한 사용자가 등록한 특정 강의의 메모를 삭제합니다.")
    public ResponseEntity<ApiResponse<LectureDetailResponse>> deleteMemo(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long lectureId
    ) {
        LectureDetailResponse lectureDetailResponse = memberLectureService.deleteMemo(user, lectureId);
        return ResponseEntity.ok(ApiResponse.success("개인 메모 삭제 성공", lectureDetailResponse));
    }

    /**
     * 강의 제목을 저장합니다.
     *
     * @param user 로그인한 사용자 정보
     * @param lectureId 강의 Id
     * @param requests 수정할 제목 DTO
     * @return 수정된 강의 상세 정보
     */
    @Transactional
    @PatchMapping("/{lectureId}/title")
    @Operation(summary = "내 특정 강의 제목 수정", description = "로그인 한 사용자가 등록한 특정 강의의 제목을 수정합니다.")
    public ResponseEntity<ApiResponse<LectureDetailResponse>> saveLectureTitle(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long lectureId, @RequestBody @Valid CustomTitleRequest requests){
        LectureDetailResponse lectureDetailResponse = memberLectureService.updateCustomTitle(user,lectureId,requests.getTitle());

        return ResponseEntity.ok(ApiResponse.success("강의 제목 수정 성공", lectureDetailResponse));
    }
}
