package com.agon.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.ui.components.GlassBackground
import com.agon.app.ui.components.GlassCard
import com.agon.app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val profile by viewModel.profile.collectAsState()

    GlassBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Student Information", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        
                        OutlinedTextField(
                            value = profile.name,
                            onValueChange = { viewModel.updateProfile(profile.copy(name = it)) },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = profile.studentId,
                            onValueChange = { viewModel.updateProfile(profile.copy(studentId = it)) },
                            label = { Text("Student ID") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = profile.level,
                                onValueChange = { viewModel.updateProfile(profile.copy(level = it)) },
                                label = { Text("Level") },
                                modifier = Modifier.weight(1f),
                                colors = textFieldColors()
                            )
                            OutlinedTextField(
                                value = profile.term,
                                onValueChange = { viewModel.updateProfile(profile.copy(term = it)) },
                                label = { Text("Term") },
                                modifier = Modifier.weight(1f),
                                colors = textFieldColors()
                            )
                            OutlinedTextField(
                                value = profile.section,
                                onValueChange = { viewModel.updateProfile(profile.copy(section = it)) },
                                label = { Text("Section") },
                                modifier = Modifier.weight(1f),
                                colors = textFieldColors()
                            )
                        }
                        OutlinedTextField(
                            value = profile.department,
                            onValueChange = { viewModel.updateProfile(profile.copy(department = it)) },
                            label = { Text("Department") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = profile.university,
                            onValueChange = { viewModel.updateProfile(profile.copy(university = it)) },
                            label = { Text("University") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                    }
                }
                DeveloperSignature()
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
