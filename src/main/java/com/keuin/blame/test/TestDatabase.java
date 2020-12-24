package com.keuin.blame.test;

import com.keuin.blame.Blame;
import com.keuin.blame.SubmitWorker;
import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import com.keuin.blame.lookup.DummyFilter;
import com.keuin.blame.lookup.LookupCallback;
import com.keuin.blame.lookup.LookupManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestDatabase {

    @Before
    public void init() {
        Blame.loadConfig();
    }

    @Test
    public void testCreateEmptyEntry() {
        try {
            SubmitWorker.INSTANCE.submit(new LogEntry());
            Thread.sleep(2000);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateNonEmptyEntry() {
        try {
            SubmitWorker.INSTANCE.submit(new LogEntry(
                    111111111,
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
            LogEntry entry = new LogEntry(
                    222222222,
                    "subject-id",
                    UUID.randomUUID(),
                    new WorldPos("world", 1, 2, 3),
                    ActionType.BLOCK_USE,
                    ObjectType.BLOCK,
                    "object-id",
                    new WorldPos("world", 4, 5, 6)
            );
            SubmitWorker.INSTANCE.submit(entry);
            Thread.sleep(2000);
            LookupManager.INSTANCE.lookup(new DummyFilter(), new LookupCallback() {
                @Override
                public void onLookupFinishes(Iterable<LogEntry> logEntries) {
                    for (LogEntry e : logEntries) {
                        System.out.println(e);
                        success[0] |= Objects.equals(e, entry);
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