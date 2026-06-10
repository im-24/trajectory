// services/ReportGenerator.kt
package services

import data.models.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import org.apache.poi.xwpf.usermodel.*
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.layout.element.List as ITextList
import com.itextpdf.layout.element.ListItem

class ReportGenerator {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * Generate a Word document report
     */
    fun generateWordReport(reportData: ReportData, outputPath: String) {
        val document = XWPFDocument()

        // 1. Cover Page
        createWordCoverPage(document, reportData)

        // 2. Program Summary
        createWordProgramSummary(document, reportData)

        // 3. Projectile Characteristics
        createWordProjectileTable(document, reportData)

        // 4. Environment Parameters
        createWordEnvironmentTable(document, reportData)

        // 5. Simulation Results
        createWordSimulationResults(document, reportData)

        // 6. Trajectory Data Table
        createWordTrajectoryTable(document, reportData)

        // Save document
        FileOutputStream(outputPath).use { out ->
            document.write(out)
        }
        document.close()
    }

    /**
     * Generate a PDF document report
     */
    fun generatePDFReport(reportData: ReportData, outputPath: String) {
        val writer = PdfWriter(outputPath)
        val pdf = PdfDocument(writer)
        val document = Document(pdf, PageSize.A4)
        document.setMargins(50f, 50f, 50f, 50f)

        // Create fonts
        val titleFont = PdfFontFactory.createFont()
        val headingFont = PdfFontFactory.createFont()
        val normalFont = PdfFontFactory.createFont()

        // 1. Cover Page
        createPDFCoverPage(document, reportData, titleFont, headingFont)

        // 2. Program Summary
        createPDFProgramSummary(document, reportData, headingFont, normalFont)

        // 3. Projectile Characteristics
        createPDFProjectileTable(document, reportData, headingFont, normalFont)

        // 4. Environment Parameters
        createPDFEnvironmentTable(document, reportData, headingFont, normalFont)

        // 5. Simulation Results
        createPDFSimulationResults(document, reportData, headingFont, normalFont)

        // 6. Trajectory Data Table
        createPDFTrajectoryTable(document, reportData, headingFont, normalFont)

        document.close()
        pdf.close()
    }

    // ==================== WORD GENERATION METHODS ====================

    private fun createWordCoverPage(document: XWPFDocument, reportData: ReportData) {
        // Title
        val titleParagraph = document.createParagraph()
        titleParagraph.setAlignment(ParagraphAlignment.CENTER)
        val titleRun = titleParagraph.createRun()
        titleRun.setText(reportData.title)
        titleRun.setFontSize(28)
        titleRun.setBold(true)
        titleRun.setFontFamily("Arial")

        // Empty lines
        for (i in 1..5) {
            document.createParagraph().createRun().addBreak()
        }

        // Subtitle
        val subtitleParagraph = document.createParagraph()
        subtitleParagraph.setAlignment(ParagraphAlignment.CENTER)
        val subtitleRun = subtitleParagraph.createRun()
        subtitleRun.setText("Comprehensive Simulation Report")
        subtitleRun.setFontSize(18)
        subtitleRun.setFontFamily("Arial")

        for (i in 1..3) {
            document.createParagraph().createRun().addBreak()
        }

        // Project info
        val infoParagraph = document.createParagraph()
        infoParagraph.setAlignment(ParagraphAlignment.CENTER)
        val infoRun = infoParagraph.createRun()
        infoRun.setText("Generated: ${dateFormat.format(reportData.date)}")
        infoRun.setFontSize(12)
        infoRun.addBreak()
        infoRun.setText("Software: ${reportData.programInfo.name} v${reportData.programInfo.version}")

        // Page break
        document.createParagraph().createRun().addBreak(BreakType.PAGE)
    }

