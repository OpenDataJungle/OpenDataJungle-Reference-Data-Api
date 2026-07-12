package com.laulem.vectopath.referential.api.shared;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public static <T> PageResultBuilder<T> builder() {
        return new PageResultBuilder<>();
    }

    public static final class PageResultBuilder<T> {
        private List<T> content;
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private int pageSize;

        private PageResultBuilder() {
        }

        public PageResultBuilder<T> content(List<T> content) {
            this.content = content;
            return this;
        }

        public PageResultBuilder<T> totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public PageResultBuilder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PageResultBuilder<T> currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public PageResultBuilder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public PageResult<T> build() {
            return new PageResult<>(content, totalElements, totalPages, currentPage, pageSize);
        }
    }
}
