package io.dannio.fishpi.entity;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = {@Index(columnList = "messageId")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public @Data class FishpiRedPacket extends AbstractPersistable<Long> {

    private Long redPacketId;

    private Integer messageId;

    private Integer size;

    private String messageContent;

}
