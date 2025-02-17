package com.benchpress200.photique.common.transaction.rollbackcontext;

import java.util.ArrayList;
import java.util.List;

public class ImageRollbackContext {
    private static final ThreadLocal<List<String>> uploadedImages = ThreadLocal.withInitial(ArrayList::new);

    // 이미지 업로드 URL 저장
    public static void addUploadedImage(final String imageUrl) {
        uploadedImages.get().add(imageUrl);
    }

    // 업로드된 이미지 URL 목록 가져오기
    public static List<String> getUploadedImages() {
        return new ArrayList<>(uploadedImages.get());
    }

    // 저장된 이미지 목록 초기화
    public static void clear() {
        uploadedImages.remove();
    }
}
