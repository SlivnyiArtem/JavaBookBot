package ru.urfu.bot.domain;

import reactor.core.publisher.Mono;

public interface BookApiClient {

    Mono<?> findBooksByName(String name);
}
