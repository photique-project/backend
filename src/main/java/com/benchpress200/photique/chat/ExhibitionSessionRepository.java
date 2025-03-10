package com.benchpress200.photique.chat;

import com.benchpress200.photique.chat.domain.entity.ExhibitionSession;
import org.springframework.data.repository.CrudRepository;

public interface ExhibitionSessionRepository extends CrudRepository<ExhibitionSession, String> {
}
