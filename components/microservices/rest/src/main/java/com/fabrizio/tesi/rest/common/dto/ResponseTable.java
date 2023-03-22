package com.fabrizio.tesi.rest.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ResponseTable<T> implements Serializable {
    static final int[] defaultPageSizeOptions = new int[] { 10, 20, 50, 100 };

    final int total;
    final List<T> data;
    final List<String> pageSizeOptions;

    private List<String> calculatePageSizes() {
        final List<String> newOptions = new ArrayList<>();
        for (final int opt : defaultPageSizeOptions) {
            if (this.total > opt)
                newOptions.add(Integer.toString(opt));
            else
                break;
        }
        newOptions.add(Integer.toString(total));

        return newOptions;
    }

    public ResponseTable(final int total, final List<T> data) {
        this.total = total;
        this.data = data;
        this.pageSizeOptions = calculatePageSizes();
    }
}
