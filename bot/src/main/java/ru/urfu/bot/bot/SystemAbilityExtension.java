package ru.urfu.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import ru.urfu.bot.service.ChatUserService;

import java.time.OffsetTime;
import java.time.format.DateTimeParseException;

import static org.telegram.telegrambots.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.TEXT;
import static org.telegram.telegrambots.abilitybots.api.objects.Locality.ALL;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.*;
import static ru.urfu.bot.utils.MessageConst.*;

@Component
public class SystemAbilityExtension implements AbilityExtension {

    private final ChatUserService chatUserService;

    private static final Logger log = LoggerFactory.getLogger(SystemAbilityExtension.class);

    public SystemAbilityExtension(ChatUserService chatUserService) {
        this.chatUserService = chatUserService;
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .info(COMMANDS.get("start"))
                .locality(ALL)
                .privacy(PUBLIC)
                .flag(TEXT)
                .action(ctx -> {
                    if (chatUserService.addUserChatIfAbsent(getUser(ctx.update()).getUserName(), getChatId(ctx.update()))) {
                        log.debug("user %s added".formatted(getUser(ctx.update()).getUserName()));
                    }
                })
                .post(ctx -> ctx.bot().getAbilities().get("help").action().accept(ctx))
                .build();
    }

    public Ability setNotificationTime() {
        return Ability.builder()
                .name("set_time")
                .info(COMMANDS.get("set_time"))
                .locality(ALL)
                .privacy(PUBLIC)
                .flag(TEXT)
                .action(ctx -> ctx.bot().getSilent()
                        .forceReply("Введите время", getChatId(ctx.update())))
                .reply((bot, update) -> {
                    OffsetTime scheduledTime;
                    try {
                        scheduledTime = OffsetTime.parse(update.getMessage().getText());
                    } catch (DateTimeParseException e) {
                        bot.getSilent().send("Неверный формат времени", getChatId(update));
                        return;
                    }

                    chatUserService.setNotificationTime(update.getMessage().getFrom().getUserName(), scheduledTime);
                    bot.getSilent().send(SET_SCHEDULED_TIME_MESSAGE, getChatId(update));
                }, TEXT, REPLY, isReplyTo("Введите время"))
                .build();
    }
}
