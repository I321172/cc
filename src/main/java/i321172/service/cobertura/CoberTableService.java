package i321172.service.cobertura;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import i321172.dao.CoverageDao;

@Service
public class CoberTableService
{
    @Resource
    private CoverageDao coverageDao;

    public void createFeatureOwnerMappingTable()
    {
        coverageDao.createFeatureOwnerMappingTable();
    }

    public void createPackageDiffTable(String currentPeriod, String previousPeriod)
    {
        coverageDao.createPackageDiffTable(currentPeriod, previousPeriod);
    }

    public void createClassDiffTable(String currentPeriod, String previousPeriod)
    {
        coverageDao.createClassDiffTable(currentPeriod, previousPeriod);
    }

    public void createPeriodPackageDataTable(String period)
    {
        coverageDao.createPeriodPackageDataTable(period);
    }

    public void createPeriodPackageCompareTable(String period, String previousPeriod)
    {
        coverageDao.createPeriodPackageCompareTable(period, previousPeriod);
    }
}
