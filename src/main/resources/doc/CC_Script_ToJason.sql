--check if the data imported is same with what you checked in DB
--832
select count(*) from qaautocandrot_datPLT05.cctable_package_data where period = 'B1608'

--6948
select count(*) from qaautocandrot_datPLT05.cctable_class_data where period = 'B1608'

--check the package added in a new period, if some package was added in this period, need to import these package with owner and feature first.
select packagename from qaautocandrot_datPLT05.cctable_package_data where period = 'B1608'
minus
select packagename from qaautocandrot_datPLT05.cctable_package_owner

--check if some package was removed in this period
SELECT * FROM qaautocandrot_datPLT05.cctable_package_owner where packagename in(
select packagename from qaautocandrot_datPLT05.cctable_package_owner where excluded is null
minus 
select packagename from qaautocandrot_datPLT05.cctable_package_data where period = 'B1608')

--update the exclude info if some packages was removed in this period
update qaautocandrot_datPLT05.cctable_package_owner set excluded = 'yes',excludedPeriod = 'B1608' where packagename in(
select packagename from qaautocandrot_datPLT05.cctable_package_owner where excluded is null
minus 
select packagename from qaautocandrot_datPLT05.cctable_package_data where period = 'B1608');
commit;

--get the total cc rate for specific period, to see if it same with the data published by framework team
select sum(a.totallines) lines, sum(a.totallinesexecuted) linesexecuted,sum(a.totalbranches) branches,sum(a.totalbranchesexecuted) branchesexecuted,
(sum(a.totallinesexecuted)+sum(a.totalbranchesexecuted))/( sum(a.totallines)+sum(a.totalbranches)) ccrate
from qaautocandrot_datPLT05.cctable_package_data a
where  a.period = 'B1608'

--create table cctable_package_differdata, can be used for team members to check different period package data
drop table qaautocandrot_datPLT05.cctable_package_differdata;
create table qaautocandrot_datPLT05.cctable_package_differdata as
select t3.owner,t3.feature,t1.packageName,decode(t2.totalCoverage,null,'+','=') status,
t1.totalCoverage newTotalCoverage,nvl(t2.totalCoverage,0) oldTotalCoverage, (t1.totalCoverage - nvl(t2.totalCoverage,0)) coverageDiffer,
t1.totalLines newTotalLines, nvl(t2.totalLines,0) oldTotalLines, (t1.totalLines - nvl(t2.totalLines,0)) totalLinesDiffer,
t1.totalLinesExecuted newTotalLinesExecuted,nvl(t2.totalLinesExecuted,0) oldTotalLinesExecuted, t1.toBeCovered,t1.totalbranches newTotalBranches, nvl(t2.totalbranches,0) oldTotalBranches,t1.totalBranchesExecuted newTotalBranchesExecuted,nvl(t2.totalBranchesExecuted,0) oldTotalBranchesExecuted
from 
(select * from qaautocandrot_datPLT05.cctable_package_data a where a.period = 'B1608') t1, 
(select * from qaautocandrot_datPLT05.cctable_package_data b where b.period = 'B1605') t2,
qaautocandrot_datPLT05.cctable_package_owner t3
where t1.packageName = t2.packageName(+)
and t1.packageName = t3.packageName(+);

--create table cctable_class_differdata, can be used for team members to check different period class data
drop table qaautocandrot_datPLT05.cctable_class_differdata;
create table qaautocandrot_datPLT05.cctable_class_differdata as
select t1.packageName,t1.classname,decode(t2.coverage,null,'+','=') status,
t1.coverage newCoverage,nvl(t2.coverage,0) oldCoverage, (t1.coverage - nvl(t2.coverage,0)) coverageDiffer,
t1.totalLines newTotalLines, nvl(t2.totalLines,0) oldTotalLines, (t1.totalLines - nvl(t2.totalLines,0)) totalLinesDiffer,
t1.totalLinesExecuted newTotalLinesExecuted,nvl(t2.totalLinesExecuted,0) oldTotalLinesExecuted, t1.totalLinesNotExecuted toBeCovered
from 
(select * from qaautocandrot_datPLT05.cctable_class_data a where a.period = 'B1608') t1, 
(select * from qaautocandrot_datPLT05.cctable_class_data b where b.period = 'B1605') t2
where t1.className = t2.className(+)
order by t1.packagename, t1.classname;


select distinct period from qaautocandrot_datPLT05.cctable_package_data order by period

--create a specific peirod every feature's cc data
drop table qaautocandrot_datPLT05.b1608packagedata;
create table qaautocandrot_datPLT05.b1608packagedata as
select t1.*, (t1.lines - t1.linesexecuted) TBC,(t1.linesexecuted+t1.branchesexecuted)/(t1.lines+t1.branches) ccrate from 
(
select b.feature, sum(a.totallines) lines, sum(a.totallinesexecuted) linesexecuted,sum(a.totalbranches) branches,sum(a.totalbranchesexecuted) branchesexecuted
from qaautocandrot_datPLT05.cctable_package_data a, qaautocandrot_datPLT05.cctable_package_owner b 
where a.packagename = b.packagename(+) and a.period = 'B1608'
group by b.feature) t1 
order by tbc desc ;

