package ru.urfu.bot.handler.callbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.UpdateHandler;
import ru.urfu.bot.utils.MessageConst;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Обработчик для обновлений, возвращаемых коллбэком
 */
public abstract class CallbackUpdateHandler implements UpdateHandler {

    private final Logger logger = LoggerFactory.getLogger(CallbackUpdateHandler.class);

    @Override
    public final List<SendMessage> process(Update update) {
        if (!canHandle(update)) {
            logger.error("canHandle() must be invoked before process()");
            return List.of();
        }
        try {
            Command command = parseUpdate(update);
            try {
                return execute(command);
            } catch (Exception e) {
                logger.warn(e.getMessage());
                return List.of(new SendMessage(command.chatId().toString(), MessageConst.INTERNAL_SERVER_ERROR));
            }
        } catch (Exception e) {
            logger.error("can't parse update or canHandle() not been invoked");
            return List.of();
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
        try {
            if (update.hasCallbackQuery()) {
                String[] data = parseUpdate(update).args();
                return canExecute(data);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
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
                update.getCallbackQuery().getData().split(" ", 2),
                update.getCallbackQuery().getFrom().getUserName(),
                update.getCallbackQuery().getMessage().getChatId()
        );
    }

    protected record Command(@NotEmpty String[] args, @NotBlank String username, @NotNull Long chatId) { }
}
