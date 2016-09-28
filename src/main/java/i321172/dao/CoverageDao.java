package i321172.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import i321172.bean.CoverageCompareBean;
import i321172.bean.cobertura.ClassBean;
import i321172.bean.cobertura.PackageBean;
import i321172.bean.cobertura.PackageCompareBean;
import i321172.dao.mapper.CoverageCompareMapper;
import i321172.utils.StringUtil;
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

    public int importPackage(String period, List<PackageBean> packages) throws InterruptedException, ExecutionException
    {
        Date start = new Date();
        Set<ImportPackageCallable> callables = new HashSet<>();
        ExecutorService service = Executors.newFixedThreadPool(50);
        for (PackageBean pack : packages)
        {
            callables.add(new ImportPackageCallable(period, pack));
        }
        List<Future<Integer>> fResult = service.invokeAll(callables, 60, TimeUnit.SECONDS);
        int count = 0;
        for (Future<Integer> f : fResult)
        {
            count += f.get();
        }
        service.shutdown();
        Date end = new Date();
        logger.info("Import Package takes " + TimeDuration.getDiff(start, end, "s"));
        return count;
    }

    public int importPackage(String period, final PackageBean pack)
    {
        String sql = "insert into " + getTableName(Tables.PackageData)
                + " (period,packageName,totalCoverage,lineCoverage,branchCoverage,totalClasses,totalMethods,totalLines,totalLinesExecuted,totalBranches,totalBranchesExecuted,toBeCovered) values(?,?,?,?,?,?,?,?,?,?,?,?)";

        int count = jdbc.update(sql, period, pack.getPackageName(), pack.getTotalCoverage(), pack.getLineCoverage(),
                pack.getBranchCoverage(), pack.getClassNum(), pack.getMethodNum(), pack.getLines(),
                pack.getCoverLines(), pack.getBranches(), pack.getCoverBranches(), pack.getToBeCoveredLines());
        return count;
    }

    class ImportPackageCallable implements Callable<Integer>
    {
        private String      period;
        private PackageBean pack;

        ImportPackageCallable(String period, PackageBean pack)
        {
            this.pack = pack;
            this.period = period;
        }

        @Override
        public Integer call() throws Exception
        {
            // TODO Auto-generated method stub
            String sql = "insert into " + getTableName(Tables.PackageData)
                    + " (period,packageName,totalCoverage,lineCoverage,branchCoverage,totalClasses,totalMethods,totalLines,totalLinesExecuted,totalBranches,totalBranchesExecuted,toBeCovered) values(?,?,?,?,?,?,?,?,?,?,?,?)";
            int count = jdbc.update(sql, period, pack.getPackageName(), pack.getTotalCoverage(), pack.getLineCoverage(),
                    pack.getBranchCoverage(), pack.getClassNum(), pack.getMethodNum(), pack.getLines(),
                    pack.getCoverLines(), pack.getBranches(), pack.getCoverBranches(), pack.getToBeCoveredLines());
            return count;
        }
    }

    class ImportClassCallable implements Callable<Integer>
    {
        private String    period;
        private ClassBean clazz;

        ImportClassCallable(String period, ClassBean clazz)
        {
            this.period = period;
            this.clazz = clazz;
        }

        @Override
        public Integer call() throws Exception
        {
            String sql = "insert into " + getTableName(Tables.ClassData)
                    + " (period,packageName,classname,coverage,totalLines,totalLinesExecuted,totalLinesNotExecuted,totalBranches,totalBranchesExecuted,totalBranchesNotExecuted,filename,linerate,branchrate,complexity) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            int count = jdbc.update(sql, period, clazz.getPackageName(), clazz.getClassName(), clazz.getTotalCoverage(),
                    clazz.getLines(), clazz.getCoverLines(), clazz.getToBeCoveredLines(), clazz.getBranches(),
                    clazz.getCoverBranches(), clazz.getToBeCoveredBranches(), clazz.getFileName(), clazz.getLineRate(),
                    clazz.getBranchRate(), clazz.getComplexity());
            return count;
        }
    }

    public int importClass(String period, List<ClassBean> clazzs) throws InterruptedException, ExecutionException
    {
        Date start = new Date();
        Set<ImportClassCallable> callables = new HashSet<>();
        ExecutorService service = Executors.newFixedThreadPool(50);
        for (ClassBean pack : clazzs)
        {
            callables.add(new ImportClassCallable(period, pack));
        }
        List<Future<Integer>> fResult = service.invokeAll(callables, 60, TimeUnit.SECONDS);
        int count = 0;
        for (Future<Integer> f : fResult)
        {
            count += f.get();
        }
        service.shutdown();
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

    public void createFeatureOwnerMappingTable()
    {
        dropTable(Tables.FeatureOwnerMapping);
        String sql = "create table " + getTableName(Tables.FeatureOwnerMapping)
                + " as select distinct feature,owner from " + getTableName(Tables.PackageOwner);
        jdbc.execute(sql);
    }

    public int countPackageData(String period)
    {
        return this.queryCount(Tables.PackageData, "period ='" + period + "'");
    }

    public int countClassData(String period)
    {
        return this.queryCount(Tables.ClassData, "period ='" + period + "'");
    }

    /**
     * --check the package added in a new period, if some package was added in
     * this period, need to import these package with owner and feature first.
     * 
     * @param currentPeriod
     * @param perviousPeriod
     * @return
     */
    public List<String> checkNewAddedPackage(String period)
    {
        String sql = "select packagename from " + getTableName(Tables.PackageData)
                + " where period = ? minus select packagename from " + getTableName(Tables.PackageOwner);
        return jdbc.queryForList(sql, String.class, period);
    }

    /**
     * --check if some package was removed in this period
     * 
     * @param period
     * @return
     */
    public List<String> checkNewRemovedPackage(String period)
    {
        String sql = "SELECT packagename FROM " + getTableName(Tables.PackageOwner)
                + " where excluded is null minus select packagename from " + getTableName(Tables.PackageData)
                + " where period = ?";
        return jdbc.queryForList(sql, String.class, period);
    }

    /**
     * --update the exclude info if some packages was removed in this period
     * 
     * @param period
     * @param comment
     * @param packages
     * @return
     */
    public int setPackageExculde(String period, String comment, List<String> packages)
    {
        StringBuilder sb = new StringBuilder("update ").append(getTableName(Tables.PackageOwner))
                .append(" set exculded = 'yes',excludePeriod=?,comment=? where packagename in")
                .append(StringUtil.joinInBrakets(packages, "'"));
        return jdbc.update(sb.toString(), period, comment);
    }

    public void createPackageDiffTable(String currentPeriod, String previousPeriod)
    {
        dropTable(Tables.PackageDiff);
        StringBuilder sb = new StringBuilder("create table ").append(getTableName(Tables.PackageDiff))
                .append(" as select t3.owner,t3.feature,t1.packageName,decode(t2.totalCoverage,null,'+','=') status, t1.totalCoverage newTotalCoverage,nvl(t2.totalCoverage,0) oldTotalCoverage, (t1.totalCoverage - nvl(t2.totalCoverage,0)) coverageDiffer,t1.totalLines newTotalLines, nvl(t2.totalLines,0) oldTotalLines, (t1.totalLines - nvl(t2.totalLines,0)) totalLinesDiffer,t1.totalLinesExecuted newTotalLinesExecuted,nvl(t2.totalLinesExecuted,0) oldTotalLinesExecuted, t1.toBeCovered,t1.totalbranches newTotalBranches, nvl(t2.totalbranches,0) oldTotalBranches,t1.totalBranchesExecuted newTotalBranchesExecuted,nvl(t2.totalBranchesExecuted,0) oldTotalBranchesExecuted from (select * from ")
                .append(getTableName(Tables.PackageData)).append(" a where a.period='").append(currentPeriod)
                .append("') t1, (select * from ").append(getTableName(Tables.PackageData)).append(" b where b.period='")
                .append(previousPeriod).append("') t2, ").append(getTableName(Tables.PackageOwner))
                .append(" t3 where t1.packageName=t2.packageName(+) and t1.packageName=t3.packageName(+)");
        jdbc.execute(sb.toString());
    }

    /**
     * --create table cctable_class_differdata, can be used for team members to
     * check different period class data
     * 
     * @param currentPeriod
     * @param previousPeriod
     * @param isDrop
     * @return
     */
    public void createClassDiffTable(String currentPeriod, String previousPeriod)
    {
        dropTable(Tables.ClassDiff);

        StringBuilder sb = new StringBuilder("create table ").append(getTableName(Tables.ClassDiff))
                .append(" as select t1.packageName,t1.classname,decode(t2.coverage,null,'+','=') status,t1.coverage newCoverage,nvl(t2.coverage,0) oldCoverage, (t1.coverage - nvl(t2.coverage,0)) coverageDiffer,t1.totalLines newTotalLines, nvl(t2.totalLines,0) oldTotalLines, (t1.totalLines - nvl(t2.totalLines,0)) totalLinesDiffer,t1.totalLinesExecuted newTotalLinesExecuted,nvl(t2.totalLinesExecuted,0) oldTotalLinesExecuted, t1.totalLinesNotExecuted toBeCovered from (select * from ")
                .append(getTableName(Tables.ClassData)).append(" a where a.period='").append(currentPeriod)
                .append("') t1,(select * from ").append(getTableName(Tables.ClassData)).append(" b where b.period='")
                .append(previousPeriod)
                .append("') t2 where t1.className=t2.className(+) order by t1.packagename,t1.classname");
        jdbc.execute(sb.toString());
    }

    public List<String> getAllPeriods()
    {
        StringBuilder sb = new StringBuilder("selct distinct period from ").append(getTableName(Tables.PackageData))
                .append(" order by period");
        return jdbc.queryForList(sb.toString(), String.class);
    }

    public void createPeriodPackageDataTable(String period)
    {
        String table = getTableName(period + "packagedata");
        dropTable(table);
        StringBuilder sb = new StringBuilder("create table ").append(table)
                .append(" as select t1.*, (t1.lines - t1.linesexecuted) TBC,(t1.linesexecuted+t1.branchesexecuted)/(t1.lines+t1.branches) ccrate from (select b.feature, sum(a.totallines) lines, sum(a.totallinesexecuted) linesexecuted,sum(a.totalbranches) branches,sum(a.totalbranchesexecuted) branchesexecuted from ")
                .append(getTableName(Tables.PackageData)).append(" a,").append(getTableName(Tables.PackageOwner))
                .append(" b where a.packagename = b.packagename(+) and a.period = '").append(period)
                .append("' group by b.feature) t1 order by tbc desc");
        jdbc.execute(sb.toString());
    }

    /**
     * --get the CC comparison data between two period
     * 
     * @param period
     * @param previousPeriod
     * @param isDrop
     * @return
     */
    public void createPeriodPackageCompareTable(String period, String previousPeriod)
    {
        String table = getTableName(period + previousPeriod + "Compare");
        String t1 = getTableName(period + "packagedata");
        String t2 = getTableName(previousPeriod + "packagedata");

        dropTable(table);

        StringBuilder sb = new StringBuilder("create table ").append(table).append("as select t1.feature, t1.lines ")
                .append(period).append("_totalLines, t1.linesexecuted ").append(period)
                .append("_linesexecuted, t1.branches ").append(period).append("_branches, t1.branchesexecuted ")
                .append(period).append("_branchesexecuted, t1.tbc ").append(period).append("_tbc, t1.ccrate ")
                .append(period).append("_ccrate, t2.ccrate ").append(previousPeriod).append("_ccrate,t2.lines ")
                .append(previousPeriod).append("_totalLines, t2.linesexecuted ").append(previousPeriod)
                .append("_linesexecuted, t2.branches ").append(previousPeriod).append("_branches, t2.branchesexecuted ")
                .append(previousPeriod).append("_branchesexecuted from ").append(t1).append(" t1, ").append(t2)
                .append(" t2 where t1.feature = t2.feature(+) order by t1.ccrate desc");
        jdbc.execute(sb.toString());
    }

    /**
     * --get the each feature's cc rate for a specific period, can be used to
     * export "Bxxxx_CCByModule.xls" file
     * 
     * @param period
     * @return
     */
    public List<PackageBean> queryPeriodPackageInfo(String period)
    {
        String table = getTableName(period + "packagedata");
        String sql = new StringBuilder("select f.owner,p.* from ").append(table).append(" p,")
                .append(getTableName(Tables.FeatureOwnerMapping))
                .append(" f where p.feature=f.feature(+) order by tbc desc").toString();
        return queryPackageBean(sql);
    }

    private List<PackageBean> queryPackageBean(String sql, Object... params)
    {
        RowMapper<PackageBean> packageMapper = new RowMapper<PackageBean>()
        {

            @Override
            public PackageBean mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                PackageBean pack = new PackageBean();
                pack.setOwner(rs.getString("owner"));
                pack.setFeature(rs.getString("feature"));
                pack.setLines(rs.getInt("lines"));
                pack.setCoverLines(rs.getInt("linesexecuted"));
                pack.setBranches(rs.getInt("branches"));
                pack.setCoverBranches(rs.getInt("branchesexecuted"));
                pack.setToBeCoveredLines(rs.getInt("tbc"));
                pack.setTotalCoverage(rs.getDouble("ccrate"));
                return pack;
            }
        };
        if (params != null && params.length > 0)
        {
            return jdbc.query(sql, params, packageMapper);
        } else
        {
            return jdbc.query(sql, packageMapper);
        }
    }

    /**
     * --get the CC comparision data between two periods, can be used to export
     * eg. "B1608_B605CompareCCByModule.xls" file
     * 
     * @param cp
     *            Current Period
     * @param pp
     *            Previous Period
     * @return
     */
    public List<PackageCompareBean> queryPackageCompareBean(final String cp, final String pp)
    {
        String table = getTableName(cp + pp + "packagedata");
        String sql = new StringBuilder("select f.owner,p.* from ").append(table).append(" p,")
                .append(getTableName(Tables.FeatureOwnerMapping)).append(" f where p.feature=f.feature(+) order by ")
                .append(cp).append("_ccrate desc").toString();
        RowMapper<PackageCompareBean> packageCompareMapper = new RowMapper<PackageCompareBean>()
        {
            @Override
            public PackageCompareBean mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                PackageCompareBean comp = new PackageCompareBean();
                comp.setOwner(rs.getString("owner"));
                comp.setFeature(rs.getString("feature"));
                comp.setLines(rs.getInt(cp + "_totallines"));
                comp.setCoverLines(rs.getInt(cp + "_linesexecuted"));
                comp.setBranches(rs.getInt(cp + "_branches"));
                comp.setCoverBranches(rs.getInt(cp + "_branchesexecuted"));
                comp.setToBeCoveredLines(rs.getInt(cp + "_tbc"));
                comp.setTotalCoverage(rs.getDouble(cp + "_ccrate"));
                PackageBean pre = new PackageBean();
                pre.setOwner(rs.getString("owner"));
                pre.setFeature(rs.getString("feature"));
                pre.setLines(rs.getInt(pp + "_totallines"));
                pre.setCoverLines(rs.getInt(pp + "_linesexecuted"));
                pre.setBranches(rs.getInt(pp + "_branches"));
                pre.setCoverBranches(rs.getInt(pp + "_branchesexecuted"));
                pre.setToBeCoveredLines(rs.getInt(pp + "_tbc"));
                pre.setTotalCoverage(rs.getDouble(pp + "_ccrate"));
                comp.setPrePackageBean(pre);
                return comp;
            }
        };
        return jdbc.query(sql, packageCompareMapper);
    }

    public double getPeriodTotalCoverage(String period)
    {
        String table = getTableName(period + "packagedata");
        String sql = "select (sum(linesexecuted)+sum(branchesexecuted))/(sum(lines)+sum(branches)) totalCoverage from "
                + getTableName(table);
        return jdbc.queryForObject(sql, Double.class);
    }

}
