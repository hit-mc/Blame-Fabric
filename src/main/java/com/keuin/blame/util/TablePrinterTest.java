package com.keuin.blame.util;

import static org.junit.jupiter.api.Assertions.*;

class TablePrinterTest {

    @org.junit.jupiter.api.Test
    void testToString() {
        var table = new TablePrinter(3);
        table.add(new TablePrinter.Row("1", "233332", "333"));
        table.add(new TablePrinter.Row("1111", "232", "344433"));
        System.out.println(table);
    }
}