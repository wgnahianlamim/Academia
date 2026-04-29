package com.agon.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.agon.app.ui.components.GlassBackground
import com.agon.app.ui.components.GlassCard
import com.agon.app.viewmodel.CoverPageViewModel
import com.agon.app.viewmodel.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverPageScreen(
    viewModel: CoverPageViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val profile by profileViewModel.profile.collectAsState()
    
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedDocType by remember { mutableStateOf(false) }
    val documentTypes = listOf("Assignment", "Lab Report", "Presentation", "Project")

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val localPath = viewModel.saveLogoLocally(context, it)
            if (localPath != null) {
                profileViewModel.updateProfile(profile.copy(logoPath = localPath))
            }
        }
    }

    GlassBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cover Page Generator", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("University Branding", color = Color.White, style = MaterialTheme.typography.titleMedium)
                            Button(
                                onClick = { logoPickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Logo")
                            }
                        }
                        
                        OutlinedTextField(
                            value = profile.university,
                            onValueChange = { profileViewModel.updateProfile(profile.copy(university = it)) },
                            label = { Text("University Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = profile.department,
                            onValueChange = { profileViewModel.updateProfile(profile.copy(department = it)) },
                            label = { Text("Department") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                    }
                }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Assignment Details", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedDocType,
                            onExpandedChange = { expandedDocType = !expandedDocType }
                        ) {
                            OutlinedTextField(
                                value = state.documentType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Document Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDocType) },
                                colors = textFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDocType,
                                onDismissRequest = { expandedDocType = false }
                            ) {
                                documentTypes.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption) },
                                        onClick = {
                                            viewModel.updateState(state.copy(documentType = selectionOption))
                                            expandedDocType = false
                                        }
                                    )
                                }
                            }
                        }

                        val noLabel = if (state.documentType == "Lab Report") "Lab Report NO" else "${state.documentType} NO"
                        val nameLabel = if (state.documentType == "Lab Report") "Report Name" else "Name"

                        OutlinedTextField(
                            value = state.reportNo,
                            onValueChange = { viewModel.updateState(state.copy(reportNo = it)) },
                            label = { Text(noLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = state.documentName,
                            onValueChange = { viewModel.updateState(state.copy(documentName = it)) },
                            label = { Text(nameLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = state.courseName,
                            onValueChange = { viewModel.updateState(state.copy(courseName = it)) },
                            label = { Text("Course Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = state.courseCode,
                            onValueChange = { viewModel.updateState(state.copy(courseCode = it)) },
                            label = { Text("Course Code") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        
                        Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                            OutlinedTextField(
                                value = state.submissionDate,
                                onValueChange = { },
                                label = { Text("Submission Date") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                colors = textFieldColors(),
                                trailingIcon = {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = Color.White)
                                }
                            )
                        }
                    }
                }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Submitted To", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        
                        OutlinedTextField(
                            value = state.facultyName,
                            onValueChange = { viewModel.updateState(state.copy(facultyName = it)) },
                            label = { Text("Faculty Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = state.facultyDesignation,
                            onValueChange = { viewModel.updateState(state.copy(facultyDesignation = it)) },
                            label = { Text("Designation") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = state.facultyDepartment,
                            onValueChange = { viewModel.updateState(state.copy(facultyDepartment = it)) },
                            label = { Text("Department") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )

                        if (state.hasSecondFaculty) {
                            Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Second Faculty", color = Color.White, style = MaterialTheme.typography.titleSmall)
                                IconButton(onClick = { viewModel.updateState(state.copy(hasSecondFaculty = false, facultyName2 = "", facultyDesignation2 = "", facultyDepartment2 = "")) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.8f))
                                }
                            }
                            OutlinedTextField(
                                value = state.facultyName2,
                                onValueChange = { viewModel.updateState(state.copy(facultyName2 = it)) },
                                label = { Text("Faculty Name 2") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors()
                            )
                            OutlinedTextField(
                                value = state.facultyDesignation2,
                                onValueChange = { viewModel.updateState(state.copy(facultyDesignation2 = it)) },
                                label = { Text("Designation 2") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors()
                            )
                            OutlinedTextField(
                                value = state.facultyDepartment2,
                                onValueChange = { viewModel.updateState(state.copy(facultyDepartment2 = it)) },
                                label = { Text("Department 2") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors()
                            )
                        } else {
                            TextButton(onClick = { viewModel.updateState(state.copy(hasSecondFaculty = true)) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Second Faculty")
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val file = viewModel.generatePdf(context)
                        if (file != null) {
                            Toast.makeText(context, "Download Successful! Saved to Downloads.", Toast.LENGTH_LONG).show()
                            sharePdf(context, file)
                        } else {
                            Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Download as PDF")
                }
                
                DeveloperSignature()
                Spacer(modifier = Modifier.height(80.dp))
            }
            
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
                                viewModel.updateState(state.copy(submissionDate = formatted))
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

@Composable
fun DeveloperSignature() {
    Text(
        text = "Developed by WG Nahian Lamim",
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    )
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    disabledBorderColor = Color.White.copy(alpha = 0.2f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
    disabledLabelColor = Color.White.copy(alpha = 0.5f)
)

private fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Cover Page"))
}
