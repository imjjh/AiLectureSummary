package com.ktnu.AiLectureSummary.util;

import java.util.Base64;

public class ThumbnailUtil {
    public static String encodeBase64ThumbnailSafe(byte[] thumbnail) {
        return thumbnail != null ? Base64.getEncoder().encodeToString(thumbnail) : null;
    }

    public static byte[] decodeBase64ThumbnailSafe(String thumbnail) {
        return thumbnail != null ? Base64.getDecoder().decode(thumbnail) : null;
    }

}
