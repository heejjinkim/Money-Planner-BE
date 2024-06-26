package com.umc5th.muffler.domain.member.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

@Component
@ConfigurationProperties(prefix = "social-login.apple")
@Getter
@Setter
@RequiredArgsConstructor
public class AppleProperties {
    private String grantType;
    private String clientId;
    private String keyId;
    private String teamId;
    private String audience;
    private String privateKeyPath;
    private String privateKey;

    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void init() throws IOException {
        Resource resource = resourceLoader.getResource(privateKeyPath);
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            this.privateKey = FileCopyUtils.copyToString(reader);
        }
        this.privateKey = processKey(this.privateKey);
    }

    private String processKey(String pemKey) {
        return pemKey.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
    }
}
