package i321172.web;

import i321172.bean.CoverageCompareBean;
import i321172.core.CacheData;
import i321172.dao.CoverageDao;
import i321172.utils.ExcelUtilForCC;
import i321172.utils.HttpClientUtil;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CoverageCompareController
{
    private Logger         logger = Logger.getLogger(getClass());
    @Resource
    private HttpClientUtil httpUtil;
    @Resource
    private CoverageDao    coverageDao;
    @Resource
    private CacheData      cache;
    @Resource
    private ExcelUtilForCC ccExcel;

    @RequestMapping(value = "/show")
    public String showCodeCoverage(@RequestParam(defaultValue = "RBP") String feature,
            @RequestParam(value = "only", defaultValue = "Package") String only, Model model)
    {
        boolean onlyPackage = "package".equalsIgnoreCase(only);
        model.addAttribute("features", coverageDao.getAllFeature());
        model.addAttribute("currentFeature", feature);
        model.addAttribute("package", onlyPackage);
        model.addAttribute("showType", only);
        model.addAttribute("list", coverageDao.queryList(feature, onlyPackage));
        model.addAttribute("urlPrefix", getCoverageFilePrefix());
        return "Coverage Compare";
    }

    @RequestMapping(value = "/cover")
    public String welcome()
    {
        return "Coverage";
    }

    @RequestMapping(value = "/date")
    public String setCCRunDate(@RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "site", required = false) String site, Model model) throws Exception
    {
        cache.setUrlPrefix(site);
        cache.setCurrentTime(date);
        model.addAttribute("result", "OK");
        return "result";
    }

    @RequestMapping(value = "/download")
    public void downloadFile(@RequestParam(value = "feature") String feature,
            @RequestParam(value = "showType", defaultValue = "Class") String featchAll, HttpServletResponse response)
    {
        response.addHeader("content-disposition", "attachment;filename=Code-Coverage-" + feature + ".xls");
        boolean onlyPackage = "package".equalsIgnoreCase(featchAll);
        List<CoverageCompareBean> ret = coverageDao.queryList(feature, onlyPackage);
        List<CoverageCompareBean> pack = onlyPackage ? ret : getPackagelistFromClassList(ret);
        HSSFWorkbook book = ccExcel.createWorkbook(pack, onlyPackage ? null : ret);
        try
        {
            book.write(response.getOutputStream());
            book.close();
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

    public List<CoverageCompareBean> getPackagelistFromClassList(List<CoverageCompareBean> classList)
    {
        List<CoverageCompareBean> packageList = new ArrayList<CoverageCompareBean>();
        for (CoverageCompareBean covBean : classList)
        {
            if (covBean.isPackag())
            {
                packageList.add(covBean);
            }
        }
        return packageList;
    }
}
