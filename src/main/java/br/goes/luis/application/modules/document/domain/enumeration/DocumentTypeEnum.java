package br.goes.luis.application.modules.document.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DocumentTypeEnum {
    FAQ("Faq"),
    PRIVATE("Private");

    private final String name;
}