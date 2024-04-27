package ru.urfu.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.urfu.bot.utils.dto.BookApiDto;

import java.time.LocalDate;

import static junit.framework.Assert.assertEquals;

public class JsonUnpackTest {
    private static final String SOURCE_JSON =
            """
           {
           "isbn13": 1,
           "title": "title",
           "publishedDate": "2023-12-08",
           "authors": "author1, author2",
           "description": "description",
           "publisher": "publisher1"
           }""";

    @Mock
    private BookApiDto bookApiDto;

    @BeforeEach
    void init() {
        bookApiDto = new BookApiDto();
    }

    @Test
    public void test() throws JsonProcessingException {
        System.out.println(LocalDate.now());

        BookApiDto bookApiDto = new ObjectMapper().registerModule(new JavaTimeModule())
                .readerFor(BookApiDto.class)
                .readValue(SOURCE_JSON);

        assertEquals("title", bookApiDto.getTitle());
        assertEquals("description", bookApiDto.getDescription());
        assertEquals("author1, author2", bookApiDto.getAuthors());
        assertEquals("publisher1", bookApiDto.getPublisher());
        assertEquals(bookApiDto.getPublishedDate(), LocalDate.of(2023, 12, 8));
        assertEquals(bookApiDto.getIsbn13(), Long.valueOf(1));
    }
}
