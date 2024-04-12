package ru.urfu.bot.app;

import reactor.core.publisher.Mono;

public interface BookApiClient {

    Mono<?> findBooksByName(String name);
}
