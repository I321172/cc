package i321172.web;

import i321172.bean.CoverageBean;
import i321172.bean.FeatureCoverage;
import i321172.utils.DBUtil;
import i321172.utils.HttpClientUtil;
import i321172.web.aop.log.LogAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RequestController
{
    private Logger logger = Logger.getLogger(getClass());

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String showClassInfo(@RequestParam(value = "feature", defaultValue = "RBP") String feature,
            @RequestParam(value = "only", defaultValue = "Package") String onlyPackage, Model model)
    {
        FeatureCoverage featureCov;
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

    @RequestMapping(value = "/show/aep")
    public String fetchAEPRunningJobs(Model model)
    {
        return "aeprun";
    }

    @RequestMapping(value = "/test")
    public String test(Model model)
    {
        model.addAttribute("list", getTest());
        model.addAttribute("urlPrefix", getCoverageFilePrefix());
        return "showcoverage";
    }

    @RequestMapping(value = "/refresh/conn")
    public String refreshConnect(Model model) throws Exception
    {
        DBUtil util = MyApplicationContext.context.getBean("dbUtil", DBUtil.class);
        util.releaseConnectionPool();
        model.addAttribute("result", "Connection All Closed! Connection Pool Refreshed!");
        return "result";
    }

    @RequestMapping(value = "/show/status")
    public String showRequestStatus(Model model)
    {
        LogAdvice advice = MyApplicationContext.context.getBean("logAdvice", LogAdvice.class);
        String msg = "SVN Fix Status Visit Count: " + advice.getSvnCount() + "; Code Covereage Visit Count: "
                + advice.getCoverageCount();
        model.addAttribute("result", msg);
        return "result";
    }

    @RequestMapping(value = "/show/compile")
    public String fetchCompileError(@RequestParam(value = "url") String url, Model model) throws Exception
    {
        HttpClientUtil httpClient = MyApplicationContext.context.getBean(HttpClientUtil.class);
        String response = httpClient.fetchWeb(url, false);
        String result = null;
        String success = "BUILD SUCCESSFUL";
        if (response.contains(success))
        {
            result = success + " on " + url;
        } else
        {
            result = fetchCompileError(response);
        }
        model.addAttribute("utext", result);
        return "result";
    }

    private String getCoverageFilePrefix()
    {
        CacheData cache = MyApplicationContext.context.getBean("cacheData", CacheData.class);
        return cache.getCoverageFilePrefix();
    }

    private FeatureCoverage getFeature(String feature, boolean fetchAll)
    {
        CacheData cache = MyApplicationContext.context.getBean("cacheData", CacheData.class);
        DBUtil util = MyApplicationContext.context.getBean("dbUtil", DBUtil.class);
        FeatureCoverage featureData = cache.getFeatureData(feature);
        if (featureData == null)
        {
            if (fetchAll)
            {
                log("Fetch class and package data for Feature:" + feature);
                featureData = util.queryClass(feature);
            } else
            {
                log("Fetch only package data for Feature:" + feature);
                featureData = util.queryPackage(feature);
            }
            cache.putFeatureData(feature, featureData);
        } else
        {
            log("Fetch cached Feature:" + feature);
            if (fetchAll && featureData.getList().isEmpty())
            {
                log("Fetch class data for Feature:" + feature);
                featureData.setList(util.queryClassList(feature));
            }
        }
        return featureData;
    }

    private List<CoverageBean> getTest()
    {
        List<CoverageBean> list = new ArrayList<CoverageBean>();
        for (int i = 0; i < 3; i++)
        {
            CoverageBean bean = new CoverageBean();
            bean.setClassName("classname" + i);
            bean.setNewTotalCoverage("50" + i);
            bean.setNewTotalLines("60" + i);
            list.add(bean);
        }
        return list;
    }

    private String fetchCompileError(String response)
    {
        String regex = "\\[javac\\].*";
        String code = "uitests.webdriver";
        boolean isContinue = false;
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(response);
        StringBuffer str = new StringBuffer();
        String line = null;
        while (mat.find())
        {
            line = mat.group();
            if (line.contains(code))
            {
                if (isError(line))
                {
                    str.append(line + "\n");
                    isContinue = true;
                } else
                {
                    isContinue = false;
                }
            } else if (isContinue)
            {
                str.append(line + "\n");
            }
        }
        String result = str.toString();
        if (result == null || result.length() == 0)
        {
            result = "Cannot fetch the compile error; See response for details:\n" + response;
        }
        result = "<pre>" + result + "</pre>";
        return result;
    }

    private boolean isError(String line)
    {
        String[] signs = { "error:" };
        for (String sign : signs)
        {
            if (line.contains(sign))
            {
                return true;
            }
        }
        return false;
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

}