    private fun createWordProgramSummary(document: XWPFDocument, reportData: ReportData) {
        // Heading
        val heading = document.createParagraph()
        val headingRun = heading.createRun()
        headingRun.setText("1. PROGRAM INFORMATION")
        headingRun.setBold(true)
        headingRun.setFontSize(16)
        headingRun.setColor("7B5EA7")

        document.createParagraph().createRun().addBreak()

        // Description
        val descParagraph = document.createParagraph()
        descParagraph.createRun().setText(reportData.programInfo.description)

        document.createParagraph().createRun().addBreak()

        // Features list
        val featuresHeading = document.createParagraph()
        featuresHeading.createRun().setText("Key Features:")
        featuresHeading.createRun().setBold(true)

        for (feature in reportData.programInfo.features) {
            val featureParagraph = document.createParagraph()
            featureParagraph.createRun().setText("• $feature")
        }

        document.createParagraph().createRun().addBreak()
    }

    private fun createWordProjectileTable(document: XWPFDocument, reportData: ReportData) {
        // Heading
        val heading = document.createParagraph()
        val headingRun = heading.createRun()
        headingRun.setText("2. PROJECTILE CHARACTERISTICS")
        headingRun.setBold(true)
        headingRun.setFontSize(16)
        headingRun.setColor("7B5EA7")

        document.createParagraph().createRun().addBreak()

        // Create table
        val table = document.createTable(8, 2)
        table.setWidth("100%")

        // Add data rows
        addWordTableRow(table, 0, "Property", "Value", true)
        addWordTableRow(table, 1, "Name", reportData.projectileParameters.name, false)
        addWordTableRow(table, 2, "Mass", "${reportData.projectileParameters.mass} kg", false)
        addWordTableRow(table, 3, "Radius", "${reportData.projectileParameters.radius} m", false)
        addWordTableRow(table, 4, "Diameter", "${reportData.projectileParameters.diameter} m", false)
        addWordTableRow(table, 5, "Volume", String.format("%.4f", reportData.projectileParameters.volume) + " m³", false)
        addWordTableRow(table, 6, "Surface Area", String.format("%.4f", reportData.projectileParameters.surfaceArea) + " m²", false)
        addWordTableRow(table, 7, "Material", reportData.projectileParameters.material, false)

        document.createParagraph().createRun().addBreak()
    }

    private fun createWordEnvironmentTable(document: XWPFDocument, reportData: ReportData) {
        // Heading
        val heading = document.createParagraph()
        val headingRun = heading.createRun()
        headingRun.setText("3. ENVIRONMENT CONDITIONS")
        headingRun.setBold(true)
        headingRun.setFontSize(16)
        headingRun.setColor("7B5EA7")

        document.createParagraph().createRun().addBreak()

        // Create table
        val table = document.createTable(8, 2)
        table.setWidth("100%")

        // Add data rows
        addWordTableRow(table, 0, "Parameter", "Value", true)
        addWordTableRow(table, 1, "Gravity", "${reportData.environmentParameters.gravity} m/s²", false)
        addWordTableRow(table, 2, "Air Density", "${reportData.environmentParameters.airDensity} kg/m³", false)
        addWordTableRow(table, 3, "Wind Speed", "${reportData.environmentParameters.windSpeed} m/s", false)
        addWordTableRow(table, 4, "Wind Direction", "${reportData.environmentParameters.windDirection}°", false)
        addWordTableRow(table, 5, "Temperature", "${reportData.environmentParameters.temperature}°C", false)
        addWordTableRow(table, 6, "Pressure", "${reportData.environmentParameters.pressure} Pa", false)
        addWordTableRow(table, 7, "Humidity", "${(reportData.environmentParameters.humidity * 100).toInt()}%", false)

        document.createParagraph().createRun().addBreak()
    }

