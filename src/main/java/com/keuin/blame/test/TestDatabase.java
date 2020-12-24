package com.keuin.blame.test;

import com.keuin.blame.Blame;
import com.keuin.blame.SubmitWorker;
import com.keuin.blame.data.LogEntry;
import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import com.keuin.blame.lookup.LookupCallback;
import com.keuin.blame.lookup.LookupManager;
import com.keuin.blame.lookup.TestableFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class TestDatabase {

    @Before
    public void init() {
        Blame.loadConfig();
    }

    @Test
    public void testCreateEmptyEntry() {
        try {
            SubmitWorker.INSTANCE.submit(LogEntry.EMPTY_ENTRY);
            Thread.sleep(2000);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateNonEmptyEntry() {
        try {
            long timeMillis = 10102020;
            SubmitWorker.INSTANCE.submit(new LogEntry(
                    timeMillis,
                    "subject-id",
                    UUID.randomUUID(),
                    new WorldPos("world", 1, 2, 3),
                    ActionType.BLOCK_USE,
                    ObjectType.BLOCK,
                    "object-id",
                    new WorldPos("world", 4, 5, 6)
            ));
            Thread.sleep(2000);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testLookupAfterInserted() {
        try {
            final boolean[] success = {false};
            SubmitWorker.INSTANCE.submit(LogEntry.EMPTY_ENTRY);
            Thread.sleep(2000);
            LookupManager.INSTANCE.lookup(new TestableFilter(), new LookupCallback() {
                @Override
                public void onLookupFinishes(Iterable<LogEntry> logEntries) {
                    for (LogEntry entry : logEntries) {
                        System.out.println(entry);
                        assertEquals(entry, LogEntry.EMPTY_ENTRY);
                        success[0] = Objects.equals(entry, LogEntry.EMPTY_ENTRY);
                    }
                }
            });
            Thread.sleep(2000);
            assertTrue(success[0]);
        } catch (Exception e) {
            fail();
        }
    }
}