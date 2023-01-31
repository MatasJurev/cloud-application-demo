package com.company.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    private Long id;
    @NonNull @NotEmpty
    private String title;
    @NonNull @NotEmpty
    private String content;
    @NonNull @NotEmpty
    private String author;
}
