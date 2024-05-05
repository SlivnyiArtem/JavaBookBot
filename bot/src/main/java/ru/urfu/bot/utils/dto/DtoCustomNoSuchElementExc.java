package ru.urfu.bot.utils.dto;

import java.util.NoSuchElementException;

public class DtoCustomNoSuchElementExc extends NoSuchElementException {
    public BookDtoFieldEnum getField() {
        return field;
    }

    private BookDtoFieldEnum field;

    public DtoCustomNoSuchElementExc(String message, BookDtoFieldEnum field){
        super(message);
        this.field = field;
    }
}
