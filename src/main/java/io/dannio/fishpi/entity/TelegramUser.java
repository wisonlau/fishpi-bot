package io.dannio.fishpi.entity;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = {@Index(columnList = "telegramId")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class TelegramUser extends AbstractPersistable<Long> {

    private Long telegramId;

    private String fishId;

    private String apiKey;

    private String fishName;

    private String fishPassword;

}
