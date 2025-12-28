package com.benchpress200.photique.singlework.domain.entity;

import com.benchpress200.photique.singlework.domain.entity.id.SingleWorkTagId;
import com.benchpress200.photique.tag.domain.entity.Tag;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "singlework_tags")
public class SingleWorkTag {
    @EmbeddedId
    private SingleWorkTagId id;

    @MapsId("singleWorkId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singlework_id", nullable = false)
    private SingleWork singleWork;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public SingleWorkTag(
            SingleWork singleWork,
            Tag tag
    ) {
        this.singleWork = singleWork;
        this.tag = tag;

        this.id = new SingleWorkTagId(
                singleWork.getId(),
                tag.getId()
        );
    }

    public static SingleWorkTag of(
            SingleWork singleWork,
            Tag tag
    ) {
        return new SingleWorkTag(singleWork, tag);
    }
}
