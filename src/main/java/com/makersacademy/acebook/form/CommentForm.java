package com.makersacademy.acebook.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentForm {
    @NotEmpty(message = "Comment text cannot be empty")
    @Size(min = 10, max = 255, message = "Comment must be between 10 and 255 characters")
    private String text;
}
