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
import i321172.dao.CoverageDao;
import i321172.utils.TimeDuration;

@RestController
public class CoverageCompareRestController
{
    @Resource
    private CoverageDao coverageDao;
    @Resource
    private ParamsBean  paramBean;
    private Logger      logger = Logger.getLogger(CoverageCompareRestController.class);

    @RequestMapping(value = "/api/show")
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

}
