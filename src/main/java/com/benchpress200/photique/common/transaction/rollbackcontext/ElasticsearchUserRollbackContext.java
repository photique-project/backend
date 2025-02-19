package com.benchpress200.photique.common.transaction.rollbackcontext;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import lombok.Getter;
import lombok.Setter;

public class ElasticsearchUserRollbackContext {
    private static final ThreadLocal<Context> documentToSave = ThreadLocal.withInitial(Context::new);
    private static final ThreadLocal<Context> documentToUpdate = ThreadLocal.withInitial(Context::new);
    private static final ThreadLocal<Context> documentToDelete = ThreadLocal.withInitial(Context::new);

    public static void addDocumentToSave(final UserSearch userSearch) {
        documentToSave.get().setContext(userSearch);
    }

    public static void addDocumentToUpdate(final UserSearch userSearch) {
        documentToUpdate.get().setContext(userSearch);
    }

    public static void addDocumentToDelete(final UserSearch userSearch) {
        documentToDelete.get().setContext(userSearch);
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

    public static UserSearch getDocumentToSave() {
        return documentToSave.get().getContext();
    }

    public static UserSearch getDocumentToUpdate() {
        return documentToUpdate.get().getContext();
    }

    public static UserSearch getDocumentToDelete() {
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
        private UserSearch context;

        public Context() {
            context = null;
        }

        public boolean hasContext() {
            return context != null;
        }

    }
}
