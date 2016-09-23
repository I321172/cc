--this table is for package, owner,feature map
create table qaautocandrot_datPLT05.cctable_package_owner(
packageName varchar2(255),
feature varchar2(255),
owner varchar2(255),
developer varchar2(255),
comments varchar2(255)
);

ALTER TABLE qaautocandrot_datPLT05.cctable_package_owner add excluded varchar2(255);
ALTER TABLE qaautocandrot_datPLT05.cctable_package_owner add excludedPeriod varchar2(255);

--this table is for package data for different period
create table qaautocandrot_datPLT05.cctable_package_data(
period varchar2(255),
packageName varchar2(255),
totalCoverage  number(18,2),
lineCoverage  number(18,2),
branchCoverage  number(18,2),
totalClasses number(18),
totalMethods number(18),
totalLines number(18),
totalLinesExecuted number(18),
totalBranches number(18),
totalBranchesExecuted number(18),
toBeCovered number(18)
);

--this table is for class data for different period
create table qaautocandrot_datPLT05.cctable_class_data(
period varchar2(255),
packageName varchar2(255),
classname varchar2(255),
coverage  number(18,2),
totalLines number(18),
totalLinesExecuted number(18),
totalLinesNotExecuted number(18),
totalBranches number(18),
totalBranchesExecuted number(18),
totalBranchesNotExecuted number(18),
filename varchar2(255),
linerate number(18,2),
branchrate number(18,2),
complexity number(18,2)
);

----get the cc rate for the class level list which was required by Edwin
create table qaautocandrot_datPLT05.cctable_class_owner(
packageName varchar2(255),
className varchar2(255),
feature varchar2(255),
owner varchar2(255),
comments varchar2(255),
excluded varchar2(255),
excludedPeriod varchar2(255)
);

--add unique constraint for tables
alter table qaautocandrot_datPLT05.cctable_package_owner
add constraint package_feature_owner_unique unique(packagename,feature,owner);

alter table qaautocandrot_datPLT05.cctable_class_owner
add constraint classfeatureowner_unique unique(packagename,classname,feature,owner);

alter table qaautocandrot_datPLT05.cctable_package_data
add constraint package_period_unique unique(packagename,period);

alter table qaautocandrot_datPLT05.cctable_class_data
add constraint package_class_period_unique unique(packagename,classname,period);