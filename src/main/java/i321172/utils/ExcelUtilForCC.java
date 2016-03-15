package i321172.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import i321172.bean.CoverageBean;

@Service
public class ExcelUtilForCC
{
    public HSSFWorkbook createWorkbook(List<CoverageBean> packageList, String[] headers, List<CoverageBean> classList)
    {
        HSSFWorkbook wb = ExcelUtils.createWorkBook();
        HSSFSheet pack = wb.createSheet("Package");
        HSSFSheet cla = wb.createSheet("Class");
        fillSheet(pack, headers, packageList);
        fillSheet(cla, headers, classList);
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
    public HSSFWorkbook createWorkbook(List<CoverageBean> packageList, List<CoverageBean> classList)
    {
        String[] headers = { "Name", "New Total Coverage", "Old Total Coverage", "Coverage Diff", "New Total Line",
                "Old Total Line", "Total Line Diff", "New Total Lines Executed", "Old Total Lines Executed",
                "To Be Covered", "Full Name" };
        return createWorkbook(packageList, headers, classList);
    }

    private void fillSheet(HSSFSheet sheet, String[] headers, List<CoverageBean> body)
    {
        ExcelUtils.addHeader(sheet, headers);
        addBody(sheet, body);
    }

    private String[] convertToArray(CoverageBean coverage)
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

    private void addBody(HSSFSheet s, List<CoverageBean> lists)
    {
        List<String[]> body = new ArrayList<String[]>();
        for (CoverageBean cov : lists)
        {
            body.add(convertToArray(cov));
        }
        ExcelUtils.addBody(s, body);
    }

}
