package io.dannio.fishpi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dannio.fishpi.bot.FishpiBot;
import io.dannio.fishpi.entity.SuperGroupMessage;
import io.dannio.fishpi.entity.FishpiRedPacket;
import io.dannio.fishpi.entity.TelegramFile;
import io.dannio.fishpi.entity.TelegramUser;
import io.dannio.fishpi.properties.DataProperties;
import io.dannio.fishpi.repository.FileRepository;
import io.dannio.fishpi.repository.MessageRepository;
import io.dannio.fishpi.repository.RedPacketRepository;
import io.dannio.fishpi.repository.UserRepository;
import io.github.danniod.fish4j.api.FishApi;
import io.github.danniod.fish4j.entites.ChatroomMessage;
import io.github.danniod.fish4j.entites.Storage;
import io.github.danniod.fish4j.entites.chatroom.*;
import io.github.danniod.fish4j.enums.ChatroomMessageType;
import io.github.danniod.fish4j.exception.FishApiException;
import io.github.danniod.fish4j.param.MessageParam;
import io.github.danniod.fish4j.param.RedPacketOpenParam;
import io.github.danniod.fish4j.param.auth.UserApiParam;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.dannio.fishpi.util.FileUtils.convertByFfmpeg;
import static io.dannio.fishpi.util.FileUtils.downloadFromTelegram;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatroomService {

    @Setter
    private String chatroomGroupId;

    @Setter
    private AbsSender absSender;

    private final FishApi fishApi;

    private final DataProperties dataProperties;

    private final UserRepository userRepository;

    private final FileRepository fileRepository;

    private final MessageRepository messageRepository;

    private final RedPacketRepository redPacketRepository;


    private static final Pattern PATTERN = Pattern.compile("<span class=\"(\\S*-)?extension-message\"/>");

    @SneakyThrows
    public void messageToTelegram(ChatroomMessage message) {

        switch (ChatroomMessageType.fromType(message.getType())) {

            case MSG:
                final ChatMessage chatMessage = (ChatMessage) message;

                final String user = StringUtils.isNotBlank(chatMessage.getUserNickname())
                        ? String.format("%s(%s)", chatMessage.getUserNickname(), chatMessage.getUserName())
                        : chatMessage.getUserName();

                String content = chatMessage.getMarkdownContent();

                final String[] split = content.split("\n");
                if (PATTERN.matcher(split[split.length - 1]).matches()) {
                    content = content.substring(0, content.lastIndexOf("\n" + split[split.length - 1]));
                }
                final String messageContent = String.format("%s:\n%s", user, content);
                log.trace("-> telegram msg[{}]", messageContent);

                final Message executed = absSender.execute(SendMessage.builder()
                        .chatId(chatroomGroupId)
                        .text(messageContent)
                        .build());
                messageRepository.save(SuperGroupMessage.builder()
                        .messageId(executed.getMessageId())
                        .fishMsgId(chatMessage.getId())
                        .build());
                break;
            case ONLINE:
                final OnlineMessage onlineMessage = (OnlineMessage) message;

                break;
            case RED_PACKET:
                final RedPacketMessage redPacketMessage = (RedPacketMessage) message;

                final String sender = StringUtils.isNotBlank(redPacketMessage.getUserNickname())
                        ? String.format("%s(%s)", redPacketMessage.getUserNickname(), redPacketMessage.getUserName())
                        : redPacketMessage.getUserName();
                final String redPacketContent = String.format("%s:\n\uD83E\uDDE7[%s]%s", sender, redPacketMessage.getRedPacket().getType(), redPacketMessage.getRedPacket().getMsg());
                log.trace("-> telegram msg[{}]", redPacketContent);


                final InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder()
                        .keyboardRow(Collections.singletonList(InlineKeyboardButton.builder()
                                .text("Open")
                                .callbackData("{\"id\":" + redPacketMessage.getId() + "}")
                                .build()))
                        .build();

                final Integer messageId = absSender.execute(SendMessage.builder()
                        .chatId(chatroomGroupId)
                        .text(redPacketContent)
                        .replyMarkup(keyboardMarkup)
                        .build()).getMessageId();
                redPacketRepository.save(FishpiRedPacket.builder()
                        .redPacketId(redPacketMessage.getId())
                        .messageId(messageId)
                        .size(redPacketMessage.getRedPacket().getCount())
                        .messageContent(redPacketContent)
                        .build());
                break;
            case RED_PACKET_STATUS:
                final RedPacketStatusMessage status = (RedPacketStatusMessage) message;
                final FishpiRedPacket redPacket = redPacketRepository.getByRedPacketId(status.getId());
                final InlineKeyboardMarkup editKeyboardMarkup = InlineKeyboardMarkup.builder()
                        .keyboardRow(Collections.singletonList(InlineKeyboardButton.builder()
                                .text(String.format("Open(%d/%d)", status.getGot(), status.getCount()))
                                .callbackData("{\"id\":" + status.getId() + "}")
                                .build()))
                        .build();
                absSender.execute(EditMessageText.builder()
                        .chatId(chatroomGroupId)
                        .messageId(redPacket.getMessageId())
                        .text(redPacket.getMessageContent())
                        .replyMarkup(editKeyboardMarkup)
                        .build());
                break;
            case REVOKE:
                final RevokeMessage revokeMessage = (RevokeMessage) message;
                final SuperGroupMessage byFishMsgId = messageRepository.getByFishMsgId(revokeMessage.getId());
                absSender.execute(DeleteMessage.builder()
                        .chatId(chatroomGroupId)
                        .messageId(byFishMsgId.getMessageId())
                        .build());
                break;
            default:
                log.warn("UNKNOWN message type!");
        }

        log.trace("messageToTelegram function passed");
    }


    @SneakyThrows
    public void messageToFishPi(Message message) {

        if (message.hasText()) {
            sendMessage(message.getFrom(), message.getText());
        }

        if (message.hasAnimation() || message.hasSticker() || message.hasPhoto() || message.hasDocument()) {
            String fileId = null;
            if (message.hasAnimation()) {
                fileId = message.getAnimation().getFileId();
            } else if (message.hasSticker()) {
                fileId = message.getSticker().getFileId();
            } else if (message.hasPhoto()) {
                fileId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
            } else if (message.hasDocument()){
                fileId = message.getDocument().getFileId();
            }
            if (fileId == null) {
                log.warn("no fileId found in message[{}]", message);
                return;
            }
            sendMessage(message.getFrom(), String.format("![图片表情](%s)", getUrl(fileId)));
        }

        log.trace("messageToFishPi function passed");

    }


    @SneakyThrows
    private String getUrl(String fileId) {

        final TelegramFile byFileId = fileRepository.getByFileId(fileId);

        if (byFileId != null) {
            return byFileId.getFishUrl();
        }

        final String filePath = absSender.execute(GetFile.builder().fileId(fileId).build()).getFilePath();
        final TelegramFile byFilePath = fileRepository.getByFilePath(filePath);

        if (byFilePath != null) {
            return byFilePath.getFishUrl();
        }

        String path = dataProperties.getTelegram() + File.separator + filePath;
        final String fileUrl = ((FishpiBot) absSender).getFileUrl(filePath);
        final File localFile = downloadFromTelegram(fileUrl, path);
        log.info("url[{}] download to local path[{}], size[{}]", fileUrl, localFile.getAbsolutePath(), localFile.length());
        if (filePath.endsWith(".mp4")) {
            final String gifFile = dataProperties.getFishpi() + File.separator + filePath.replaceAll("\\.mp4", ".gif");
            convertByFfmpeg(localFile.getAbsolutePath(), gifFile);
            path = gifFile;
        }
        final File file = new File(path);
        final long start = System.currentTimeMillis();
        final Storage upload = fishApi.upload(file);
        final String fishUrl = upload.getSuccessMap().get(file.getName());
        log.info("upload cost[{}]ms result[{}]", System.currentTimeMillis() - start, upload);
        if (fishUrl == null) {
            log.warn("upload to fishpi failure, cannot get picture url");
        } else {
            fileRepository.save(TelegramFile.builder()
                    .fileId(fileId)
                    .filePath(filePath)
                    .fishUrl(fishUrl)
                    .build());
        }
        return fishUrl;
    }


    @SneakyThrows
    private void sendMessage(User user, String content) {
        final TelegramUser byTelegramId = userRepository.getByTelegramId(user.getId());

        if (byTelegramId == null) {
            log.warn("user[{}] not link account to fishpi", user.getUserName());
            return;
        }

        log.info("telegram -> fishpi user[{}], message[{}]", byTelegramId.getFishId(), content);

        try {
            fishApi.sendMessage(MessageParam.builder()
                    .apiKey(byTelegramId.getApiKey())
                    .content(content)
                    .build());
        } catch (FishApiException e) {
            if ("Unauthorized".equals(e.getMessage()) && byTelegramId.getFishPassword() != null) {
                final String apiKey = fishApi.getApiKey(UserApiParam.builder()
                        .nameOrEmail(byTelegramId.getFishName())
                        .userPassword(byTelegramId.getFishPassword()).build());
                byTelegramId.setApiKey(apiKey);
                userRepository.save(byTelegramId);
                log.info("update user[{}] apiKey, and try again", byTelegramId.getFishName());
                fishApi.sendMessage(MessageParam.builder()
                        .apiKey(byTelegramId.getApiKey())
                        .content(content)
                        .build());
            } else {
                throw e;
            }
        }
    }


    @SneakyThrows
    public void openRedPacket(CallbackQuery callbackQuery) {
        final Map<String, Long> map = new ObjectMapper().readValue(callbackQuery.getData(), new TypeReference<HashMap<String, Long>>() {
        });
        final TelegramUser user = userRepository.getByTelegramId(callbackQuery.getFrom().getId());
        final OpenedRedPocket result = fishApi.openRedPocket(RedPacketOpenParam.builder()
                .apiKey(user.getApiKey())
                .oId(map.get("id"))
                .build());
        log.info("open redPacket result[{}]", result);
        final List<OpenedRedPocket.User> openedUser = result.getWho().stream()
                .filter(who -> user.getFishId().equals(who.getUserId()))
                .collect(Collectors.toList());
        String answer;
        if (openedUser.isEmpty()) {
            answer = "你来晚了，下次早点！";
        } else if (openedUser.get(0).getUserMoney() > 0) {
            answer = String.format("抢到了 %d 积分！", openedUser.get(0).getUserMoney());
        } else if (openedUser.get(0).getUserMoney() == 0) {
            answer = "抢了个寂寞。";
        } else {
            answer = String.format("被打劫了 %d 积分!", -openedUser.get(0).getUserMoney());
        }
        absSender.execute(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(answer)
                .build());
    }
}
