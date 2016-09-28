package i321172.web;

import i321172.core.CacheData;
import i321172.dao.CoverageDao;
import i321172.service.http.HttpClient;
import i321172.utils.ExcelUtilForCC;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CoverageController
{
    private Logger         logger = Logger.getLogger(getClass());
    @Resource
    private HttpClient     httpUtil;
    @Resource
    private CoverageDao    coverageDao;
    @Resource
    private CacheData      cache;
    @Resource
    private ExcelUtilForCC ccExcel;

    @RequestMapping(value = "/period")
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

    @RequestMapping(value = "/period/download")
    public void downloadFile(@RequestParam(value = "feature") String feature,
            @RequestParam(value = "showType", defaultValue = "Class") String featchAll, HttpServletResponse response)
    {
        response.addHeader("content-disposition", "attachment;filename=Code-Coverage-" + feature + ".xls");
        HSSFWorkbook book = ccExcel.createWorkbook(null, null);
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

}
