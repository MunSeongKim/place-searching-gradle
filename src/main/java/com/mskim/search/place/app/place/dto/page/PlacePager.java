package com.mskim.search.place.app.place.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class PlacePager {
    public static final int MAX_PAGE_COUNT = 45;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DISPLAY_PAGE_COUNT = 3;
    /**
     * startPageNumber - 한 화면의 시작 페이지 번호
     * endPageNumber - 한 화면의 끝 페이지 번호
     * leftNavigator - 왼쪽 페이지 리스트 네비게이션 유/무
     * rightNavigator - 오른쪽 페이지 리스트 네비게이션 유/무
     * displayItemCount - 한 화면의 출력할 아이템 수
     * totalItemCount - 전체 아이템의 개수
     * currentPageNumber - 현재 페이지 번호
     */

    @Builder.Default
    private int startPageNumber = 1;
    @Builder.Default
    private int endPageNumber = 3;
    @Builder.Default
    private boolean leftNavigator = false;
    @Builder.Default
    private boolean rightNavigator = false;
    @Builder.Default
    private int startItemNumber = 1;
    @Builder.Default
    private int displayItemCount = 15;
    @Builder.Default
    private int currentPageNumber = 1;

    private int totalPageCount;
    private int totalItemCount;

    private List<DisplayPage> pageNumberList;

    public PlacePager update() {
        update(DEFAULT_PAGE_NUMBER);
        setTotalPageCount();

        return this;
    }

    public PlacePager update(int pageNo) {
        int diff = ((pageNo - 1) % DISPLAY_PAGE_COUNT);

        // 시작, 끝, 현재 페이지 번호 설정
        currentPageNumber = pageNo;
        if (currentPageNumber > endPageNumber) {
            startPageNumber = pageNo - diff;
            endPageNumber = pageNo + (DISPLAY_PAGE_COUNT - diff - 1);
        }

        // 각 페이지의 아이템 시작 번호 설정
        startItemNumber = ((pageNo - 1) * displayItemCount) + 1;

        this.setPageNumberList();
        this.updateNavigator();

        return this;
    }

    private void setTotalPageCount() {
        totalPageCount = totalItemCount / displayItemCount;
        if ((totalItemCount % displayItemCount) != 0) {
            totalPageCount = totalPageCount + 1;
        }
    }

    private void setPageNumberList() {
        pageNumberList = new ArrayList<>();
        for (int i = startPageNumber; i <= endPageNumber; i++) {
            pageNumberList.add(DisplayPage.builder()
                    .isActive(i == currentPageNumber)
                    .value(i)
                    .build());
        }
    }

    private void updateNavigator() {
        // 좌, 우 네비게이터 화면 표시 설정
        leftNavigator = false;
        rightNavigator = false;

        if (endPageNumber < totalPageCount) {
            rightNavigator = true;
        }
        if (startPageNumber > DISPLAY_PAGE_COUNT) {
            leftNavigator = true;
        }
    }
}