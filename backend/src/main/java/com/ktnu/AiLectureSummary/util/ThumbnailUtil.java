package com.ktnu.AiLectureSummary.util;

import java.util.Base64;

public class ThumbnailUtil {
    public static String encodeThumbnailSafe(byte[] thumbnail) {
        return thumbnail != null ? Base64.getEncoder().encodeToString(thumbnail) : null;
    }

}
