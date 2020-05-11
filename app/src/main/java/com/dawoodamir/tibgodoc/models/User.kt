package com.dawoodamir.tibgodoc.models

data class User(
    val id: Int,
    val d_id: Int,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val ADT: String,
    val address: String,
    val chargePerVisit: Int,
    val latLng: String,
    val userType: String,
    val isSpecialist: Int,
    val workingHours: String,
    val specialistIn: String
)