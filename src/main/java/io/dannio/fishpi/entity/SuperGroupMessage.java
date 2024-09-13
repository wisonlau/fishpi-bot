package io.dannio.fishpi.entity;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = {@Index(columnList = "fishMsgId,messageId")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data
class SuperGroupMessage extends AbstractPersistable<Long> {

    private Integer messageId;

    private Long fishMsgId;
}
