package ru.urfu.bot.utils.dto;

public enum BookDtoFieldEnum{
    IDENTIFIER ("identifier"),
    TITLE ("title"),
    DESC ("description"),
    AUTHORS("authors list"),
    PUBLISHER("publisher"),
    PBDATE("published date");

    public String getFieldName() {
        return fieldName;
    }

    private String fieldName;

    BookDtoFieldEnum(String fieldName) {
        this.fieldName = fieldName;
    }

}
