package com.benchpress200.photique.common.transaction.rollbackcontext;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import java.util.ArrayList;
import java.util.List;

public class ElasticsearchUserRollbackContext {
    private static final ThreadLocal<List<UserSearch>> documentsToSave = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<List<UserSearch>> documentsToUpdate = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<List<UserSearch>> documentsToDelete = ThreadLocal.withInitial(ArrayList::new);

    public static void addDocumentsToSave(final UserSearch userSearch) {
        documentsToSave.get().add(userSearch);
    }

    public static void addDocumentsToUpdate(final UserSearch userSearch) {
        documentsToUpdate.get().add(userSearch);
    }

    public static void addDocumentsToDelete(final UserSearch userSearch) {
        documentsToDelete.get().add(userSearch);
    }


    public static List<UserSearch> getDocumentsToSave() {
        return new ArrayList<>(documentsToSave.get());
    }

    public static List<UserSearch> getDocumentsToUpdate() {
        return new ArrayList<>(documentsToUpdate.get());
    }

    public static List<UserSearch> getDocumentsToDelete() {
        return new ArrayList<>(documentsToDelete.get());
    }

    // 저장된 엘라스틱서치 쿼리 초기화
    public static void clear() {
        documentsToSave.remove();
        documentsToUpdate.remove();
        documentsToDelete.remove();
    }
}
