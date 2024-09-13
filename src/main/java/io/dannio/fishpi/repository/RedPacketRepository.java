package io.dannio.fishpi.repository;

import io.dannio.fishpi.entity.FishpiRedPacket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface RedPacketRepository extends JpaRepository<FishpiRedPacket, BigInteger> {

    FishpiRedPacket getByRedPacketId(Long redPacketId);


}
