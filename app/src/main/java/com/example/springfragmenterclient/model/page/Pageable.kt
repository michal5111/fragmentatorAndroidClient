package com.example.springfragmenterclient.model.page

data class Pageable(
    val sort: Sort,
    val offset: Number,
    val pageSize: Number,
    val pageNumber: Number,
    val paged: Boolean,
    val unpaged: Boolean
)