package i321172.web.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import i321172.bean.CoverageCompareBean;
import i321172.bean.ParamsBean;
import i321172.bean.cobertura.PackageBean;
import i321172.bean.cobertura.PackageCompareBean;
import i321172.dao.CoverageDao;
import i321172.service.coverage.CoverageService;
import i321172.utils.TimeDuration;

@RestController
public class CoverageRestController
{
    @Resource
    private CoverageDao     coverageDao;
    @Resource
    private CoverageService service;
    @Resource
    private ParamsBean      paramBean;
    private Logger          logger = Logger.getLogger(CoverageRestController.class);

    @RequestMapping(value = "/api/compare")
    public List<CoverageCompareBean> getFeature(@RequestParam String feature,
            @RequestParam(required = false) String fetchType, @RequestParam int startRow, @RequestParam int endRow)
    {
        Date start = new Date();
        boolean onlyPackage = "package".equalsIgnoreCase(fetchType);
        paramBean.setTotalCount(paramBean.getTotalCount() + 1);

        List<CoverageCompareBean> result = coverageDao.queryDiffInRange(feature, onlyPackage, startRow, endRow);
        Date end = new Date();
        logger.info("Duration: " + TimeDuration.getDiff(start, end, "s"));
        return result;
    }

    @RequestMapping(value = "/api/query/coverage")
    public String getTotalCoverage(@RequestParam String period)
    {
        return service.getTotalCoverage(period);
    }

    /**
     * Package Level
     * 
     * @param period
     * @return
     */
    @RequestMapping(value = "/api/query/period")
    public List<PackageBean> getPeriodInfo(@RequestParam String period)
    {
        return service.getPeriodInfo(period);
    }

    /**
     * Package Level
     * 
     * @param cp
     *            current period
     * @param pp
     *            previous period
     * @return
     */
    @RequestMapping(value = "/api/query/period/compare")
    public List<PackageCompareBean> getPeriodCompareInfo(@RequestParam String cp, @RequestParam String pp)
    {
        return service.getPeriodCompareInfo(cp, pp);
    }

}
