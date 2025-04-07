package com.benchpress200.photique.common.transaction.rollbackcontext;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import lombok.Getter;
import lombok.Setter;

public class ElasticsearchSingleWorkRollbackContext {
    private static final ThreadLocal<Context> documentToSave = ThreadLocal.withInitial(Context::new);
    private static final ThreadLocal<Context> documentToUpdate = ThreadLocal.withInitial(
            Context::new);
    private static final ThreadLocal<Context> documentToDelete = ThreadLocal.withInitial(
            Context::new);

    public static void addDocumentToSave(final SingleWorkSearch singleWorkSearch) {
        documentToSave.get().setContext(singleWorkSearch);
    }

    public static void addDocumentToUpdate(final SingleWorkSearch singleWorkSearch) {
        documentToUpdate.get().setContext(singleWorkSearch);
    }

    public static void addDocumentToDelete(final SingleWorkSearch singleWorkSearch) {
        documentToDelete.get().setContext(singleWorkSearch);
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

    public static SingleWorkSearch getDocumentToSave() {
        return documentToSave.get().getContext();
    }

    public static SingleWorkSearch getDocumentToUpdate() {
        return documentToUpdate.get().getContext();
    }

    public static SingleWorkSearch getDocumentToDelete() {
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
        private SingleWorkSearch context;

        public Context() {
            context = null;
        }

        public boolean hasContext() {
            return context != null;
        }

    }
}
