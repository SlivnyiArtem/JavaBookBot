package ru.urfu.bot.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dto для получения информации о книге из api.
 */
@JsonNaming
public class BookApiDto {

    private static final Logger LOG = LoggerFactory.getLogger(BookApiDto.class);

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Long getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(Long isbn13) {
        this.isbn13 = isbn13;
    }

    public BookApiDto(Long isbn13, String title, String description, String authors, String publisher, LocalDate publishedDate) {
        this.isbn13 = isbn13;
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
    }

    public BookApiDto() { }

    private Long isbn13;

    private String title;

    private String description;

    private String authors;

    private String publisher;

    private LocalDate publishedDate;

    @JsonProperty("volumeInfo")
    public void unpackNested(Map<String,Object> volumeInfo) {

        try {
            List<?> identifiers = Optional.of((List<?>) Optional.of(volumeInfo.get("industryIdentifiers"))
                            .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.IDENTIFIER)))
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.IDENTIFIER));

            this.isbn13 = Long.parseLong(Optional.of((String) identifiers.stream()
                    .map(obj -> (Map<?,?>) obj)
                    .filter(map -> Objects.equals(map.get("type"), "ISBN_13"))
                    .findFirst()
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.IDENTIFIER))
                    .get("identifier")).orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.IDENTIFIER)));

            this.title = Optional.of((String) Optional.of(volumeInfo.get("title"))
                            .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.TITLE)))
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.TITLE));

            this.description = Optional.of((String) Optional.of(volumeInfo.get("description"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.DESC)))
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.DESC));
            List<?> authorsList = Optional.of((List<?>) Optional.of(volumeInfo.get("authors"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.AUTHORS)))
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.AUTHORS));
            this.authors = authorsList.stream()
                    .map(obj -> Optional.of((String) obj)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.AUTHORS)))
                    .collect(Collectors.joining(", "));
            this.publisher = Optional.of((String) Optional.of(volumeInfo.get("publisher"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.PUBLISHER)))
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.PUBLISHER));
            this.publishedDate = LocalDate.parse(Optional.of((String) Optional.of(volumeInfo.get("publishedDate"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.PBDATE)))
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.PBDATE)));


//            List<?> identifiers = Optional.ofNullable((List<?>) volumeInfo.get("industryIdentifiers").)
//                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.IDENTIFIER));
//            this.isbn13 = Long.parseLong(Optional.of((String) identifiers.stream()
//                    .map(obj -> (Map<?,?>) obj)
//                    .filter(map -> Objects.equals(map.get("type"), "ISBN_13"))
//                    .findFirst()
//                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.IDENTIFIER))
//                    .get("identifier")).orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.IDENTIFIER)));
//            this.title = Optional.of((String) volumeInfo.get("title"))
        }catch (DtoCastCustomExc exc) {
            LOG.warn(String.format("can't parse %s", exc.getField()), exc);
        }catch (DtoCustomNoSuchElementExc exc) {
            LOG.warn(String.format("can't parse %s", exc.getField()), exc);
        }catch (DateTimeParseException exc){
            LOG.warn("can't parse published date", exc);
        }


//        try {
//            if (volumeInfo.get("industryIdentifiers") == null) {
//                throw new NoSuchElementException();
//            }
//            List<?> identifiers = (List<?>) volumeInfo.get("industryIdentifiers");
//            this.isbn13 = Long.parseLong(
//                    (String) identifiers.stream()
//                            .map(obj -> (Map<?,?>) obj)
//                            .filter(map -> Objects.equals(map.get("type"), "ISBN_13"))
//                            .findFirst()
//                            .orElseThrow(NoSuchElementException::new)
//                            .get("identifier")
//            );
//        } catch (ClassCastException | NoSuchElementException e) {
//            LOG.warn("can't parse identifier", e);
//            // Если нет идентификатора, значит объект не годен
//            return;
//        }
//
//        try {
//            if (volumeInfo.get("title") == null) {
//                throw new NoSuchElementException();
//            }
//            this.title = (String) volumeInfo.get("title");
//        } catch (ClassCastException | NoSuchElementException e) {
//            LOG.warn("can't parse title", e);
//        }
//
//        try {
//            if (volumeInfo.get("description") == null) {
//                throw new NoSuchElementException();
//            }
//            this.description = (String) volumeInfo.get("description");
//        } catch (ClassCastException | NoSuchElementException e) {
//            LOG.warn("can't parse description", e);
//        }
//
//        try {
//            if (volumeInfo.get("authors") == null) {
//                throw new NoSuchElementException();
//            }
//            List<?> authorsList = (List<?>) volumeInfo.get("authors");
//            this.authors = authorsList.stream()
//                    .map(obj -> (String) obj)
//                    .collect(Collectors.joining(", "));
//        } catch (ClassCastException | NoSuchElementException e) {
//            LOG.warn("can't parse authors list", e);
//        }
//
//        try {
//            if (volumeInfo.get("publisher") == null) {
//                throw new NoSuchElementException();
//            }
//            this.publisher = (String) volumeInfo.get("publisher");
//        } catch (ClassCastException | NoSuchElementException e) {
//            LOG.warn("can't parse publisher", e);
//        }
//
//        try {
//            if (volumeInfo.get("publishedDate") == null) {
//                throw new NoSuchElementException();
//            }
//            this.publishedDate = LocalDate.parse((String) volumeInfo.get("publishedDate"));
//        } catch (ClassCastException | NoSuchElementException | DateTimeParseException e) {
//            LOG.warn("can't parse published date", e);
//        }
    }
}
