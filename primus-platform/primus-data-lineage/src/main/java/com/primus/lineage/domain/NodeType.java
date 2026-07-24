package com.primus.lineage.domain;

/** Classifies a node in the lineage graph. */
public enum NodeType {
    SOURCE,       // originating data source (DB, API, file)
    EXPORT,       // a Primus export operation
    RETRIEVAL,    // a Primus retrieval event
    STORAGE,      // where data is persisted
    ARCHIVE,      // long-term archive location
    CONSUMER      // downstream system or user that received the data
}
