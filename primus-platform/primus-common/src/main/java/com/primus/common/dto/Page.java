package com.primus.common.dto;

import java.util.Collections;
import java.util.List;

/**
 * Paginated list response.
 */
public class Page<T> {

    private final List<T> content;
    private final long totalElements;
    private final int pageSize;
    private final int pageNumber;
    private final int totalPages;

    public Page(List<T> content, long totalElements, int pageSize, int pageNumber) {
        this.content = content == null ? Collections.emptyList() : Collections.unmodifiableList(content);
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
    }

    public List<T> getContent() { return content; }
    public long getTotalElements() { return totalElements; }
    public int getPageSize() { return pageSize; }
    public int getPageNumber() { return pageNumber; }
    public int getTotalPages() { return totalPages; }
    public boolean hasNext() { return pageNumber < totalPages - 1; }
    public boolean hasPrevious() { return pageNumber > 0; }
    public boolean isEmpty() { return content.isEmpty(); }

    public static <T> Page<T> of(List<T> content, long totalElements, int pageSize, int pageNumber) {
        return new Page<>(content, totalElements, pageSize, pageNumber);
    }

    public static <T> Page<T> empty(int pageSize) {
        return new Page<>(Collections.emptyList(), 0, pageSize, 0);
    }
}
