package com.gustavo.sfrestlibrary.entities;

import com.gustavo.sfrestlibrary.model.BookGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID bookId;

    @NotNull
    @NotBlank
    @Size(min = 13, max = 13)
    private int bookIsbn;

    @NotNull
    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String bookName;

    @NotNull
    private BookGenre bookgenre;

    @Version
    private Integer bookVersion;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime bookCreationDate;

    @UpdateTimestamp
    private LocalDateTime bookUpdateDate;
}
