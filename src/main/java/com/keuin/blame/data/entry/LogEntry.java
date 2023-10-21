package com.keuin.blame.data.entry;

import com.clickhouse.data.ClickHousePipedOutputStream;
import com.clickhouse.data.format.BinaryStreamUtils;
import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import com.keuin.blame.util.PrettyUtil;
import com.keuin.blame.util.UuidUtils;
import net.minecraft.MinecraftVersion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;


public class LogEntry {

    /*
         {
            "version": 1, // int
            "subject": {
                "uuid": player_uuid_bytes, // bytes
                "id": player_id, // string
                "pos": {
                    "world": world_id, // string
                    "x": pos_x, // float
                    "y": pos_y, // float
                    "z": pos_z, // float
                }
            },
            "action": BLOCK_BREAK | BLOCK_PLACE | BLOCK_USE | ENTITY_USE | ENTITY_ATTACK | ITEM_USE, // int
            "object": {
                "type": OBJECT_BLOCK | OBJECT_ENTITY, // int
                "id": object_id, // string
                "pos": {
                    "world": world_id, // string
                    "x": pos_x, // float
                    "y": pos_y, // float
                    "z": pos_z, // float
                }
            }
        }
    */

    public int version = 1;

    public String gameVersion = MinecraftVersion.field_25319.getName();

    public long timeMillis = 0; // timestamp_millis

    public String subjectId = "";

    public UUID subjectUUID = UuidUtils.UUID_NULL;

    public WorldPos subjectPos = WorldPos.NULL_POS;

    public ActionType actionType = ActionType.NULL;

    public ObjectType objectType = ObjectType.NULL;

    public String objectId = "";

    public WorldPos objectPos = WorldPos.NULL_POS;

    public LogEntry() {
    }

    // copy constructor
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

        // v1 params
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
        return "Time: " + PrettyUtil.timestampToString(timeMillis) + "\n" +
                "Subject: " + subjectId + "{" + subjectUUID + "}@" +
                subjectPos.toString() +
                "\n" +
                "Action: " + actionType.toString() + "\n" +
                "Object: " + objectType.toString() + "[" + objectId + "]@" +
                objectPos.toString() +
                "\n" +
                "(entryVersion: " +
                version +
                ", gameVersion:" +
                gameVersion +
                ")";
    }

    public void write(ClickHousePipedOutputStream os) throws IOException {
        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeInt8(os, actionType.getValue());

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeFixedString(os, gameVersion, 8);

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeString(os, objectId);

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeFixedString(os, objectPos.getWorld(), 24);

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeInt64(os, (long) (objectPos.getX()));

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeInt64(os, (long) (objectPos.getY()));

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeInt64(os, (long) (objectPos.getZ()));

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeInt32(os, objectType.getValue());

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeString(os, subjectId);

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeFixedString(os, subjectPos.getWorld(), 24);

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeFloat64(os, subjectPos.getX());

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeFloat64(os, subjectPos.getY());

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeFloat64(os, subjectPos.getZ());

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeFixedString(os, subjectUUID.toString(), 36); // lowercase

        BinaryStreamUtils.writeNonNull(os);
        BinaryStreamUtils.writeInt64(os, timeMillis);
    }
}
