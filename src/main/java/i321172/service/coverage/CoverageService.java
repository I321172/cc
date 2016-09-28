package i321172.service.coverage;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import i321172.bean.cobertura.PackageBean;
import i321172.bean.cobertura.PackageCompareBean;
import i321172.dao.CoverageDao;

@Service
public class CoverageService
{
    @Resource
    private CoverageDao coverageDao;

    public String getTotalCoverage(String period)
    {
        double c = coverageDao.getPeriodTotalCoverage(period);
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(100 * c);
    }

    /**
     * Package Level
     * 
     * @param period
     * @return
     */
    public List<PackageBean> getPeriodInfo(String period)
    {
        return coverageDao.queryPeriodPackageInfo(period);
    }

    /**
     * @param cp
     *            current period
     * @param pp
     *            previous period
     * @return
     */
    public List<PackageCompareBean> getPeriodCompareInfo(String cp, String pp)
    {
        return coverageDao.queryPackageCompareBean(cp, pp);
    }
}
