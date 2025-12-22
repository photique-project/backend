package com.benchpress200.photique.tag.domain.vo;

import com.benchpress200.photique.tag.domain.entity.Tag;
import java.util.ArrayList;
import java.util.List;

public class AbsentTags {
    private final List<Tag> absentTags;

    private AbsentTags(List<String> absentTagNames) {
        this.absentTags = absentTagNames.stream()
                .map(Tag::of)
                .toList();
    }

    public static AbsentTags of(List<String> absentTagNames) {
        return new AbsentTags(absentTagNames);
    }

    public List<Tag> values() {
        return new ArrayList<>(absentTags);
    }
}
