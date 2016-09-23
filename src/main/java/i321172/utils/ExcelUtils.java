package i321172.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtils
{
    private final static String PS           = File.separator;
    private final static String XML_LOCATION = "xml" + PS;
    private static Logger       logger       = Logger.getLogger(ExcelUtils.class);

    /**
     * creates an {@link HSSFWorkbook} the specified OS filename.
     */
    public static HSSFWorkbook readFile(String filename) throws IOException
    {
        return new HSSFWorkbook(new FileInputStream(filename));
    }

    public static HSSFWorkbook toBook(InputStream is) throws IOException
    {
        return new HSSFWorkbook(is);
    }

    private static void log(String msg)
    {
        logger.info(msg);
    }

    public static Map<String, List<String>> readFileAsMap(String fileName, String sheetName) throws IOException
    {
        HSSFWorkbook workbook = readFile(fileName);
        HSSFSheet sheet = workbook.getSheet(sheetName);
        Map<String, List<String>> columnValueMap = new LinkedHashMap<String, List<String>>();
        List<String> header = new ArrayList<String>();
        int maxCol = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum();
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++)
        {
            HSSFRow currentRow = sheet.getRow(i);
            String colValue = "";
            for (int j = currentRow.getFirstCellNum(); j < maxCol; j++)
            {
                Cell currentCell = currentRow.getCell(j);
                if (currentCell == null)
                {
                    colValue = "";
                } else
                {
                    colValue = getCellValue(currentCell);
                }
                if (i == sheet.getFirstRowNum())
                {
                    List<String> columnValues = new ArrayList<String>();
                    columnValueMap.put(colValue, columnValues);
                    header.add(colValue);
                } else
                {
                    if (j < header.size())
                    {
                        columnValueMap.get(header.get(j)).add(colValue);
                    } else
                    {
                        log("Header column count is " + header.size() + "! But row " + i + " column:" + j
                                + " still has value:" + colValue);
                    }
                }
            }
        }
        return columnValueMap;
    }

    public static HSSFWorkbook createWorkBook()
    {
        return new HSSFWorkbook();
    }

    public static void fillSheet(HSSFSheet sheet, String[] headers, List<String[]> lists)
    {
        addHeader(sheet, headers);
        addBody(sheet, lists);
    }

    public static void fillHeaderStyle(HSSFSheet sheet, HSSFCellStyle style)
    {
        for (Iterator<Cell> it = sheet.getRow(0).cellIterator(); it.hasNext();)
        {
            it.next().setCellStyle(style);
        }
    }

    public static void setColumnWidth(HSSFSheet sheet, Map<Integer, Integer> columnWidths)
    {
        for (int col : columnWidths.keySet())
        {
            sheet.setColumnWidth(col, columnWidths.get(col));
        }
    }

    public static void setRowHeight(HSSFSheet sheet, Map<Integer, Integer> rowHeights, int defaultHeight)
    {
        int index = 0;
        int height = 0;
        for (Iterator<Row> it = sheet.rowIterator(); it.hasNext();)
        {
            Row cur = it.next();
            index = cur.getRowNum();
            height = rowHeights.containsKey(index) ? rowHeights.get(index) : defaultHeight;
            cur.setHeightInPoints(height);
        }
    }

    public static void setRowHeight(HSSFSheet sheet, Map<Integer, Integer> rowHeights)
    {
        setRowHeight(sheet, rowHeights, -1);
    }

    public static void setRowHeight(HSSFSheet sheet, int defaultHeight)
    {
        setRowHeight(sheet, new HashMap<Integer, Integer>(), defaultHeight);
    }

    /**
     * @param sheet
     * @param columnWidths
     *            each one must be int,int
     */
    public static void setColumnWidth(HSSFSheet sheet, String... columnWidths)
    {
        Map<Integer, Integer> widths = new HashMap<Integer, Integer>();
        for (String col : columnWidths)
        {
            String[] splits = col.split(",");
            widths.put(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));
        }

        // here is actually to do
        setColumnWidth(sheet, widths);
    }

    public static void createExcelFile(Map<String, List<String>> modifiedMap, String fileName) throws IOException
    {
        if (modifiedMap == null || modifiedMap.keySet().size() == 0)
        {
            log("Skip create Excel file since map is null or empty");
            return;
        }
        long start = System.currentTimeMillis();
        log("Start to create Excel from map!");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet s = wb.createSheet();
        // the first row is key; follow with list size, so total size should
        // plus 1
        int rowCount = 1 + modifiedMap.get(modifiedMap.keySet().iterator().next()).size();
        int row = 0;
        HSSFRow currentRow;
        HSSFCell currentCol;
        String value;
        while (row < rowCount)
        {
            // create one new row from 0;
            currentRow = s.createRow(row);
            int col = 0;
            for (String key : modifiedMap.keySet())
            {
                currentCol = currentRow.createCell(col);
                if (row == 0)
                {
                    value = key;
                } else
                {
                    value = modifiedMap.get(key).get(row - 1);
                }
                if (value == null)
                {
                    value = "";
                    log("Null value when row:" + row + " and column:" + col);
                }
                if (value.startsWith("="))
                {
                    currentCol.setCellFormula(value.substring(1));
                } else
                {
                    currentCol.setCellValue(value);
                }
                col++;
            }
            row++;
        }

        // end deleted sheet
        String filePath = getDefaultFilePath() + fileName;
        FileOutputStream out = new FileOutputStream(filePath);
        wb.write(out);
        out.close();
        wb.close();
        log("Create Excel takes " + getDuration(start) + " at " + filePath);
    }

    public static void createExcelFile(HSSFWorkbook wb, String filename) throws Exception
    {
        FileOutputStream out = new FileOutputStream(filename);
        wb.write(out);
        out.close();
        wb.close();
        log("Create Excel at " + filename);
    }

    public static void addHeader(HSSFSheet s, String[] headers)
    {
        HSSFRow currentRow = s.createRow(0);
        addRow(currentRow, headers);
    }

    public static void addRow(HSSFRow row, String[] texts)
    {
        int col = 0;
        for (String key : texts)
        {
            HSSFCell currentCol = row.createCell(col);
            currentCol.setCellValue(key);
            col++;
        }
    }

    public static void addBody(HSSFSheet s, List<String[]> lists)
    {
        int startRow = s.getLastRowNum() + 1;
        for (String[] cur : lists)
        {
            HSSFRow currentRow = s.createRow(startRow++);
            addRow(currentRow, cur);
        }
    }

    /**
     * Append = if cell is formula
     * 
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell)
    {
        String colValue = "";
        if (cell.getCellType() == Cell.CELL_TYPE_STRING)
        {
            colValue = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
        {
            colValue = "=" + cell.getCellFormula();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
        {
            colValue = String.valueOf(cell.getNumericCellValue());
        }
        return colValue;
    }

    /**
     * given a filename this outputs a sample sheet with just a set of
     * rows/cells.
     */
    public static void testCreateSampleSheet(String outputFilename) throws IOException
    {
        int rownum;
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet s = wb.createSheet();
        HSSFCellStyle cs = wb.createCellStyle();
        HSSFCellStyle cs2 = wb.createCellStyle();
        HSSFCellStyle cs3 = wb.createCellStyle();
        HSSFFont f = wb.createFont();
        HSSFFont f2 = wb.createFont();

        f.setFontHeightInPoints((short) 12);
        f.setColor((short) 0xA);
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        f2.setFontHeightInPoints((short) 10);
        f2.setColor((short) 0xf);
        f2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cs.setFont(f);
        cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
        cs2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cs2.setFillPattern((short) 1); // fill w fg
        cs2.setFillForegroundColor((short) 0xA);
        cs2.setFont(f2);
        wb.setSheetName(0, "HSSF Test");
        for (rownum = 0; rownum < 300; rownum++)
        {
            HSSFRow r = s.createRow(rownum);
            if ((rownum % 2) == 0)
            {
                r.setHeight((short) 0x249);
            }

            for (int cellnum = 0; cellnum < 50; cellnum += 2)
            {
                HSSFCell c = r.createCell(cellnum);
                c.setCellValue(rownum * 10000 + cellnum + (((double) rownum / 1000) + ((double) cellnum / 10000)));
                if ((rownum % 2) == 0)
                {
                    c.setCellStyle(cs);
                }
                c = r.createCell(cellnum + 1);
                c.setCellValue(new HSSFRichTextString("TEST"));
                // 50 characters divided by 1/20th of a point
                s.setColumnWidth(cellnum + 1, (int) (50 * 8 / 0.05));
                if ((rownum % 2) == 0)
                {
                    c.setCellStyle(cs2);
                }
            }
        }

        // draw a thick black border on the row at the bottom using BLANKS
        rownum++;
        rownum++;
        HSSFRow r = s.createRow(rownum);
        cs3.setBorderBottom(HSSFCellStyle.BORDER_THICK);
        for (int cellnum = 0; cellnum < 50; cellnum++)
        {
            HSSFCell c = r.createCell(cellnum);
            c.setCellStyle(cs3);
        }
        s.addMergedRegion(new CellRangeAddress(0, 3, 0, 3));
        s.addMergedRegion(new CellRangeAddress(100, 110, 100, 110));

        // end draw thick black border
        // create a sheet, set its title then delete it
        s = wb.createSheet();
        wb.setSheetName(1, "DeletedSheet");
        wb.removeSheetAt(1);

        // end deleted sheet
        FileOutputStream out = new FileOutputStream(outputFilename);
        wb.write(out);
        out.close();
        wb.close();
    }

    private static String getDefaultFilePath()
    {
        String userdir = System.getProperty("user.dir");
        String path = userdir.substring(0, userdir.indexOf(PS)) + PS + XML_LOCATION;
        return path;
    }

    private static String getDuration(long start)
    {
        long seconds = (System.currentTimeMillis() - start) / 1000;
        return seconds / 60 + " minutes and " + seconds % 60 + " seconds.";
    }

}
