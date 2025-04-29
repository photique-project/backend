package com.benchpress200.photique.common.transaction.rollbackcontext;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ElasticsearchSingleWorkRollbackContext {
    private static final ThreadLocal<Context> documentToSave = ThreadLocal.withInitial(Context::new);
    private static final ThreadLocal<Context> documentToDelete = ThreadLocal.withInitial(
            Context::new);

    public static void addDocumentToSave(final SingleWorkSearch singleWorkSearch) {
        documentToSave.get().add(singleWorkSearch);
    }

    public static void addDocumentToDelete(final SingleWorkSearch singleWorkSearch) {
        documentToDelete.get().add(singleWorkSearch);
    }

    public static boolean hasDocumentToSave() {
        return documentToSave.get().hasContext();
    }

    public static boolean hasDocumentToDelete() {
        return documentToDelete.get().hasContext();
    }

    public static List<SingleWorkSearch> getDocumentToSave() {
        return documentToSave.get().getContext();
    }

    public static List<SingleWorkSearch> getDocumentToDelete() {
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
        private List<SingleWorkSearch> context;

        public Context() {
            context = new ArrayList<>();
        }

        public boolean hasContext() {
            return !context.isEmpty();
        }

        public void add(final SingleWorkSearch singleWorkSearch) {
            context.add(singleWorkSearch);
        }
    }
}
