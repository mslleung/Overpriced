package com.igrocery.overpriced.domain

/**
 * A layer supertype providing common non-domain-specific fields for technical reasons.
 * All aggregate roots must inherit this.
 */
abstract class AggregateRoot(
    id: Long,
    creationTimestamp: Long,
    updateTimestamp: Long,
) : Entity(id, creationTimestamp, updateTimestamp)
