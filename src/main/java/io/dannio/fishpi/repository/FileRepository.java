package io.dannio.fishpi.repository;

import io.dannio.fishpi.entity.TelegramFile;
import io.dannio.fishpi.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface FileRepository extends JpaRepository<TelegramFile, BigInteger> {

    TelegramFile getByFileId(String fileId);

    TelegramFile getByFilePath(String filePath);

}
