package com.benchpress200.photique.common.transaction.rollbackcontext;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import lombok.Getter;
import lombok.Setter;

public class ElasticsearchExhibitionRollbackContext {
    private static final ThreadLocal<Context> documentToSave = ThreadLocal.withInitial(
            Context::new);
    private static final ThreadLocal<Context> documentToUpdate = ThreadLocal.withInitial(
            Context::new);
    private static final ThreadLocal<Context> documentToDelete = ThreadLocal.withInitial(
            Context::new);

    public static void addDocumentToSave(final ExhibitionSearch exhibitionSearch) {
        documentToSave.get().setContext(exhibitionSearch);
    }

    public static void addDocumentToUpdate(final ExhibitionSearch exhibitionSearch) {
        documentToUpdate.get().setContext(exhibitionSearch);
    }

    public static void addDocumentToDelete(final ExhibitionSearch exhibitionSearch) {
        documentToDelete.get().setContext(exhibitionSearch);
    }

    public static boolean hasDocumentToSave() {
        return documentToSave.get().hasContext();
    }

    public static boolean hasDocumentToUpdate() {
        return documentToUpdate.get().hasContext();
    }

    public static boolean hasDocumentToDelete() {
        return documentToDelete.get().hasContext();
    }

    public static ExhibitionSearch getDocumentToSave() {
        return documentToSave.get().getContext();
    }

    public static ExhibitionSearch getDocumentToUpdate() {
        return documentToUpdate.get().getContext();
    }

    public static ExhibitionSearch getDocumentToDelete() {
        return documentToDelete.get().getContext();
    }

    // 저장된 엘라스틱서치 쿼리 초기화
    public static void clear() {
        documentToSave.remove();
        documentToUpdate.remove();
        documentToDelete.remove();
    }

    @Setter
    @Getter
    static class Context {
        private ExhibitionSearch context;

        public Context() {
            context = null;
        }

        public boolean hasContext() {
            return context != null;
        }

    }
}
