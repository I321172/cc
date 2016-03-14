package i321172.web;

import i321172.bean.FeatureCoverage;
import i321172.utils.DBUtil;
import i321172.utils.HttpClientUtil;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
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

    @RequestMapping(value = "/show/cc")
    public String showCodeCoverage(@RequestParam(value = "feature", defaultValue = "RBP") String feature,
            @RequestParam(value = "only", defaultValue = "Package") String onlyPackage, Model model)
    {
        model.addAttribute("features", cache.getAllFeatures());
        boolean isClass = onlyPackage.equals("Class");
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

    @RequestMapping(value = "/show/o")
    public String showClassInfo(@RequestParam(value = "feature", defaultValue = "RBP") String feature,
            @RequestParam(value = "only", defaultValue = "Package") String onlyPackage, Model model)
    {
        FeatureCoverage featureCov;
        model.addAttribute("features", cache.getAllFeatures());
        if (onlyPackage.equals("Package"))
        {
            featureCov = getFeature(feature, false);
            model.addAttribute("list", featureCov.getOnlyPackagelist());
            model.addAttribute("urlPrefix", getCoverageFilePrefix());
        } else
        {
            featureCov = getFeature(feature, true);
            model.addAttribute("list", featureCov.getList());
            model.addAttribute("urlPrefix", getCoverageFilePrefix());
        }
        return "showcoverage";
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
