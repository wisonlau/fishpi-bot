package io.dannio.fishpi.repository;

import io.dannio.fishpi.entity.SuperGroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MessageRepository extends JpaRepository<SuperGroupMessage, BigInteger> {

    SuperGroupMessage getByFishMsgId(Long fishMsgId);

    SuperGroupMessage getByMessageId(Integer messageId);
}
