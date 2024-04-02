package com.umc5th.muffler.global.config;

import com.umc5th.muffler.global.util.DateTimeProvider;
import com.umc5th.muffler.global.util.DefaultDateTimeProvider;
import com.umc5th.muffler.message.service.sender.Sender;
import com.umc5th.muffler.message.service.sender.impl.ConsoleMessageSender;
import com.umc5th.muffler.message.service.sender.impl.FirebaseMessageSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration
public class AppConfig {
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new DefaultDateTimeProvider();
    }

    @Bean
    public Sender Sender() { return new FirebaseMessageSender(); }
}
