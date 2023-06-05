package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OverriddenPageRequest extends PageRequest {

    private final int from;

    public OverriddenPageRequest(int page, int size) {
        super(page, size, Sort.unsorted());
        this.from = page;
    }

    public OverriddenPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
        this.from = page;
    }

    @Override
    public long getOffset() {
        return from;
    }


}