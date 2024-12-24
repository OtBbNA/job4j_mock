package ru.job4j.site.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class ErrorMessage {

    @NonNull
    private String message;
}
