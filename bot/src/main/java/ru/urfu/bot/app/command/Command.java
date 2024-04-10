package ru.urfu.bot.app.command;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

    SendMessage handle(Update update);
}
