package com.benchpress200.photique.common.transaction.rollbackcontext;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ElasticsearchExhibitionRollbackContext {
    private static final ThreadLocal<Context> documentToSave = ThreadLocal.withInitial(
            Context::new);

    private static final ThreadLocal<Context> documentToDelete = ThreadLocal.withInitial(
            Context::new);

    public static void addDocumentToSave(final ExhibitionSearch exhibitionSearch) {
        documentToSave.get().add(exhibitionSearch);
    }


    public static void addDocumentToDelete(final ExhibitionSearch exhibitionSearch) {
        documentToDelete.get().add(exhibitionSearch);
    }

    public static boolean hasDocumentToSave() {
        return documentToSave.get().hasContext();
    }


    public static boolean hasDocumentToDelete() {
        return documentToDelete.get().hasContext();
    }

    public static List<ExhibitionSearch> getDocumentToSave() {
        return documentToSave.get().getContext();
    }


    public static List<ExhibitionSearch> getDocumentToDelete() {
        return documentToDelete.get().getContext();
    }

    // 저장된 엘라스틱서치 쿼리 초기화
    public static void clear() {
        documentToSave.remove();
        documentToDelete.remove();
    }

    @Setter
    @Getter
    static class Context {
        private List<ExhibitionSearch> context;

        public Context() {
            context = new ArrayList<>();
        }

        public boolean hasContext() {
            return !context.isEmpty();
        }

        public void add(final ExhibitionSearch exhibitionSearch) {
            context.add(exhibitionSearch);
        }
    }
}
