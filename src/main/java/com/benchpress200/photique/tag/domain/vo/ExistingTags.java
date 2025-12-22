package com.benchpress200.photique.tag.domain.vo;

import com.benchpress200.photique.tag.domain.entity.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExistingTags {
    private final Set<Tag> existingTags;

    private ExistingTags(List<Tag> existingTags) {
        this.existingTags = new HashSet<>(existingTags);
    }

    public static ExistingTags of(List<Tag> existingTags) {
        return new ExistingTags(existingTags);
    }

    public List<String> findAbsent(List<String> tags) {
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return tags.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .toList();
    }

    public ExistingTags merge(List<Tag> newTags) {
        Set<Tag> merged = new HashSet<>(existingTags);
        merged.addAll(newTags);

        return new ExistingTags(new ArrayList<>(merged));
    }

    public List<Tag> values() {
        return new ArrayList<>(existingTags);
    }
}
