package io.github.shivibhardwaj.seatrush.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//Without this, @CreatedDate and @LastModifiedDate do nothing
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
