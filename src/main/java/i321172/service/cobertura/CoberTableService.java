package i321172.service.cobertura;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import i321172.dao.CoverageDao;

@Service
public class CoberTableService
{
    @Resource
    private CoverageDao coverageDao;

    public void createFeatureOwnerMappingTable(boolean isDropBeforeCreate)
    {
        coverageDao.createFeatureOwnerMappingTable(isDropBeforeCreate);
    }
}
