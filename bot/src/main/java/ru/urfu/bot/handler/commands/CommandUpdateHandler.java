package ru.urfu.bot.handler.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.UpdateHandler;
import ru.urfu.bot.utils.MessageConst;

import javax.validation.Valid;
import java.util.List;

/**
 * Обработчик для команд, отправляемых пользователем
 */
public abstract class CommandUpdateHandler implements UpdateHandler {

    private final Logger logger = LoggerFactory.getLogger(CommandUpdateHandler.class);

    @Override
    public final List<SendMessage> process(Update update) {
        if (!canHandle(update)) {
            return List.of();
        }
        Command command = parseUpdate(update);
        try {
            return execute(command);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return List.of(new SendMessage(command.chatId().toString(), MessageConst.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Выполняет команду
     * @param command дто
     * @return ответ
     */
    protected abstract List<SendMessage> execute(Command command);

    @Override
    public final boolean canHandle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                String[] data = parseUpdate(update).args();
                return canExecute(data);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Проверка на возможность выполнения команды
     * @param args содержание команды
     * @return true если может выполнить; иначе false
     */
    protected abstract boolean canExecute(String[] args);

    @Valid
    private Command parseUpdate(Update update) {
        return new Command(
                update.getMessage().getText().split(" ", 2),
                update.getMessage().getChat().getUserName(),
                update.getMessage().getChat().getId()
        );
    }
}
