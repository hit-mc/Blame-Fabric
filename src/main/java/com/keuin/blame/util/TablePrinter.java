package com.keuin.blame.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TablePrinter {
    private final int columns;

    private final List<Row> rows = new ArrayList<>(16);

    private Function<String, Integer> widthSupplier = (String::length);

    private final int BORDER = 1;

    public TablePrinter(int columns) {
        this.columns = columns;
    }

    public void add(Row row) {
        if (row.get().length != columns) {
            throw new IllegalArgumentException("invalid row columns, expected " + columns);
        }
        rows.add(row);
    }

    public TablePrinter setWidthSupplier(Function<String, Integer> widthSupplier) {
        this.widthSupplier = widthSupplier;
        return this;
    }

    @Override
    public String toString() {
        var maxWidths = new int[columns];
        for (var r : rows) {
            for (int i = 0; i < r.row.length; i++) {
                var len = widthSupplier.apply(r.row[i]);
                maxWidths[i] = Math.max(maxWidths[i], len);
            }
        }
        var sb = new StringBuilder();
        for (var r : rows) {
            var i = 0;
            for (String s : r.row) {
                sb.append('|');
                sb.append(' ');
                sb.append(s);
                var n = maxWidths[i] - s.length();
                if (n > 0) {
                    sb.append(" ".repeat(n));
                }
                sb.append(' ');
                i++;
            }
            if (r.row.length == 0) {
                sb.append('|');
            }
            sb.append("|");
            sb.append('\n');
        }
        return sb.toString();
    }

    public static class Row {
        private final String[] row;

        public Row(String... row) {
            this.row = row;
        }

        private String[] get() {
            return row;
        }
    }
}
