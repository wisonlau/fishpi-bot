package io.dannio.fishpi.repository;

import io.dannio.fishpi.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserRepository extends JpaRepository<TelegramUser, BigInteger> {

    TelegramUser getByTelegramId(Long telegramId);
}
