package ru.urfu.bot.utils.dto;

public class DtoCastCustomExc extends ClassCastException{


    public BookDtoFieldEnum getField() {
        return field;
    }

    private BookDtoFieldEnum field;

    public DtoCastCustomExc(String message, BookDtoFieldEnum field){
        super(message);
        this.field = field;
    }
}
