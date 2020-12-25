package com.keuin.blame.data.entry;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import com.keuin.blame.util.PrettyUtil;
import com.keuin.blame.util.UuidUtils;
import net.minecraft.MinecraftVersion;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.UUID;

import static com.keuin.blame.data.entry.LogEntryNamesV1.*;

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

    @BsonProperty(VERSION)
    public int version = 1;

    @BsonProperty(GAME_VERSION)
    public String gameVersion = MinecraftVersion.field_25319.getName();

    @BsonProperty(TIMESTAMP_MILLIS)
    public long timeMillis = 0;

    @BsonProperty(SUBJECT_ID)
    public String subjectId = "";

    @BsonProperty(SUBJECT_UUID)
    public UUID subjectUUID = UuidUtils.UUID_NULL;

    @BsonProperty(SUBJECT_POS)
    public WorldPos subjectPos = WorldPos.NULL_POS;

    @BsonProperty(ACTION_TYPE)
    public ActionType actionType = ActionType.NULL;

    @BsonProperty(OBJECT_TYPE)
    public ObjectType objectType = ObjectType.NULL;

    @BsonProperty(OBJECT_ID)
    public String objectId = "";

    @BsonProperty(OBJECT_POS)
    public WorldPos objectPos = WorldPos.NULL_POS;

    public LogEntry() {
    }

    // 拷贝构造器
    public LogEntry(LogEntry entry) {
        this.version = entry.version;
        this.gameVersion = entry.gameVersion;
        this.timeMillis = entry.timeMillis;
        this.subjectId = entry.subjectId;
        this.subjectUUID = entry.subjectUUID;
        this.subjectPos = entry.subjectPos;
        this.actionType = entry.actionType;
        this.objectType = entry.objectType;
        this.objectId = entry.objectId;
        this.objectPos = entry.objectPos;
    }

    public LogEntry(long timeMillis, String subjectId, UUID subjectUUID, WorldPos subjectPos, ActionType actionType, ObjectType objectType, String objectId, WorldPos objectPos) {
        if (subjectId == null)
            throw new IllegalArgumentException("subjectId cannot be null");
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

        this.subjectId = subjectId;
        this.timeMillis = timeMillis;
        this.subjectUUID = subjectUUID;
        this.subjectPos = subjectPos;
        this.actionType = actionType;
        this.objectType = objectType;
        this.objectId = objectId;
        this.objectPos = objectPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry entry = (LogEntry) o;
        return version == entry.version &&
                timeMillis == entry.timeMillis &&
                Objects.equals(gameVersion, entry.gameVersion) &&
                Objects.equals(subjectId, entry.subjectId) &&
                Objects.equals(subjectUUID, entry.subjectUUID) &&
                Objects.equals(subjectPos, entry.subjectPos) &&
                actionType == entry.actionType &&
                objectType == entry.objectType &&
                Objects.equals(objectId, entry.objectId) &&
                Objects.equals(objectPos, entry.objectPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, gameVersion, timeMillis, subjectId, subjectUUID, subjectPos, actionType, objectType, objectId, objectPos);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Time: ").append(PrettyUtil.timestampToString(timeMillis)).append("\n");
        builder.append("Subject: ").append(subjectId).append("{").append(subjectUUID).append("}@")
                .append(subjectPos.toString())
                .append("\n");
        builder.append("Action: ").append(actionType.toString()).append("\n");
        builder.append("Object: ").append(objectType.toString()).append("[").append(objectId).append("]@")
                .append(objectPos.toString())
                .append("\n");
        builder.append("(entryVersion: ")
                .append(version)
                .append(", gameVersion:")
                .append(gameVersion)
                .append(")");
        return builder.toString();
    }
}