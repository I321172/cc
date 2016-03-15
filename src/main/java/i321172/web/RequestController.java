package i321172.web;

import i321172.bean.FeatureCoverage;
import i321172.utils.DBUtil;
import i321172.utils.ExcelUtilForCC;
import i321172.utils.HttpClientUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RequestController
{
    private Logger         logger = Logger.getLogger(getClass());
    @Resource
    private HttpClientUtil httpUtil;
    @Resource
    private DBUtil         dbUtil;
    @Resource
    private CacheData      cache;
    @Resource
    private ExcelUtilForCC ccExcel;

    @RequestMapping(value = "/show/cc")
    public String showCodeCoverage(@RequestParam(value = "feature", defaultValue = "RBP") String feature,
            @RequestParam(value = "only", defaultValue = "Package") String onlyPackage, Model model)
    {
        boolean isClass = onlyPackage.equals("Class");
        model.addAttribute("features", cache.getAllFeatures());
        model.addAttribute("currentFeature", feature);
        model.addAttribute("package", !isClass);
        FeatureCoverage featureCov = getFeature(feature, isClass);
        model.addAttribute("list", featureCov.getList(isClass));
        model.addAttribute("urlPrefix", getCoverageFilePrefix());
        return "Coverage";
    }

    @RequestMapping(value = "/refresh/conn")
    public String refreshConnect(Model model) throws Exception
    {
        dbUtil.releaseConnectionPool();
        model.addAttribute("result", "Connection All Closed! Connection Pool Refreshed!");
        return "result";
    }

    @RequestMapping(value = "/cc/date")
    public String setCCRunDate(@RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "site", required = false) String site, Model model) throws Exception
    {
        cache.setUrlPrefix(site);
        cache.setCurrentTime(date);
        model.addAttribute("result", "OK");
        return "result";
    }

    @RequestMapping(value = "/cc/download")
    public void downloadFile(@RequestParam(value = "feature") String feature, HttpServletResponse response)
    {
        response.addHeader("content-disposition", "attachment;filename=Code-Coverage-" + feature + ".xls");
        FeatureCoverage featureCov = getFeature(feature, true);
        HSSFWorkbook book = ccExcel.createWorkbook(featureCov.getList(false), featureCov.getList());
        try
        {
            book.write(response.getOutputStream());
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            this.logger.error("Write to Response outputstream error!", e);
        }
    }

    private String getCoverageFilePrefix()
    {
        return cache.getCoverageFilePrefix();
    }

    private FeatureCoverage getFeature(String feature, boolean fetchAll)
    {
        FeatureCoverage featureData = cache.getFeatureData(feature);
        if (featureData == null)
        {
            if (fetchAll)
            {
                log("Fetch class and package data for Feature:" + feature);
                featureData = dbUtil.queryClass(feature);
            } else
            {
                log("Fetch only package data for Feature:" + feature);
                featureData = dbUtil.queryPackage(feature);
            }
            cache.putFeatureData(feature, featureData);
        } else
        {
            log("Fetch cached Feature:" + feature);
            if (fetchAll && featureData.getList().isEmpty())
            {
                log("Fetch class data for Feature:" + feature);
                featureData.setList(dbUtil.queryClassList(feature));
            }
        }
        return featureData;
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

}
