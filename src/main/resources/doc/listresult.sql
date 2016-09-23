#get Package
select a.feature,a.packageName, a.status,a.newTotalCoverage newCoverage,a.oldTotalCoverage oldCoverage, a.coverageDiffer coverageDiffer, a.newTotalLines, a.oldTotalLines,a.totallinesdiffer,a.newTotalLinesExecuted,a.oldTotalLinesExecuted,a.tobecovered from qaautocandrot_datPLT05.cctable_package_differdata a where a.feature= ? order by packageName


#get Class
select * from(select a.feature,a.packageName,'' className, a.status,a.newTotalCoverage newCoverage,a.oldTotalCoverage oldCoverage, a.coverageDiffer coverageDiffer, a.newTotalLines, a.oldTotalLines,a.totallinesdiffer,a.newTotalLinesExecuted,a.oldTotalLinesExecuted,a.tobecovered   from qaautocandrot_datPLT05.cctable_package_differdata a union all select a.feature,a.packageName,b.className,b.status,b.newcoverage newCoverage, b.oldCoverage oldRate, b.coveragediffer coverageDiffer,  b.newTotalLines, b.oldtotallines,b.totallinesdiffer,b.newTotalLinesExecuted,b.oldTotalLinesExecuted,b.tobecovered from qaautocandrot_datPLT05.cctable_package_differdata a, qaautocandrot_datPLT05.cctable_class_differdata b where a.packageName = b.packageName) t1 where t1.feature= ? order by t1.packageName, t1.classname desc"

#create feature own mapping
drop table qaautocandrot_datPLT05.cctable_package_differdata;
create table qaautocandrot_datPLT05.cctable_feature_owner_mapping as select distinct feature,owner from qaautocandrot_datPLT05.cctable_package_owner;