    private fun createWordSimulationResults(document: XWPFDocument, reportData: ReportData) {
        // Heading
        val heading = document.createParagraph()
        val headingRun = heading.createRun()
        headingRun.setText("4. SIMULATION RESULTS")
        headingRun.setBold(true)
        headingRun.setFontSize(16)
        headingRun.setColor("7B5EA7")

        document.createParagraph().createRun().addBreak()

        // Create table
        val table = document.createTable(11, 2)
        table.setWidth("100%")

        // Add data rows
        addWordTableRow(table, 0, "Parameter", "Value", true)
        addWordTableRow(table, 1, "Initial Velocity", "${reportData.simulationResults.initialVelocity} m/s", false)
        addWordTableRow(table, 2, "Launch Angle", "${reportData.simulationResults.launchAngle}°", false)
        addWordTableRow(table, 3, "Initial Height", "${reportData.simulationResults.initialHeight} m", false)
        addWordTableRow(table, 4, "Maximum Distance", "${reportData.simulationResults.maxDistance.toInt()} m", false)
        addWordTableRow(table, 5, "Maximum Height", "${reportData.simulationResults.maxHeight.toInt()} m", false)
        addWordTableRow(table, 6, "Time of Flight", "${reportData.simulationResults.timeOfFlight.toInt()} s", false)
        addWordTableRow(table, 7, "Impact Velocity", "${reportData.simulationResults.impactVelocity.toInt()} m/s", false)
        addWordTableRow(table, 8, "Impact Angle", "${reportData.simulationResults.impactAngle.toInt()}°", false)
        addWordTableRow(table, 9, "Maximum Speed", "${reportData.simulationResults.maxSpeed.toInt()} m/s", false)
        addWordTableRow(table, 10, "Total Energy", String.format("%.2f", reportData.simulationResults.totalEnergy) + " J", false)

        document.createParagraph().createRun().addBreak()
    }

    private fun createWordTrajectoryTable(document: XWPFDocument, reportData: ReportData) {
        // Heading
        val heading = document.createParagraph()
        val headingRun = heading.createRun()
        headingRun.setText("5. TRAJECTORY DATA")
        headingRun.setBold(true)
        headingRun.setFontSize(16)
        headingRun.setColor("7B5EA7")

        document.createParagraph().createRun().addBreak()

        // Limit to 50 points for readability
        val pointsToShow = reportData.trajectoryPoints.take(50)

        // Create table
        val table = document.createTable(pointsToShow.size + 1, 6)
        table.setWidth("100%")

        // Headers
        val headers = arrayOf("Time (s)", "Distance (m)", "Height (m)", "Velocity (m/s)", "Angle (°)", "Energy (J)")
        val headerRow = table.getRow(0)
        for (i in headers.indices) {
            val cell = headerRow.getCell(i)
            if (cell == null) {
                headerRow.createCell()
            }
            val headerCell = headerRow.getCell(i)
            headerCell.setText(headers[i])
            headerCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)

            val paragraph = headerCell.paragraphs[0]
            val run = paragraph.runs[0]
            run.setBold(true)
            run.setFontSize(11)
            run.setColor("FFFFFF")
            headerCell.setColor("7B5EA7")
        }

        // Add data rows
        pointsToShow.forEachIndexed { index, point ->
            val row = if (table.getRow(index + 1) == null) {
                table.createRow()
            } else {
                table.getRow(index + 1)
            }

            addWordDataCell(row, 0, String.format("%.2f", point.time))
            addWordDataCell(row, 1, String.format("%.1f", point.x))
            addWordDataCell(row, 2, String.format("%.1f", point.y))
            addWordDataCell(row, 3, String.format("%.1f", point.speed))
            addWordDataCell(row, 4, String.format("%.1f", point.angle))
            addWordDataCell(row, 5, String.format("%.1f", point.kineticEnergy + point.potentialEnergy))
        }

        // Add note if data was truncated
        if (reportData.trajectoryPoints.size > 50) {
            val noteParagraph = document.createParagraph()
            noteParagraph.createRun().setText("Note: Showing first 50 of ${reportData.trajectoryPoints.size} data points for readability.")
            noteParagraph.createRun().setItalic(true)
            noteParagraph.createRun().setFontSize(10)
        }

