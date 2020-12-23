package com.keuin.blame.data;

import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.UUID;

public class LogEntry {

    // {
    //    "version": 1, // int
    //    "subject": {
    //        "uuid": player_uuid_bytes, // bytes
    //        "id": player_id, // string
    //        "pos": {
    //            "world": world_id, // string
    //            "x": pos_x, // float
    //            "y": pos_y, // float
    //            "z": pos_z, // float
    //        }
    //    },
    //    "action": BLOCK_BREAK | BLOCK_PLACE | BLOCK_USE | ENTITY_USE | ENTITY_ATTACK | ITEM_USE, // int
    //    "object": {
    //        "type": OBJECT_BLOCK | OBJECT_ENTITY, // int
    //        "id": object_id, // string
    //        "pos": {
    //            "world": world_id, // string
    //            "x": pos_x, // float
    //            "y": pos_y, // float
    //            "z": pos_z, // float
    //        }
    //    }
    //}

    @BsonProperty("version")
    private final int version = 1;
    @BsonProperty("timestamp_millis")
    private final long timeMillis;
    @BsonProperty("subject_uuid")
    private final String subjectUUID; // TODO: use Binary instead (BasicDBObject("_id", Binary(session.getIp().getAddress()))) (https://stackoverflow.com/questions/30566905/store-byte-in-mongodb-using-java/40843195)
    @BsonProperty("subject_pos")
    private final WorldPos subjectPos; // TODO: write codec and transformer for this
    @BsonProperty("action_type")
    private final ActionType actionType;
    @BsonProperty("object_type")
    private final ObjectType objectType;
    @BsonProperty("object_id")
    private final String objectId;
    @BsonProperty("object_pos")
    private final WorldPos objectPos;

    public LogEntry(long timeMillis, UUID subjectUUID, WorldPos subjectPos, ActionType actionType, ObjectType objectType, String objectId, WorldPos objectPos) {
//        this.subjectUUID = UuidUtils.asBytes(subjectUUID);
//        this.subjectUUID
        if (subjectUUID == null)
            throw new IllegalArgumentException("subjectUUID cannot be null");
        if (subjectPos == null)
            throw new IllegalArgumentException("subjectPos cannot be null");
        if (actionType == null)
            throw new IllegalArgumentException("actionType cannot be null");
        if (objectType == null)
            throw new IllegalArgumentException("objectType cannot be null");
        if (objectId == null)
            throw new IllegalArgumentException("objectId cannot be null");
        if (objectPos == null)
            throw new IllegalArgumentException("objectPos cannot be null");

        this.timeMillis = timeMillis;
        this.subjectUUID = subjectUUID.toString();
        this.subjectPos = subjectPos;
        this.actionType = actionType;
        this.objectType = objectType;
        this.objectId = objectId;
        this.objectPos = objectPos;
    }

    public int getVersion() {
        return 1;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public UUID getSubjectUUID() {
        return UUID.fromString(subjectUUID);
    }

    public WorldPos getSubjectPos() {
        return subjectPos;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public WorldPos getObjectPos() {
        return objectPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry entry = (LogEntry) o;
        return version == entry.version &&
                timeMillis == entry.timeMillis &&
                Objects.equals(subjectUUID, entry.subjectUUID) &&
                Objects.equals(subjectPos, entry.subjectPos) &&
                actionType == entry.actionType &&
                objectType == entry.objectType &&
                Objects.equals(objectId, entry.objectId) &&
                Objects.equals(objectPos, entry.objectPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, timeMillis, subjectUUID, subjectPos, actionType, objectType, objectId, objectPos);
    }
}
