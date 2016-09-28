package i321172.dao;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseDao
{
    @Resource(name = "jdbcTemplate")
    protected JdbcTemplate  jdbc;
    protected static Logger logger     = Logger.getLogger(BaseDao.class);
    protected String        query;
    @Value("${oracle.schema}")
    protected String        schema;
    protected static String packageSql = "select a.feature,a.packageName, a.status,a.newTotalCoverage newCoverage,a.oldTotalCoverage oldCoverage, a.coverageDiffer coverageDiffer, a.newTotalLines, a.oldTotalLines,a.totallinesdiffer,a.newTotalLinesExecuted,a.oldTotalLinesExecuted,a.tobecovered from [Schema]."
            + Tables.PackageDiff.toTableName() + " a where a.feature= ? order by packageName";
    public static String    classSql   = "select * from(select a.feature,a.packageName,'' className, a.status,a.newTotalCoverage newCoverage,a.oldTotalCoverage oldCoverage, a.coverageDiffer coverageDiffer, a.newTotalLines, a.oldTotalLines,a.totallinesdiffer,a.newTotalLinesExecuted,a.oldTotalLinesExecuted,a.tobecovered from [Schema]."
            + Tables.PackageDiff.toTableName()
            + " a union all select a.feature,a.packageName,b.className,b.status,b.newcoverage newCoverage, b.oldCoverage oldRate, b.coveragediffer coverageDiffer,  b.newTotalLines, b.oldtotallines,b.totallinesdiffer,b.newTotalLinesExecuted,b.oldTotalLinesExecuted,b.tobecovered from [Schema]."
            + Tables.PackageDiff.toTableName() + " a, [Schema]." + Tables.ClassDiff.toTableName()
            + " b where a.packageName = b.packageName) t1 where t1.feature= ? order by t1.packageName, t1.classname desc";

    protected String appendRange(String sql, int start, int end)
    {
        // TODO
        StringBuilder sb = new StringBuilder("select * from ( select tp.*,rownum rn from (");
        sb.append(sql).append(") tp where rownum<").append(end).append(") tp2 where tp2.rn>").append(start - 1);
        return sb.toString();
    }

    public String getTableName(String table)
    {
        return schema + "." + table;
    }

    public int queryCount(String table, String condition)
    {
        return jdbc.queryForObject(
                new StringBuilder("select count(*) from ").append(table).append(" where ").append(condition).toString(),
                int.class);
    }

    public int queryCount(Tables table, String condition)
    {
        return queryCount(getTableName(table), condition);
    }

    public void dropTable(String table)
    {
        try
        {
            jdbc.execute(String.format("drop table %s", table));
        } catch (Exception e)
        {
            if (!e.getMessage().contains("table or view does not exist"))
                logger.error(e);
        }
    }

    public void dropTable(Tables table)
    {
        dropTable(getTableName(table));
    }

    public String getTableName(Tables table)
    {
        return getTableName(table.tableName);
    }

    @PostConstruct
    public void init()
    {
        packageSql = packageSql.replace("[Schema]", schema);
        classSql = classSql.replace("[Schema]", schema);
    }

    public static enum Tables
    {
        PackageDiff("cctable_package_differdata"), ClassDiff("cctable_class_differdata"), FeatureOwnerMapping(
                "cctable_feature_owner_mapping"), PackageData(
                        "cctable_package_data"), ClassData("cctable_class_data"), PackageOwner("cctable_package_owner");

        Tables(String tableName)
        {
            this.tableName = tableName;
        }

        private String tableName;

        public String toTableName()
        {
            return tableName;
        }

    }
}
