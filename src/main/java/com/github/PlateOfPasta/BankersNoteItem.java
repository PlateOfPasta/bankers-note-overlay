package com.github.PlateOfPasta;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class BankersNoteItem
{
    @Getter(AccessLevel.PACKAGE)
    private final String searchableName;

    @Getter(AccessLevel.PACKAGE)
    private final int canonItemId;

    @Override
    public String toString()
    {
        return "BankersNoteItem{ " +
            "searchableName='" + searchableName + '\'' +
            ", canonItemId=" + canonItemId +
            " }";
    }
}
