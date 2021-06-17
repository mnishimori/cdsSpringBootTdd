package com.github.mnishimori.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotEmpty(message = "Informe o título")
    private String title;

    @NotEmpty(message = "Informe o autor")
    private String author;

    @NotEmpty(message = "Informe o isbn")
    private String isbn;

}
