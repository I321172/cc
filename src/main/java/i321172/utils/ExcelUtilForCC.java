package i321172.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Service;

import i321172.bean.CoverageCompareBean;

@Service
public class ExcelUtilForCC
{
    public HSSFWorkbook createWorkbook(List<CoverageCompareBean> packageList, String[] headers, List<CoverageCompareBean> classList)
    {
        HSSFWorkbook wb = ExcelUtils.createWorkBook();

        HSSFSheet pack = wb.createSheet("Package");
        fillSheet(pack, headers, packageList);
        if (classList != null && !classList.isEmpty())
        {
            HSSFSheet cla = wb.createSheet("Class");
            fillSheet(cla, headers, classList);
        }

        handleExcelStyle(wb);
        return wb;
    }

    /**
     * Default Header:"Name", "New Total Coverage", "Old Total Coverage",
     * "Coverage Diff", "New Total Line", "Old Total Line", "Total Line Diff",
     * "New Total Lines Executed", "Old Total Lines Executed", "To Be Covered",
     * "Full Name"
     * 
     * @param packageList
     * @param classList
     * @return
     */
    public HSSFWorkbook createWorkbook(List<CoverageCompareBean> packageList, List<CoverageCompareBean> classList)
    {
        String[] headers = { "Name", "New Total Coverage", "Old Total Coverage", "Coverage Diff", "New Total Line",
                "Old Total Line", "Total Line Diff", "New Total Lines Executed", "Old Total Lines Executed",
                "To Be Covered", "Full Name" };
        return createWorkbook(packageList, headers, classList);
    }

    private void fillSheet(HSSFSheet sheet, String[] headers, List<CoverageCompareBean> body)
    {
        ExcelUtils.fillSheet(sheet, headers, convertBody(body));
    }

    private HSSFCellStyle getHeaderStyle(HSSFWorkbook wb)
    {
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(HSSFColor.GREEN.index);
        style.setWrapText(true);
        HSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.WHITE.index);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        return style;
    }

    private String[] convertToArray(CoverageCompareBean coverage)
    {
        String result[] = new String[11];
        result[0] = coverage.getName();
        result[1] = coverage.getNewTotalCoverage();
        result[2] = coverage.getOldTotalCoverage();
        result[3] = coverage.getCoverageDiffer();
        result[4] = coverage.getNewTotalLines();
        result[5] = coverage.getOldTotalLines();
        result[6] = coverage.getTotallinesdiffer();
        result[7] = coverage.getNewTotalLinesExecuted();
        result[8] = coverage.getOldTotalLinesExecuted();
        result[9] = coverage.getToBeCovered();
        result[10] = coverage.getClassName();
        return result;
    }

    private void handleExcelStyle(HSSFWorkbook wb)
    {
        HSSFCellStyle style = this.getHeaderStyle(wb);
        HSSFSheet pack = wb.getSheet("Package");
        HSSFSheet cla = wb.getSheet("Class");
        handleSheetStyle(pack, style);
        if (cla != null)
            handleSheetStyle(cla, style);
    }

    private void handleSheetStyle(HSSFSheet sheet, HSSFCellStyle style)
    {
        ExcelUtils.fillHeaderStyle(sheet, style);
        // set column width
        Map<Integer, Integer> lengths = new HashMap<Integer, Integer>();
        int col = 0;
        // set row widths
        for (int i = 0; i < 11; i++)
        {
            col = i == 0 ? 256 * 65 : 256 * 18;
            lengths.put(i, col);
        }
        ExcelUtils.setColumnWidth(sheet, lengths);

        // set column height
        lengths.clear();
        lengths.put(0, 39);
        ExcelUtils.setRowHeight(sheet, lengths);
    }

    private List<String[]> convertBody(List<CoverageCompareBean> lists)
    {
        List<String[]> body = new ArrayList<String[]>();
        for (CoverageCompareBean cov : lists)
        {
            body.add(convertToArray(cov));
        }
        return body;
    }

    // public static void main(String args[]) throws Exception
    // {
    // ExcelUtilForCC excel = new ExcelUtilForCC();
    // HSSFWorkbook wb = excel.createWorkbook(new ArrayList<CoverageBean>(), new
    // ArrayList<CoverageBean>());
    // ExcelUtils.createExcelFile(wb, "jasonexcel.xls");
    // }

}
