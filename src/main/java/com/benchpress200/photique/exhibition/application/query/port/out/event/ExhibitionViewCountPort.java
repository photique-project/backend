package com.benchpress200.photique.exhibition.application.query.port.out.event;

public interface ExhibitionViewCountPort {
    void incrementViewCount(Long exhibitionId);
}
