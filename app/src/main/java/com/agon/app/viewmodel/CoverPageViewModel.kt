package com.agon.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import com.agon.app.data.DataStoreManager
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

data class CoverPageState(
    val documentType: String = "Assignment",
    val reportNo: String = "",
    val documentName: String = "",
    val courseName: String = "",
    val courseCode: String = "",
    val submissionDate: String = "",
    val facultyName: String = "",
    val facultyDesignation: String = "",
    val facultyDepartment: String = "",
    val hasSecondFaculty: Boolean = false,
    val facultyName2: String = "",
    val facultyDesignation2: String = "",
    val facultyDepartment2: String = ""
)

class CoverPageViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStoreManager(application)
    
    private val _state = MutableStateFlow(CoverPageState())
    val state: StateFlow<CoverPageState> = _state.asStateFlow()

    fun updateState(newState: CoverPageState) {
        _state.value = newState
    }

    fun saveLogoLocally(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "university_logo.png")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generatePdf(context: Context): File? {
        val currentState = _state.value
        val profile = runBlocking { dataStore.userProfileFlow.first() }

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()
        
        val pdfFile = File(downloadsDir, "Academia_${currentState.documentType}_${System.currentTimeMillis()}.pdf")
        
        try {
            val writer = PdfWriter(pdfFile.absolutePath)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)
            
            // Set margins (top, right, bottom, left)
            document.setMargins(50f, 50f, 50f, 50f)
            
            val timesFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)
            document.setFont(timesFont)

            // Header Section: Logo
            if (!profile.logoPath.isNullOrEmpty()) {
                val logoFile = File(profile.logoPath)
                if (logoFile.exists()) {
                    try {
                        val imageData = ImageDataFactory.create(logoFile.absolutePath)
                        val image = Image(imageData)
                        image.setWidth(100f)
                        image.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                        image.setMarginBottom(5f) // Tight spacing
                        document.add(image)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Header Section: University Name & Department
            document.add(
                Paragraph(profile.university.ifEmpty { "UNIVERSITY NAME" })
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20f)
                    .setMarginBottom(0f)
                    .setMultipliedLeading(1.0f)
            )
            document.add(
                Paragraph("Department of ${profile.department.ifEmpty { "DEPARTMENT" }}")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20f)
                    .setMarginBottom(25f)
                    .setMultipliedLeading(1.0f)
            )

            // Middle Section: Document Type
            val docTypeLabel = if (currentState.documentType == "Lab Report") "Lab Report NO:" else "${currentState.documentType} NO:"
            if (currentState.reportNo.isNotEmpty()) {
                document.add(
                    Paragraph("$docTypeLabel ${currentState.reportNo}")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(18f)
                        .setMarginBottom(20f)
                )
            } else {
                document.add(
                    Paragraph(currentState.documentType)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(18f)
                        .setMarginBottom(20f)
                )
            }

            // Middle Section: Data Block (Centered block, Left-aligned text)
            val nameLabel = if (currentState.documentType == "Lab Report") "Report Name: " else "Name: "
            
            val infoTable = Table(1)
            infoTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
            infoTable.setMarginBottom(100f) // Push boxes further down
            
            val infoCell = Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
            val middleSection = Paragraph()
                .add(Text(nameLabel).setBold()).add(Text("${currentState.documentName}\n"))
                .add(Text("Course Title: ").setBold()).add(Text("${currentState.courseName}\n"))
                .add(Text("Course Code: ").setBold()).add(Text("${currentState.courseCode}\n"))
                .add(Text("Date of Submission: ").setBold()).add(Text(currentState.submissionDate))
                .setFontSize(16f)
                .setMultipliedLeading(1.5f)
            
            infoCell.add(middleSection)
            infoTable.addCell(infoCell)
            document.add(infoTable)

            // Information Boxes (Horizontal Layout)
            // 3 columns: Left Box (48%), Space (4%), Right Box (48%)
            val table = Table(UnitValue.createPercentArray(floatArrayOf(48f, 4f, 48f))).setWidth(UnitValue.createPercentValue(100f))

            // Left Box: Submitted By
            val leftCell = Cell().setPadding(12f).setBorder(SolidBorder(ColorConstants.BLACK, 1f))
            leftCell.add(
                Paragraph("Submitted by:")
                    .setBold()
                    .setFontSize(16f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10f)
            )
            leftCell.add(Paragraph()
                .add(Text("Name: ").setBold()).add(Text("${profile.name}\n"))
                .add(Text("ID: ").setBold()).add(Text("${profile.studentId}\n"))
                .add(Text("Level: ").setBold()).add(Text("${profile.level}\n"))
                .add(Text("Term: ").setBold()).add(Text("${profile.term}\n"))
                .add(Text("Section: ").setBold()).add(Text("${profile.section}\n"))
                .add(Text("Department: ").setBold()).add(Text(profile.department))
                .setFontSize(14f)
                .setMultipliedLeading(1.3f)
            )
            table.addCell(leftCell)

            // Space
            val spaceCell = Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            table.addCell(spaceCell)

            // Right Box: Submitted To
            val rightCell = Cell().setPadding(12f).setBorder(SolidBorder(ColorConstants.BLACK, 1f))
            rightCell.add(
                Paragraph("Submitted to:")
                    .setBold()
                    .setFontSize(16f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10f)
            )
            rightCell.add(Paragraph()
                .add(Text("Name: ").setBold()).add(Text("${currentState.facultyName}\n"))
                .add(Text("Designation: ").setBold()).add(Text("${currentState.facultyDesignation}\n"))
                .add(Text("Department: ").setBold()).add(Text(currentState.facultyDepartment))
                .setFontSize(14f)
                .setMultipliedLeading(1.3f)
            )
            
            if (currentState.hasSecondFaculty) {
                rightCell.add(Paragraph("\n").setFontSize(14f).setMultipliedLeading(1.3f))
                rightCell.add(Paragraph()
                    .add(Text("Name: ").setBold()).add(Text("${currentState.facultyName2}\n"))
                    .add(Text("Designation: ").setBold()).add(Text("${currentState.facultyDesignation2}\n"))
                    .add(Text("Department: ").setBold()).add(Text(currentState.facultyDepartment2))
                    .setFontSize(14f)
                    .setMultipliedLeading(1.3f)
                )
            }
            table.addCell(rightCell)

            document.add(table)
            document.close()
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
