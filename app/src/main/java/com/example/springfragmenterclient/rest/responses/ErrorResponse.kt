package com.example.springfragmenterclient.rest.responses

import java.sql.Timestamp

data class ErrorResponse(
    val timestamp: Timestamp,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)