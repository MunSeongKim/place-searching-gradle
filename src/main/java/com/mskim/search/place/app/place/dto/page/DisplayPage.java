package com.mskim.search.place.app.place.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class DisplayPage {
    @Builder.Default
    private boolean isActive = false;
    private int value;
}
