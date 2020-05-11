package com.dawoodamir.tibgodoc.models

data class LoginUserResponse(
    val error: Boolean,
    val message: String,
    val user: User
)