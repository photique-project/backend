package com.benchpress200.photique.profile;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/profiles")
public class ProfileController {
    private static final String BLUE_CONTAINER = "blue";
    private static final String GREEN_CONTAINER = "green";

    private final Environment environment;

    @GetMapping
    public String profile() {
        final List<String> profiles = Arrays.asList(environment.getActiveProfiles());
        final List<String> prodProfiles = Arrays.asList(BLUE_CONTAINER, GREEN_CONTAINER);
        final String defaultProfile = profiles.get(0);

        return Arrays.stream(environment.getActiveProfiles())
                .filter(prodProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}
