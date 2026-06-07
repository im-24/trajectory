import androidx.compose.runtime.Composable
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream



    fun saveexcel(
        xValues: List<Float>,
        yValues: List<Float>
    ){
        if (xValues.isEmpty()) return

        val vlues = mutableListOf<Pair<Float , Float>>()

        for (i in xValues.indices){
            vlues.add(Pair(xValues[i], yValues[i]))
        }

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet()

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("X")
        header.createCell(1).setCellValue("Y")

        vlues.forEachIndexed { index, pair ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(pair.first.toString())
            row.createCell(1).setCellValue(pair.second.toString())
        }

        val fileOut = FileOutputStream("trajectory.xlsx")
        workbook.write(fileOut)
        fileOut.close()
        workbook.close()
    }

    fun exportWord(
        Velocity : Float,
        angle : Float,
        xValues : Float,
        yValues : Float,
    ) {
        val document = XWPFDocument()

        val paragraph = document.createParagraph()
        val run = paragraph.createRun()

        run.setText("Projectile Information")
        run.addBreak()
        run.setText("Velocity: $Velocity m/s")
        run.addBreak()
        run.setText("Angle: ${angle}°")
        run.addBreak()
        run.setText("Final Position: ($xValues, $yValues)")

        val out = FileOutputStream("report.docx")
        document.write(out)
        out.close()
        document.close()
    }