        document.createParagraph().createRun().addBreak()
    }

    private fun addWordTableRow(table: XWPFTable, rowIndex: Int, label: String, value: String, isHeader: Boolean) {
        val row = table.getRow(rowIndex)
        if (row == null) {
            table.createRow()
        }
        val dataRow = table.getRow(rowIndex)

        // Label cell
        val labelCell = dataRow.getCell(0)
        if (labelCell == null) {
            dataRow.createCell()
        }
        val labelCellFinal = dataRow.getCell(0)
        labelCellFinal.setText(label)
        labelCellFinal.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)

        if (isHeader) {
            val paragraph = labelCellFinal.paragraphs[0]
            val run = paragraph.runs[0]
            run.setBold(true)
            run.setColor("FFFFFF")
            labelCellFinal.setColor("7B5EA7")
        }

        // Value cell
        val valueCell = dataRow.getCell(1)
        if (valueCell == null) {
            dataRow.createCell()
        }
        val valueCellFinal = dataRow.getCell(1)
        valueCellFinal.setText(value)
        valueCellFinal.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)

        if (isHeader) {
            val paragraph = valueCellFinal.paragraphs[0]
            val run = paragraph.runs[0]
            run.setBold(true)
            run.setColor("FFFFFF")
            valueCellFinal.setColor("7B5EA7")
        }
    }

    private fun addWordDataCell(row: XWPFTableRow, colIndex: Int, text: String) {
        val cell = row.getCell(colIndex)
        if (cell == null) {
            row.createCell()
        }
        val dataCell = row.getCell(colIndex)
        dataCell.setText(text)
        dataCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)
    }

    // ==================== PDF GENERATION METHODS ====================

    private fun createPDFCoverPage(
        document: Document,
        reportData: ReportData,
        titleFont: com.itextpdf.kernel.font.PdfFont,
        headingFont: com.itextpdf.kernel.font.PdfFont
    ) {
        document.add(Paragraph(reportData.title)
            .setFont(titleFont)
            .setFontSize(28f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30f))

        document.add(Paragraph("Comprehensive Simulation Report")
            .setFont(headingFont)
            .setFontSize(18f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(50f))

        for (i in 1..10) {
            document.add(Paragraph(""))
        }

        document.add(Paragraph("Generated: ${dateFormat.format(reportData.date)}")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER))

        document.add(Paragraph("Software: ${reportData.programInfo.name} v${reportData.programInfo.version}")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER))
    }

    private fun createPDFProgramSummary(
        document: Document,
        reportData: ReportData,
        headingFont: com.itextpdf.kernel.font.PdfFont,
        normalFont: com.itextpdf.kernel.font.PdfFont
    ) {
        document.add(Paragraph("1. PROGRAM INFORMATION")
            .setFont(headingFont)
            .setFontSize(16f)
            .setBold()
            .setFontColor(ColorConstants.BLUE)
            .setMarginTop(20f)
            .setMarginBottom(10f))

        document.add(Paragraph(reportData.programInfo.description)
            .setFont(normalFont)
            .setFontSize(12f)
            .setMarginBottom(10f))

        document.add(Paragraph("Key Features:")
            .setFont(normalFont)
            .setFontSize(12f)
            .setBold()
            .setMarginBottom(5f))

        val list = ITextList()
        reportData.programInfo.features.forEach { feature ->
            list.add(ListItem(feature))
        }
        document.add(list)

        document.add(Paragraph(""))
    }

    private fun createPDFProjectileTable(
        document: Document,
        reportData: ReportData,
        headingFont: com.itextpdf.kernel.font.PdfFont,
        normalFont: com.itextpdf.kernel.font.PdfFont
    ) {
        document.add(Paragraph("2. PROJECTILE CHARACTERISTICS")
            .setFont(headingFont)
            .setFontSize(16f)
            .setBold()
            .setFontColor(ColorConstants.BLUE)
            .setMarginTop(20f)
            .setMarginBottom(10f))

        val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 60f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        addPDFTableCell(table, "Property", true)
        addPDFTableCell(table, "Value", true)
        addPDFTableCell(table, "Name", false)
        addPDFTableCell(table, reportData.projectileParameters.name, false)
        addPDFTableCell(table, "Mass", false)
        addPDFTableCell(table, "${reportData.projectileParameters.mass} kg", false)
        addPDFTableCell(table, "Radius", false)
        addPDFTableCell(table, "${reportData.projectileParameters.radius} m", false)
        addPDFTableCell(table, "Diameter", false)
        addPDFTableCell(table, "${reportData.projectileParameters.diameter} m", false)
        addPDFTableCell(table, "Volume", false)
        addPDFTableCell(table, String.format("%.4f", reportData.projectileParameters.volume) + " m³", false)
        addPDFTableCell(table, "Surface Area", false)
        addPDFTableCell(table, String.format("%.4f", reportData.projectileParameters.surfaceArea) + " m²", false)
        addPDFTableCell(table, "Material", false)
        addPDFTableCell(table, reportData.projectileParameters.material, false)

        document.add(table)
        document.add(Paragraph(""))
    }

    private fun createPDFEnvironmentTable(
        document: Document,
        reportData: ReportData,
        headingFont: com.itextpdf.kernel.font.PdfFont,
        normalFont: com.itextpdf.kernel.font.PdfFont
    ) {
        document.add(Paragraph("3. ENVIRONMENT CONDITIONS")
            .setFont(headingFont)
            .setFontSize(16f)
            .setBold()
            .setFontColor(ColorConstants.BLUE)
            .setMarginTop(20f)
            .setMarginBottom(10f))

        val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 60f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        addPDFTableCell(table, "Parameter", true)
        addPDFTableCell(table, "Value", true)
        addPDFTableCell(table, "Gravity", false)
        addPDFTableCell(table, "${reportData.environmentParameters.gravity} m/s²", false)
        addPDFTableCell(table, "Air Density", false)
        addPDFTableCell(table, "${reportData.environmentParameters.airDensity} kg/m³", false)
        addPDFTableCell(table, "Wind Speed", false)
        addPDFTableCell(table, "${reportData.environmentParameters.windSpeed} m/s", false)
        addPDFTableCell(table, "Wind Direction", false)
        addPDFTableCell(table, "${reportData.environmentParameters.windDirection}°", false)
        addPDFTableCell(table, "Temperature", false)
        addPDFTableCell(table, "${reportData.environmentParameters.temperature}°C", false)
        addPDFTableCell(table, "Pressure", false)
        addPDFTableCell(table, "${reportData.environmentParameters.pressure} Pa", false)
        addPDFTableCell(table, "Humidity", false)
        addPDFTableCell(table, "${(reportData.environmentParameters.humidity * 100).toInt()}%", false)

        document.add(table)
        document.add(Paragraph(""))
    }

    private fun createPDFSimulationResults(
        document: Document,
        reportData: ReportData,
        headingFont: com.itextpdf.kernel.font.PdfFont,
        normalFont: com.itextpdf.kernel.font.PdfFont
    ) {
        document.add(Paragraph("4. SIMULATION RESULTS")
            .setFont(headingFont)
            .setFontSize(16f)
            .setBold()
            .setFontColor(ColorConstants.BLUE)
            .setMarginTop(20f)
            .setMarginBottom(10f))

        val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 60f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        addPDFTableCell(table, "Parameter", true)
        addPDFTableCell(table, "Value", true)
        addPDFTableCell(table, "Initial Velocity", false)
        addPDFTableCell(table, "${reportData.simulationResults.initialVelocity} m/s", false)
        addPDFTableCell(table, "Launch Angle", false)
        addPDFTableCell(table, "${reportData.simulationResults.launchAngle}°", false)
        addPDFTableCell(table, "Initial Height", false)
        addPDFTableCell(table, "${reportData.simulationResults.initialHeight} m", false)
        addPDFTableCell(table, "Maximum Distance", false)
        addPDFTableCell(table, "${reportData.simulationResults.maxDistance.toInt()} m", false)
        addPDFTableCell(table, "Maximum Height", false)
        addPDFTableCell(table, "${reportData.simulationResults.maxHeight.toInt()} m", false)
        addPDFTableCell(table, "Time of Flight", false)
        addPDFTableCell(table, "${reportData.simulationResults.timeOfFlight.toInt()} s", false)
        addPDFTableCell(table, "Impact Velocity", false)
        addPDFTableCell(table, "${reportData.simulationResults.impactVelocity.toInt()} m/s", false)
        addPDFTableCell(table, "Impact Angle", false)
        addPDFTableCell(table, "${reportData.simulationResults.impactAngle.toInt()}°", false)
        addPDFTableCell(table, "Maximum Speed", false)
        addPDFTableCell(table, "${reportData.simulationResults.maxSpeed.toInt()} m/s", false)
        addPDFTableCell(table, "Total Energy", false)
        addPDFTableCell(table, String.format("%.2f", reportData.simulationResults.totalEnergy) + " J", false)

        document.add(table)
        document.add(Paragraph(""))
    }

    private fun createPDFTrajectoryTable(
        document: Document,
        reportData: ReportData,
        headingFont: com.itextpdf.kernel.font.PdfFont,
        normalFont: com.itextpdf.kernel.font.PdfFont
    ) {
        document.add(Paragraph("5. TRAJECTORY DATA")
            .setFont(headingFont)
            .setFontSize(16f)
            .setBold()
            .setFontColor(ColorConstants.BLUE)
            .setMarginTop(20f)
            .setMarginBottom(10f))

        val pointsToShow = reportData.trajectoryPoints.take(50)

        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 15f, 15f, 20f, 15f, 20f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        // Headers
        val headers = arrayOf("Time (s)", "Distance (m)", "Height (m)", "Velocity (m/s)", "Angle (°)", "Energy (J)")
        headers.forEach { header ->
            val cell = Cell().add(Paragraph(header))
                .setBackgroundColor(ColorConstants.BLUE)
                .setFontColor(ColorConstants.WHITE)
                .setBold()
                .setFontSize(10f)
            table.addCell(cell)
        }

        // Data rows
        pointsToShow.forEach { point ->
            addPDFDataCell(table, String.format("%.2f", point.time))
            addPDFDataCell(table, String.format("%.1f", point.x))
            addPDFDataCell(table, String.format("%.1f", point.y))
            addPDFDataCell(table, String.format("%.1f", point.speed))
            addPDFDataCell(table, String.format("%.1f", point.angle))
            addPDFDataCell(table, String.format("%.1f", point.kineticEnergy + point.potentialEnergy))
        }

        document.add(table)

        if (reportData.trajectoryPoints.size > 50) {
            document.add(Paragraph("Note: Showing first 50 of ${reportData.trajectoryPoints.size} data points for readability.")
                .setFontSize(10f)
                .setFontColor(ColorConstants.GRAY)
                .setMarginTop(10f))
        }
    }

    private fun addPDFTableCell(table: Table, text: String, isHeader: Boolean) {
        val cell = Cell().add(Paragraph(text))
        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.BLUE)
                .setFontColor(ColorConstants.WHITE)
                .setBold()
        }
        table.addCell(cell)
    }

    private fun addPDFDataCell(table: Table, text: String) {
        val cell = Cell().add(Paragraph(text))
        table.addCell(cell)
    }
}