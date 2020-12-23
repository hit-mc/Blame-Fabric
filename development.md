# 数据库结构

基于MongoDB。

## Collection: logs

```json
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
```

