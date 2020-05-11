package com.dawoodamir.tibgodoc.models

data class SignUpResponse(
    val error: Boolean,
    val message: String,
    val user: User
)