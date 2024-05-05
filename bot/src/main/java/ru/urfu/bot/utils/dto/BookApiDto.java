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
            List<?> identifiers = Optional.ofNullable( Optional.ofNullable(volumeInfo.get("industryIdentifiers"))
                            .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.IDENTIFIER)))
                    .filter(List.class::isInstance)
                    .map(List.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.IDENTIFIER));

            this.isbn13 = Long.parseLong(Optional.of( identifiers.stream()
                    .map(obj -> (Map<?,?>) obj)
                    .filter(map -> Objects.equals(map.get("type"), "ISBN_13"))
                    .findFirst()
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.IDENTIFIER))
                    .get("identifier"))
                    .filter(String.class::isInstance).map(String.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.IDENTIFIER)));

            this.title = Optional.of(Optional.ofNullable(volumeInfo.get("title"))
                            .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.TITLE)))
                    .filter(String.class::isInstance).map(String.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.TITLE));

            this.description = Optional.of(Optional.ofNullable(volumeInfo.get("description"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.DESC)))
                    .filter(String.class::isInstance).map(String.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.DESC));

            List<?> authorsList = Optional.of( Optional.ofNullable(volumeInfo.get("authors"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.AUTHORS)))
                    .filter(List.class::isInstance).map(List.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.AUTHORS));

            this.authors = authorsList.stream()
                    .map(obj -> Optional.of(obj)
                            .filter(String.class::isInstance).map(String.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.AUTHORS)))
                    .collect(Collectors.joining(", "));

            this.publisher = Optional.of(Optional.ofNullable(volumeInfo.get("publisher"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.PUBLISHER)))
                    .filter(String.class::isInstance).map(String.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.PUBLISHER));
            
            this.publishedDate = LocalDate.parse(Optional.of(Optional.ofNullable(volumeInfo.get("publishedDate"))
                    .orElseThrow(() -> new DtoCustomNoSuchElementExc(null, BookDtoFieldEnum.PBDATE)))
                    .filter(String.class::isInstance).map(String.class::cast)
                    .orElseThrow(() -> new DtoCastCustomExc(null, BookDtoFieldEnum.PBDATE)));
        }catch (DtoCastCustomExc exc) {
            LOG.warn(String.format("can't parse %s", exc.getField()), exc);
        }catch (DtoCustomNoSuchElementExc exc) {
            LOG.warn(String.format("can't parse %s", exc.getField()), exc);
        }catch (DateTimeParseException exc){
            LOG.warn("can't parse published date", exc);
        }
    }
}
