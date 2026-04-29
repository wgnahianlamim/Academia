package com.agon.app.data

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val name: String = "",
    val studentId: String = "",
    val level: String = "",
    val term: String = "",
    val section: String = "",
    val department: String = "",
    val university: String = "",
    val logoPath: String? = null
)

@Serializable
data class Course(
    val id: String,
    val name: String = "",
    val credits: Double = 3.0,
    val grade: Double = 4.0
)

@Serializable
data class Semester(
    val id: String,
    val name: String = "",
    val courses: List<Course> = emptyList()
)

@Serializable
data class CgpaData(
    val semesters: List<Semester> = emptyList()
)