drop table qaautocandrot_datPLT05.b1605packagedata;
create table qaautocandrot_datPLT05.b1605packagedata as
select t1.*, (t1.lines - t1.linesexecuted) TBC,(t1.linesexecuted+t1.branchesexecuted)/(t1.lines+t1.branches) ccrate from 
(
select b.feature, sum(a.totallines) lines, sum(a.totallinesexecuted) linesexecuted,sum(a.totalbranches) branches,sum(a.totalbranchesexecuted) branchesexecuted
from qaautocandrot_datPLT05.cctable_package_data a, qaautocandrot_datPLT05.cctable_package_owner b 
where a.packagename = b.packagename(+) and a.period = 'B1605'
group by b.feature) t1 
order by tbc desc ;

--get the CC comparision data between two period
drop table qaautocandrot_datPLT05.b16081605Compare;
create table qaautocandrot_datPLT05.b16081605Compare as
select t1.feature, t1.lines b1608_totalLines, t1.linesexecuted b1608_linesexecuted, t1.branches b1608_branches, t1.branchesexecuted b1608_branchesexecuted,t1.tbc b1608_tbc, t1.ccrate b1608_ccrate, 
t2.ccrate b1605_ccrate,t2.lines b1605_totalLines, t2.linesexecuted b1605_linesexecuted, t2.branches b1605_branches, t2.branchesexecuted b1605_branchesexecuted
from qaautocandrot_datPLT05.b1608packagedata t1, qaautocandrot_datPLT05.b1605packagedata t2
where t1.feature = t2.feature(+)
order by t1.ccrate desc;

--check if the total CC rate same with the data published by framework team
select (sum(b1608_linesexecuted)+sum(b1608_branchesexecuted))/(sum(b1608_totalLines)+sum(b1608_branches)) from qaautocandrot_datPLT05.b16081605Compare

--get the each feature's cc rate for a specific period, can be used to export "B1608_CCByModule.xls" file
select (select owner from qaautocandrot_datPLT05.cctable_package_owner where feature = t1.feature and rownum = 1 ) owner,t1.*, (t1.lines - t1.linesexecuted) TBC,(t1.linesexecuted+t1.branchesexecuted)/(t1.lines+t1.branches) ccrate from 
(
select b.feature, sum(a.totallines) lines, sum(a.totallinesexecuted) linesexecuted,sum(a.totalbranches) branches,sum(a.totalbranchesexecuted) branchesexecuted
from qaautocandrot_datPLT05.cctable_package_data a, qaautocandrot_datPLT05.cctable_package_owner b 
where a.packagename = b.packagename(+) and a.period = 'B1608'
group by b.feature) t1 
order by tbc desc 

--get the CC comparision data between two periods, can be used to export "B1608_B605CompareCCByModule.xls" file
select (select owner from qaautocandrot_datPLT05.cctable_package_owner where feature = a.feature and rownum = 1 ) owner, a.* 
from qaautocandrot_datPLT05.b16081605Compare a order by a.b1608_ccrate desc

--create multiple period feature cc data
create table qaautocandrot_datPLT05.TmpCCRateSixPeriodData as
select 'B1608' period, feature, lines totallines, ccrate  from qaautocandrot_datPLT05.b1608packagedata
union
select 'B1605' period, feature, lines totallines, ccrate  from qaautocandrot_datPLT05.b1605packagedata
union
select 'B1602' period, feature, lines totallines, ccrate  from qaautocandrot_datPLT05.b1602packagedata
UNION
select 'B1511' period, feature, lines totallines, ccrate  from qaautocandrot_datPLT05.b151102packagedata
UNION
select 'B1508' period, feature, lines totallines, ccrate  from qaautocandrot_datPLT05.b1508packagedata
UNION
select 'B1411' period, feature, lines totallines, ccrate  from qaautocandrot_datPLT05.b1411packagedata

--get all the period data
select feature, period, totallines, ccrate from qaautocandrot_datPLT05.TmpCCRateSixPeriodData

--get specific period cc rate
select sum(lines) totallines,(sum(linesexecuted) + sum(branchesexecuted))/(sum(lines) + sum(branches)) ccrate from qaautocandrot_datPLT05.b1608packagedata

--get the cc data for class level, for now, only MDF RBP feature need class level cc data.
select b.owner,b.feature, sum(a.totallines) lines, sum(a.totallinesexecuted) linesexecuted,sum(a.totalbranches) branches,sum(a.totalbranchesexecuted) branchesexecuted, 
(sum(a.totallinesexecuted)+sum(a.totalbranchesexecuted))/(sum(a.totallines)+sum(a.totalbranches)) ccrate
from qaautocandrot_datPLT05.cctable_class_data a, qaautocandrot_datPLT05.cctable_class_owner b 
where a.classname = b.classname and a.period = 'B1608'
group by b.owner,b.feature
