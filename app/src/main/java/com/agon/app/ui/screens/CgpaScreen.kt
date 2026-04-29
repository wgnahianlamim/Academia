package com.agon.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.data.Course
import com.agon.app.data.Semester
import com.agon.app.ui.components.GlassBackground
import com.agon.app.ui.components.GlassCard
import com.agon.app.viewmodel.CgpaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaScreen(viewModel: CgpaViewModel = viewModel()) {
    val cgpaData by viewModel.cgpaData.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Normal", "Projected", "Predictor")

    GlassBackground {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text("Smart CGPA Planner", color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }
                }
            },
            containerColor = Color.Transparent,
            floatingActionButton = {
                if (selectedTab == 0) {
                    FloatingActionButton(
                        onClick = { viewModel.addSemester() },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Semester", tint = Color.White)
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (selectedTab) {
                    0 -> NormalMode(cgpaData.semesters, viewModel)
                    1 -> ProjectedMode()
                    2 -> PredictorMode()
                }
            }
        }
    }
}

@Composable
fun NormalMode(semesters: List<Semester>, viewModel: CgpaViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            OverallCgpaCard(semesters)
        }
        items(semesters) { semester ->
            SemesterCard(
                semester = semester,
                onAddCourse = { viewModel.addCourse(semester.id) },
                onUpdateCourse = { sId, cId, name, cred, grade -> 
                    viewModel.updateCourse(sId, cId, name, cred, grade) 
                },
                onDeleteCourse = { sId, cId -> viewModel.deleteCourse(sId, cId) },
                onDeleteSemester = { viewModel.deleteSemester(semester.id) }
            )
        }
        item { DeveloperSignature() }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun ProjectedMode() {
    var currentCgpa by remember { mutableStateOf("") }
    var completedSemesters by remember { mutableStateOf("") }
    var totalSemesters by remember { mutableStateOf("") }
    var targetCgpa by remember { mutableStateOf("") }

    val current = currentCgpa.toDoubleOrNull() ?: 0.0
    val completed = completedSemesters.toDoubleOrNull() ?: 0.0
    val total = totalSemesters.toDoubleOrNull() ?: 0.0
    val target = targetCgpa.toDoubleOrNull() ?: 0.0

    val required = if (total > completed && completed >= 0) {
        ((target * total) - (current * completed)) / (total - completed)
    } else 0.0

    val animatedRequired by animateFloatAsState(
        targetValue = required.toFloat(),
        animationSpec = tween(1000)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Required Average GPA", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                Text(
                    text = if (required > 0 && required <= 4.0) String.format("%.2f", animatedRequired)
                           else if (required > 4.0) "Impossible" else "0.00",
                    color = if (required > 4.0) Color.Red else Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                if (required > 0 && required <= 4.0) {
                    Text("for the remaining ${total - completed} semesters", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentCgpa,
                    onValueChange = { currentCgpa = it },
                    label = { Text("Current CGPA") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = completedSemesters,
                        onValueChange = { completedSemesters = it },
                        label = { Text("Completed Sems") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors()
                    )
                    OutlinedTextField(
                        value = totalSemesters,
                        onValueChange = { totalSemesters = it },
                        label = { Text("Total Sems") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors()
                    )
                }
                OutlinedTextField(
                    value = targetCgpa,
                    onValueChange = { targetCgpa = it },
                    label = { Text("Target CGPA") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        DeveloperSignature()
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun PredictorMode() {
    var midWeight by remember { mutableStateOf("30") }
    var assessWeight by remember { mutableStateOf("20") }
    var finalWeight by remember { mutableStateOf("50") }

    var midScore by remember { mutableStateOf("") }
    var assessScore by remember { mutableStateOf("") }
    var targetGradePercent by remember { mutableStateOf("80") } // e.g. 80% for A+

    val mWeight = midWeight.toDoubleOrNull() ?: 0.0
    val aWeight = assessWeight.toDoubleOrNull() ?: 0.0
    val fWeight = finalWeight.toDoubleOrNull() ?: 0.0

    val mScore = midScore.toDoubleOrNull() ?: 0.0
    val aScore = assessScore.toDoubleOrNull() ?: 0.0
    val target = targetGradePercent.toDoubleOrNull() ?: 0.0

    // Calculate how many raw marks needed in final to hit target percent overall
    val currentTotal = mScore + aScore
    val neededTotal = target - currentTotal
    val requiredFinalScore = if (fWeight > 0) neededTotal else 0.0

    val animatedRequired by animateFloatAsState(
        targetValue = requiredFinalScore.toFloat(),
        animationSpec = tween(1000)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Required Final Exam Score", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                Text(
                    text = if (requiredFinalScore <= fWeight && requiredFinalScore > 0) String.format("%.1f", animatedRequired) + " / $fWeight"
                           else if (requiredFinalScore > fWeight) "Impossible" else "0.0 / $fWeight",
                    color = if (requiredFinalScore > fWeight) Color.Red else Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("to achieve ${targetGradePercent}% overall", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Mark Distribution (%)", color = Color.White, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = midWeight, onValueChange = { midWeight = it }, label = { Text("Mid") }, modifier = Modifier.weight(1f), colors = textFieldColors())
                    OutlinedTextField(value = assessWeight, onValueChange = { assessWeight = it }, label = { Text("Assess") }, modifier = Modifier.weight(1f), colors = textFieldColors())
                    OutlinedTextField(value = finalWeight, onValueChange = { finalWeight = it }, label = { Text("Final") }, modifier = Modifier.weight(1f), colors = textFieldColors())
                }
                
                Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                Text("Your Scores", color = Color.White, fontWeight = FontWeight.Bold)
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = midScore, onValueChange = { midScore = it }, label = { Text("Mid Score") }, modifier = Modifier.weight(1f), colors = textFieldColors())
                    OutlinedTextField(value = assessScore, onValueChange = { assessScore = it }, label = { Text("Assess Score") }, modifier = Modifier.weight(1f), colors = textFieldColors())
                }

                OutlinedTextField(
                    value = targetGradePercent,
                    onValueChange = { targetGradePercent = it },
                    label = { Text("Target Grade (%) e.g. 80 for A+") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        DeveloperSignature()
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun OverallCgpaCard(semesters: List<Semester>) {
    var totalCredits = 0.0
    var totalPoints = 0.0
    
    semesters.forEach { s ->
        s.courses.forEach { c ->
            totalCredits += c.credits
            totalPoints += c.credits * c.grade
        }
    }
    
    val cgpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0
    val animatedCgpa by animateFloatAsState(targetValue = cgpa.toFloat(), animationSpec = tween(1500))

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Overall CGPA", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
            Text(
                text = String.format("%.2f", animatedCgpa),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Total Credits: $totalCredits", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}

@Composable
fun SemesterCard(
    semester: Semester,
    onAddCourse: () -> Unit,
    onUpdateCourse: (String, String, String, Double, Double) -> Unit,
    onDeleteCourse: (String, String) -> Unit,
    onDeleteSemester: () -> Unit
) {
    var semesterCredits = 0.0
    var semesterPoints = 0.0
    
    semester.courses.forEach { c ->
        semesterCredits += c.credits
        semesterPoints += c.credits * c.grade
    }
    val sgpa = if (semesterCredits > 0) semesterPoints / semesterCredits else 0.0
    val animatedSgpa by animateFloatAsState(targetValue = sgpa.toFloat(), animationSpec = tween(1000))

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Semester SGPA: ${String.format("%.2f", animatedSgpa)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDeleteSemester) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Semester", tint = Color.Red.copy(alpha = 0.8f))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            semester.courses.forEach { course ->
                CourseRow(
                    course = course,
                    onUpdate = { n, c, g -> onUpdateCourse(semester.id, course.id, n, c, g) },
                    onDelete = { onDeleteCourse(semester.id, course.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = onAddCourse,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Text("Add Course", color = Color.White)
            }
        }
    }
}

@Composable
fun CourseRow(
    course: Course,
    onUpdate: (String, Double, Double) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = course.name,
            onValueChange = { onUpdate(it, course.credits, course.grade) },
            label = { Text("Course") },
            modifier = Modifier.weight(1.5f),
            singleLine = true,
            colors = textFieldColors()
        )
        OutlinedTextField(
            value = course.credits.toString(),
            onValueChange = { 
                val cred = it.toDoubleOrNull() ?: 0.0
                onUpdate(course.name, cred, course.grade) 
            },
            label = { Text("Cr") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = textFieldColors()
        )
        OutlinedTextField(
            value = course.grade.toString(),
            onValueChange = { 
                val grade = it.toDoubleOrNull() ?: 0.0
                onUpdate(course.name, course.credits, grade) 
            },
            label = { Text("Gr") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = textFieldColors()
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.5f))
        }
    }
}
