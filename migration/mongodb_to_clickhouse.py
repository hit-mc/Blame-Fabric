#!/usr/bin/python3
'''
This script exports data from Blame MongoDB to TSV format.
You can redirect the output to clickhouse-client to migrate to new Blame which uses ClickHouse.
'''

import uuid

import pymongo

from sys import stderr

MG_URL = 'mongodb://@localhost:27017/'  # Change your credential here

stdout = open(1, "w", buffering=1024 * 1024 * 1024)  # 1GB buffer, you can reduce this


def read_mongo():
    conn = pymongo.MongoClient(MG_URL)
    table = conn['blame']['survival']
    n = 0
    for i, doc in enumerate(table.find()):
        print(
            doc['action_type'],
            doc['game_version'],
            doc['object_id'],
            doc['object_pos']['world'],
            int(doc['object_pos']['x']),
            int(doc['object_pos']['y']),
            int(doc['object_pos']['z']),
            doc['object_type'],
            doc['subject_id'],
            doc['subject_pos']['world'],
            doc['subject_pos']['x'],
            doc['subject_pos']['y'],
            doc['subject_pos']['z'],
            uuid.UUID(bytes=doc['subject_uuid']),
            doc['timestamp_millis'],
            sep='\t',
            file=stdout,
        )
        n += 1
        if n == 10000:
            print('Rows written:', i+1, file=stderr)
            n = 0
    print('OK', file=stderr)


def main():
    read_mongo()


if __name__ == '__main__':
    main()
