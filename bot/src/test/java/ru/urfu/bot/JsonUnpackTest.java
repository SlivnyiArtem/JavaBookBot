package ru.urfu.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import junit.framework.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import ru.urfu.bot.utils.dto.BookApiDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class JsonUnpackTest {


    @Mock
    private BookApiDto bookApiDto;

    @BeforeEach
    void init() {
        bookApiDto = new BookApiDto();
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
        TestLogger logger = TestLoggerFactory.getTestLogger(BookApiDto.class);

        BookApiDto bookApiDto = new BookApiDto();
        bookApiDto.unpackNested(Map.of("industryIdentifiers", List.of(Map.of("type", "ISBN_13","identifier","ast-tree")),
                "title", "title",
                "publishedDate", "2023-11-08",
                "authors", List.of("author1, author2"),
                "description", "description",
                "publisher", "publisher1"));

        Assert.assertEquals("k", logger.getLoggingEvents().asList().getFirst().getMessage());




    }
    @Test
    public void testIncorrectPublishedDate() throws JsonProcessingException {

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
