package i321172.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import i321172.bean.CoverageCompareBean;
import i321172.bean.cobertura.ClassBean;
import i321172.bean.cobertura.PackageBean;
import i321172.dao.mapper.CoverageCompareMapper;
import i321172.utils.TimeDuration;

@Service
public class CoverageDao extends BaseDao
{
    public List<CoverageCompareBean> queryList(String feature, boolean onlyPackage)
    {
        query = onlyPackage ? packageSql : classSql;
        return queryCoverageCompare(query, onlyPackage, feature);
    }

    public List<CoverageCompareBean> queryCoverageCompare(String sql, boolean onlyPackage, Object... params)
    {
        return jdbc.query(sql, params, new CoverageCompareMapper(onlyPackage));
    }

    public List<CoverageCompareBean> queryPackageDiffInRange(String feature, int startRow, int endRow)
    {
        query = this.appendRange(packageSql, startRow, endRow);
        return queryCoverageCompare(query, true, feature);
    }

    public List<CoverageCompareBean> queryClassDiffInRange(String feature, int startRow, int endRow)
    {
        query = this.appendRange(classSql, startRow, endRow);
        return queryCoverageCompare(query, false, feature);
    }

    public List<CoverageCompareBean> queryDiffInRange(String feature, boolean onlyPackage, int startRow, int endRow)
    {
        return onlyPackage ? queryPackageDiffInRange(feature, startRow, endRow)
                : queryClassDiffInRange(feature, startRow, endRow);
    }

    public List<String> getAllFeature()
    {
        String sql = "select feature from " + this.getTableName(Tables.FeatureOwnerMapping);
        List<String> features = jdbc.queryForList(sql, String.class);
        return features;
    }

    public int importPackage(String period, List<PackageBean> packages)
    {
        Date start = new Date();
        int count = 0;
        for (PackageBean pack : packages)
        {
            count += importPackage(period, pack);
        }
        Date end = new Date();
        logger.info("Import Class takes " + TimeDuration.getDiff(start, end, "s"));
        return count;
    }

    public int importPackage(String period, PackageBean pack)
    {
        String sql = "insert into " + getTableName(Tables.PackageData)
                + " (period,packageName,totalCoverage,lineCoverage,branchCoverage,totalClasses,totalMethods,totalLines,totalLinesExecuted,totalBranches,totalBranchesExecuted,toBeCovered) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        int count = jdbc.update(sql, period, pack.getPackageName(), pack.getTotalCoverage(), pack.getLineCoverage(),
                pack.getBranchCoverage(), pack.getClassNum(), pack.getMethodNum(), pack.getLines(),
                pack.getCoverLines(), pack.getBranches(), pack.getCoverBranches(), pack.getToBeCoveredLines());
        return count;
    }

    public int importClass(String period, List<ClassBean> clazzs)
    {
        Date start = new Date();
        int count = 0;
        for (ClassBean clazz : clazzs)
        {
            count += importClass(period, clazz);
        }
        Date end = new Date();
        logger.info("Import Class takes " + TimeDuration.getDiff(start, end, "s"));
        return count;
    }

    public int importClass(String period, ClassBean clazz)
    {
        String sql = "insert into " + getTableName(Tables.ClassData)
                + " (period,packageName,classname,coverage,totalLines,totalLinesExecuted,totalLinesNotExecuted,totalBranches,totalBranchesExecuted,totalBranchesNotExecuted,filename,linerate,branchrate,complexity) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int count = jdbc.update(sql, period, clazz.getPackageName(), clazz.getClassName(), clazz.getTotalCoverage(),
                clazz.getLines(), clazz.getCoverLines(), clazz.getToBeCoveredLines(), clazz.getBranches(),
                clazz.getCoverBranches(), clazz.getToBeCoveredBranches(), clazz.getFileName(), clazz.getLineRate(),
                clazz.getBranchRate(), clazz.getComplexity());
        return count;
    }

    public void createFeatureOwnerMappingTable(boolean isDropBeforeCreate)
    {
        if (isDropBeforeCreate)
            jdbc.execute("drop table " + getTableName(Tables.FeatureOwnerMapping));
        String sql = " create table " + getTableName(Tables.FeatureOwnerMapping)
                + " as select distinct feature,owner from " + getTableName(Tables.PackageOwner);
        jdbc.execute(sql);
    }

}
