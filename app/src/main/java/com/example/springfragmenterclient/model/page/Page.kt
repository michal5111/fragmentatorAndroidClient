package com.example.springfragmenterclient.model.page

data class Page<T>(
    val content: List<T>,
    val pageable: Pageable,
    val totalPages: Number,
    val totalElements: Number,
    val last: Boolean,
    val size: Number,
    val number: Number,
    val sort: Sort,
    val numberOfElements: Number,
    val first: Boolean,
    val empty: Boolean
)