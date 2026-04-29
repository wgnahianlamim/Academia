package com.agon.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.CgpaData
import com.agon.app.data.Course
import com.agon.app.data.DataStoreManager
import com.agon.app.data.Semester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CgpaViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStoreManager(application)
    
    private val _cgpaData = MutableStateFlow(CgpaData())
    val cgpaData: StateFlow<CgpaData> = _cgpaData.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.cgpaDataFlow.collect { data ->
                _cgpaData.value = data
            }
        }
    }

    fun addSemester() {
        val current = _cgpaData.value
        val newSemester = Semester(id = UUID.randomUUID().toString())
        val updated = current.copy(semesters = current.semesters + newSemester)
        saveData(updated)
    }

    fun deleteSemester(semesterId: String) {
        val current = _cgpaData.value
        val updated = current.copy(semesters = current.semesters.filter { it.id != semesterId })
        saveData(updated)
    }

    fun addCourse(semesterId: String) {
        val current = _cgpaData.value
        val updatedSemesters = current.semesters.map { s ->
            if (s.id == semesterId) {
                s.copy(courses = s.courses + Course(id = UUID.randomUUID().toString()))
            } else s
        }
        saveData(current.copy(semesters = updatedSemesters))
    }

    fun updateCourse(semesterId: String, courseId: String, name: String, credits: Double, grade: Double) {
        val current = _cgpaData.value
        val updatedSemesters = current.semesters.map { s ->
            if (s.id == semesterId) {
                val updatedCourses = s.courses.map { c ->
                    if (c.id == courseId) c.copy(name = name, credits = credits, grade = grade) else c
                }
                s.copy(courses = updatedCourses)
            } else s
        }
        saveData(current.copy(semesters = updatedSemesters))
    }

    fun deleteCourse(semesterId: String, courseId: String) {
        val current = _cgpaData.value
        val updatedSemesters = current.semesters.map { s ->
            if (s.id == semesterId) {
                s.copy(courses = s.courses.filter { it.id != courseId })
            } else s
        }
        saveData(current.copy(semesters = updatedSemesters))
    }

    private fun saveData(data: CgpaData) {
        _cgpaData.value = data
        viewModelScope.launch {
            dataStore.saveCgpaData(data)
        }
    }
}
