package edu.telegram.bot;

import edu.telegram.exception.DataNotFoundException;
import edu.telegram.utils.MessageConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import edu.telegram.service.ChatUserService;

import java.time.OffsetTime;
import java.time.format.DateTimeParseException;

import static org.telegram.telegrambots.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.TEXT;
import static org.telegram.telegrambots.abilitybots.api.objects.Locality.ALL;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.*;

@Component
public class SystemAbilityExtension implements AbilityExtension {

    private static final String DATA_NOT_FOUND_LOG_MSG = "some data not found";

    private static final String UNKNOWN_EXCEPTION_LOG_MSG = "unknown exception";

    private final ChatUserService chatUserService;

    private static final Logger log = LoggerFactory.getLogger(SystemAbilityExtension.class);

    public SystemAbilityExtension(ChatUserService chatUserService) {
        this.chatUserService = chatUserService;
    }

    public Ability start() {
        return Ability.builder()
                .name(MessageConst.Commands.START.getName())
                .info(MessageConst.Commands.START.getInfo())
                .locality(ALL)
                .privacy(PUBLIC)
                .flag(TEXT)
                .action(ctx -> {
                    try {
                        chatUserService.addUserChatIfAbsent(getUser(ctx.update()).getUserName(), getChatId(ctx.update()));
                    } catch (Exception e) {
                        ctx.bot().getSilent().send(MessageConst.INTERNAL_SERVER_ERROR_MESSAGE, getChatId(ctx.update()));
                        log.error(UNKNOWN_EXCEPTION_LOG_MSG, e);
                    }
                })
                .post(ctx -> ctx.bot().getAbilities().get(MessageConst.Commands.HELP.getName()).action().accept(ctx))
                .build();
    }

    public Ability setNotificationTime() {
        return Ability.builder()
                .name(MessageConst.Commands.SET_TIME.getName())
                .info(MessageConst.Commands.SET_TIME.getInfo())
                .locality(ALL)
                .privacy(PUBLIC)
                .flag(TEXT)
                .action(ctx -> ctx.bot().getSilent().forceReply(MessageConst.SET_NOTIFICATION_TIME_MESSAGE, getChatId(ctx.update())))
                .reply((bot, update) -> {
                    String response;
                    try {
                        OffsetTime scheduledTime = OffsetTime.parse(update.getMessage().getText());
                        chatUserService.setNotificationTime(update.getMessage().getFrom().getUserName(), scheduledTime);
                        response = MessageConst.NOTIFICATION_TIME_SET_MESSAGE;
                    } catch (DateTimeParseException e) {
                        response = MessageConst.WRONG_TIME_FORMAT_MESSAGE;
                    } catch (DataNotFoundException e) {
                        response = MessageConst.INTERNAL_SERVER_ERROR_MESSAGE;
                        log.warn(DATA_NOT_FOUND_LOG_MSG, e);
                    } catch (Exception e) {
                        response = MessageConst.INTERNAL_SERVER_ERROR_MESSAGE;
                        log.error(UNKNOWN_EXCEPTION_LOG_MSG, e);
                    }

                    bot.getSilent().send(response, getChatId(update));
                }, TEXT, REPLY, isReplyTo(MessageConst.SET_NOTIFICATION_TIME_MESSAGE))
                .build();
    }
}
