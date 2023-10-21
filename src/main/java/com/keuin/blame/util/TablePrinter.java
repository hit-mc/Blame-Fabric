package com.keuin.blame.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TablePrinter {
    private final int columns;

    private final List<Row> rows = new ArrayList<>(16);

    private Function<String, Integer> widthSupplier = (String::length);

    private List<Formatting> colors = List.of(
            Formatting.RED, Formatting.GOLD, Formatting.GREEN, Formatting.BLUE, Formatting.DARK_PURPLE);

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

    private Formatting getColor(int i) {
        return colors.get(i % colors.size());
    }

    public Text build() {
        final String SEPARATOR = ".";
        var maxWidths = new int[columns];
        for (var r : rows) {
            for (int i = 0; i < r.row.length; i++) {
                var len = widthSupplier.apply(r.row[i]);
                maxWidths[i] = Math.max(maxWidths[i], len);
            }
        }
        MutableText t = new LiteralText("");
        for (var r : rows) {
            var i = 0;
            for (String s : r.row) {
                t = t.append(new LiteralText(SEPARATOR + " "));

                var n = maxWidths[i] - s.length();
                if (n > 0) {
                    t = t.append(" ".repeat(n / 2));
                }
                t = t.append(new LiteralText(s).formatted(getColor(i)));
                if (n > 0) {
                    t = t.append(" ".repeat(n - n / 2));
                }
                t = t.append(" ");
                i++;
            }
            if (r.row.length == 0) {
                t = t.append(SEPARATOR);
            }
            t = t.append(SEPARATOR);
            t = t.append("\n");
        }
        return t;
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
