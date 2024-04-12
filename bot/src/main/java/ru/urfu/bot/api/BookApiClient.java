package ru.urfu.bot.api;

import reactor.core.publisher.Mono;

public interface BookApiClient {

    Mono<?> findBooksByName(String name);
}
