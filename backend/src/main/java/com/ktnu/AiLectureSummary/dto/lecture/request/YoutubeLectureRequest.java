package com.ktnu.AiLectureSummary.dto.lecture.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class YoutubeLectureRequest {


    // youtube.com/watch?v= 또는 youtu.be/ 형식의 유효한 URL만 허용
    @NotBlank(message = "YouTube 링크는 필수입니다.")
    @Pattern(
            regexp = "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}(&.+)?$",
        message = "유효한 YouTube 링크 형식이어야 합니다."
    )
    @Schema(example = "https://www.youtube.com/watch?v=00yJy7W0DQE&list=PLfLgtT94nNq0qTRunX9OEmUzQv4lI4pnP")
    private String url;
}
