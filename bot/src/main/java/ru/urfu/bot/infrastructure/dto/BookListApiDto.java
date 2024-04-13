package ru.urfu.bot.infrastructure.dto;

import java.util.List;

public class BookListApiDto {

    private List<BookApiDto> items;

    public List<BookApiDto> getItems() {
        return items;
    }

    public void setItems(List<BookApiDto> items) {
        this.items = items;
    }
}
