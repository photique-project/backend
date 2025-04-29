package com.benchpress200.photique.common.transaction.rollbackcontext;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ElasticsearchUserRollbackContext {
    private static final ThreadLocal<Context> documentToSave = ThreadLocal.withInitial(Context::new);
    private static final ThreadLocal<Context> documentToDelete = ThreadLocal.withInitial(Context::new);

    public static void addDocumentToSave(final UserSearch userSearch) {
        documentToSave.get().add(userSearch);
    }

    public static void addDocumentToDelete(final UserSearch userSearch) {
        documentToDelete.get().add(userSearch);
    }

    public static boolean hasDocumentToSave() {
        return documentToSave.get().hasContext();
    }

    public static boolean hasDocumentToDelete() {
        return documentToDelete.get().hasContext();
    }

    public static List<UserSearch> getDocumentToSave() {
        return documentToSave.get().getContext();
    }

    public static List<UserSearch> getDocumentToDelete() {
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
        private List<UserSearch> context;

        public Context() {
            context = new ArrayList<>();
        }

        public boolean hasContext() {
            return !context.isEmpty();
        }

        public void add(final UserSearch userSearch) {
            context.add(userSearch);
        }
    }
}
