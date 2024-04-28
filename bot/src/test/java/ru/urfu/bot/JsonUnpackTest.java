package ru.urfu.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.slf4j.event.Level;
import ru.urfu.bot.services.handlers.callbacks.AddBookService;
import ru.urfu.bot.utils.dto.BookApiDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.extension.ExtendWith;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;

import static junit.framework.Assert.assertEquals;

@ExtendWith(TestLoggerFactoryExtension.class)
public class JsonUnpackTest {

    private TestLogger logger;
    private BookApiDto bookApiDto;

    @BeforeEach
    void init() {
        bookApiDto = new BookApiDto();
        logger = TestLoggerFactory.getTestLogger(AddBookService.class);
    }

    @Test
    public void testCorrectInitialization() throws JsonProcessingException {
        String source_json =
                """
               {
               "isbn13": 1,
               "title": "title",
               "publishedDate": "2023-12-08",
               "authors": "author1, author2",
               "description": "description",
               "publisher": "publisher1"
               }""";

        BookApiDto bookApiDto = new ObjectMapper().registerModule(new JavaTimeModule())
                .readerFor(BookApiDto.class)
                .readValue(source_json);

        assertEquals("title", bookApiDto.getTitle());
        assertEquals("description", bookApiDto.getDescription());
        assertEquals("author1, author2", bookApiDto.getAuthors());
        assertEquals("publisher1", bookApiDto.getPublisher());
        assertEquals(bookApiDto.getPublishedDate(), LocalDate.of(2023, 12, 8));
        assertEquals(bookApiDto.getIsbn13(), Long.valueOf(1));
    }

    @Test
    public void testIncorrectIsbn() throws JsonProcessingException {
        bookApiDto.unpackNested(Map.of("industryIdentifiers", List.of(Map.of("type", "tree","identifier","ast-tree")),
                "title", "title1",
                "publishedDate", "2023-11-08",
                "authors", List.of("author1, author2"),
                "description", "description",
                "publisher", "publisher1"));


        assertEquals("no isbn of title1", logger.getLoggingEvents().getFirst().getFormattedMessage());
        assertEquals(Level.ERROR, logger.getLoggingEvents().getFirst().getLevel());




    }
    @Test
    public void testIncorrectPublishedDate() throws JsonProcessingException {
        bookApiDto.unpackNested(Map.of("industryIdentifiers", List.of(Map.of("type", "ISBN_13","identifier","1")),
                "title", "title2",
                "publishedDate", "2023-15-08",
                "authors", List.of("author1, author2"),
                "description", "description",
                "publisher", "publisher1"));


        assertEquals("incorrect published date of title2", logger.getLoggingEvents().getFirst().getFormattedMessage());
        assertEquals(Level.ERROR, logger.getLoggingEvents().getFirst().getLevel());

        //FIXME возможно стоит напрямую сравнивать объекты исключений, но по моему это лишнее
//        assertEquals(new DateTimeParseException("Text '2023-15-08' could not be parsed: Invalid value for MonthOfYear (valid values 1 - 12): 15", "2023-15-08", 0),
//                logger.getLoggingEvents().getFirst().getThrowable().get());
    }

    @Test
    public void testCorrectedParsing() throws JsonProcessingException {
        BookApiDto bookApiDto = new BookApiDto();
        bookApiDto.unpackNested(Map.of("industryIdentifiers", List.of(Map.of("type", "ISBN_13","identifier","1")),
                "title", "title",
                "publishedDate", "2023-11-08",
                "authors", List.of("author1, author2"),
                "description", "description",
                "publisher", "publisher1"));
        assertEquals("title", bookApiDto.getTitle());
        assertEquals("description", bookApiDto.getDescription());
        assertEquals("author1, author2", bookApiDto.getAuthors());
        assertEquals("publisher1", bookApiDto.getPublisher());
        assertEquals(bookApiDto.getPublishedDate(), LocalDate.of(2023, 11, 8));
        assertEquals(bookApiDto.getIsbn13(), Long.valueOf(1));
    }
}
