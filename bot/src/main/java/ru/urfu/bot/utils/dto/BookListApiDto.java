package ru.urfu.bot.utils.dto;

import java.util.List;

/**
 * Dto контейнер для списка книг.
 */
public class BookListApiDto {

    private Long totalItems;

    private List<BookApiDto> items;

    public List<BookApiDto> getItems() {
        return items;
    }

    public void setItems(List<BookApiDto> items) {
        this.items = items;
    }

    public Long getTotalItems() {
        if (totalItems == null)
            return 0L;
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }
}
