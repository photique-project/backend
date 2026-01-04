package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.event.ExhibitionCreateEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionDeleteEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionUpdateEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionWorkImageUploadEvent;

public interface ExhibitionEventPublishPort {
    void publishExhibitionWorkImageUploadEvent(ExhibitionWorkImageUploadEvent event);

    void publishExhibitionCreateEvent(ExhibitionCreateEvent event);

    void publishExhibitionUpdateEvent(ExhibitionUpdateEvent event);

    void publishExhibitionDeleteEvent(ExhibitionDeleteEvent event);
}
