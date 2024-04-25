package ru.urfu.bot.utils.dto;

/**
 * Спарсенная команда.
 * @param commandType тип команды
 * @param data аргумент команды (если имеется)
 */
public record Command(CommandType commandType, String data) { }
