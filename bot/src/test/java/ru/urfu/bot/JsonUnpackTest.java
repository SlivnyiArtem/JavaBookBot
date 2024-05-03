package ru.urfu.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.urfu.bot.utils.dto.BookApiDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class JsonUnpackTest {

    private BookApiDto bookApiDto;

    @BeforeEach
    void init() {
        bookApiDto = new BookApiDto();
    }

    private void assertInfo(Long isbn, String title, String description,
                            String authors, String publisher, LocalDate publishedDate) {
        assertEquals(isbn, bookApiDto.getIsbn13());
        assertEquals(title, bookApiDto.getTitle());
        assertEquals(description, bookApiDto.getDescription());
        assertEquals(authors, bookApiDto.getAuthors());
        assertEquals(publisher, bookApiDto.getPublisher());
        assertEquals(publishedDate, bookApiDto.getPublishedDate());
    }

    private static Stream<Arguments> provideVolumeInfoArguments() {
        return Stream.of(
                Arguments.of(
                        Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_10", "identifier","2"),
                                        Map.of("type", "ISBN_13", "identifier","1")
                                ),
                                "title", "title",
                                "publishedDate", "2023-11-08",
                                "authors", List.of("author1", "author2"),
                                "description", "description",
                                "publisher", "publisher"
                        ),
                        new BookApiDto(
                                1L, "title", "description", "author1, author2",
                                "publisher", LocalDate.of(2023, 11, 8)
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideVolumeInfoArguments")
    void correctVolumeInfoTest(Map<String, Object> volumeInfo, BookApiDto expected) {
        bookApiDto.unpackNested(volumeInfo);

        assertInfo(expected.getIsbn13(), expected.getTitle(), expected.getDescription(), expected.getAuthors(),
                expected.getPublisher(), expected.getPublishedDate());
    }


    private static Stream<Arguments> provideIncorrectIsbnInfoArguments() {
        return Stream.of(
                Arguments.of(
                        Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_10", "identifier","1")
                                ),
                                "title", "title",
                                "publishedDate", "2023-11-08",
                                "authors", List.of("author1", "author2"),
                                "description", "description",
                                "publisher", "publisher"
                        ),
                        Map.of(
                                "industryIdentifiers", List.of("1", "2"),
                                "title", "title",
                                "publishedDate", "2023-11-08",
                                "authors", List.of("author1", "author2"),
                                "description", "description",
                                "publisher", "publisher"
                        ),
                        new BookApiDto()
                ),
                Arguments.of(
                        Map.of(
                                "title", "title",
                                "publishedDate", "2023-11-08",
                                "authors", List.of("author1", "author2"),
                                "description", "description",
                                "publisher", "publisher"
                        ),
                        new BookApiDto()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectIsbnInfoArguments")
    public void testIncorrectIsbn(Map<String, Object> volumeInfo) {
        bookApiDto.unpackNested(volumeInfo);

        assertInfo(null, null, null, null, null, null);
    }

    private static Stream<Arguments> provideIncorrectVolumeInfo() {
        return Stream.of(
                Arguments.of(
                        Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_13", "identifier","1")
                                ),
                                "title", List.of("title"),
                                "publishedDate", "2023-11",
                                "authors", "author1, author2",
                                "description", 123,
                                "publisher", Map.of("name", "publisher")
                        )
                ),
                Arguments.of(
                        Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_13", "identifier","1"),
                                        Map.of("type", "ISBN_10", "identifier","2")
                                )
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectVolumeInfo")
    public void testIncorrectProperties(Map<String, Object> volumeInfo) {
        bookApiDto.unpackNested(volumeInfo);

        assertInfo(1L, null, null, null, null, null);
    }
}
