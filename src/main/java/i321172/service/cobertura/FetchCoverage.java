package i321172.service.cobertura;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import i321172.bean.cobertura.ClassBean;
import i321172.bean.cobertura.CoberturaBean;
import i321172.bean.cobertura.PackageBean;
import i321172.dao.CoverageDao;
import i321172.service.http.HttpClient;
import i321172.service.http.HttpClientBean;
import i321172.utils.JsonUtil;
import i321172.utils.StringUtil;

@Service
public class FetchCoverage
{
    @Resource
    private HttpClient    httpUtil;
    @Resource
    private CoberturaBean coberBean;
    @Resource
    private CoverageDao   coverageDao;
    private Logger        logger = Logger.getLogger(FetchCoverage.class);

    public List<PackageBean> fetchPackageData(String period, String moduleId, String moduleName) throws Exception
    {
        String[][] csv = getCSVFile(period, moduleId, moduleName, true);
        int row = csv.length;
        List<PackageBean> result = new ArrayList<PackageBean>(row - 1);
        // skip the first row
        for (int i = 1; i < row; i++)
        {
            PackageBean cur = new PackageBean();

            cur.setPackageName(csv[i][3]);
            cur.setTotalCoverage(Float.parseFloat(csv[i][4]));
            cur.setLineCoverage(Float.parseFloat(csv[i][5]));
            cur.setBranchCoverage(Float.parseFloat(csv[i][6]));
            cur.setClassNum(parseInt(csv[i][7]));
            cur.setMethodNum(parseInt(csv[i][8]));
            int totalLines = parseInt(csv[i][9]);
            cur.setLines(totalLines);
            int coveredLines = parseInt(csv[i][10]);
            cur.setCoverLines(coveredLines);
            cur.setBranches(parseInt(csv[i][11]));
            cur.setCoverBranches(parseInt(csv[i][12]));
            cur.setToBeCoveredLines(totalLines - coveredLines);
            result.add(cur);
        }
        logger.info("Fetch Package Data, Size = " + result.size());
        return result;
    }

    private int parseInt(String text)
    {
        int pos = text.indexOf(".");
        if (pos < 0)
        {
            return Integer.parseInt(text);
        } else
        {
            return Integer.parseInt(text.substring(0, pos));
        }
    }

    public List<ClassBean> fetchClassData(String period, String moduleId, String moduleName) throws Exception
    {
        String[][] csv = getCSVFile(period, moduleId, moduleName, false);
        int row = csv.length;
        List<ClassBean> result = new ArrayList<ClassBean>(row - 1);
        // skip the first row
        for (int i = 1; i < row; i++)
        {
            ClassBean cur = new ClassBean();

            cur.setClassName(csv[i][1]);
            cur.setTotalCoverage(Float.parseFloat(csv[i][2]));
            int totalLines = parseInt(csv[i][3]);
            cur.setLines(totalLines);
            int coveredLines = parseInt(csv[i][4]);
            cur.setCoverLines(coveredLines);
            cur.setToBeCoveredLines(totalLines - coveredLines);
            cur.setBranches(parseInt(csv[i][6]));
            cur.setCoverBranches(parseInt(csv[i][7]));
            cur.setToBeCoveredBranches(parseInt(csv[i][8]));
            cur.setFileName(csv[i][9]);
            cur.setLineRate(Float.parseFloat(csv[i][10]));
            cur.setBranchRate(Float.parseFloat(csv[i][11]));
            cur.setComplexity(parseInt(csv[i][12]));
            result.add(cur);
        }
        logger.info("Fetch Class Data, Size = " + result.size());

        return result;
    }

    public InputStream fetch(String url, boolean needProxy) throws IOException
    {
        InputStream is = httpUtil.getResponse(new HttpClientBean.Builder(url).setProxy(false).build());
        return is;
    }

    public String getRunId(String period) throws Exception
    {
        String url = coberBean.getRunIdUrl(period);
        String resp = httpUtil.fetchWebResponse(url, true);
        return JsonUtil.parseNode(resp).get(0).get("runid").asText();
    }

    private String[][] getCSVFile(String period, String moduleId, String moduleName, boolean isPackage) throws Exception
    {
        String runId = getRunId(period);
        String url = isPackage ? coberBean.getPackageDownloadUrl(moduleId, runId, moduleName)
                : coberBean.getClassDownloadUrl(moduleId, runId, moduleName);
        String response = IOUtils.toString(fetch(url, false));
        String[][] respArray = StringUtil.parseToArray(response);
        return respArray;
    }

    public int importPackageData(String period, String moduleId, String moduleName) throws Exception
    {
        return coverageDao.importPackage(period, fetchPackageData(period, moduleId, moduleName));
    }

    public int importClassData(String period, String moduleId, String moduleName) throws Exception
    {
        return coverageDao.importClass(period, fetchClassData(period, moduleId, moduleName));
    }

}
