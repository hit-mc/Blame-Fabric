package com.keuin.blame.data.helper;

import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.entry.LogEntryNames;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VersionedLogEntryHelper {
    public static Collection<String> getLoggedSubjectsId(MongoCollection<LogEntry> collection) {
        List<String> list = new ArrayList<>();
        for (String s : collection.distinct(LogEntryNames.SUBJECT_ID, String.class))
            list.add(s);
        return list;
    }
    public static long countBySubjectId(MongoCollection<LogEntry> collection, String subjectId) {
        return collection.countDocuments(Filters.eq(LogEntryNames.SUBJECT_ID, subjectId));
    }
}
