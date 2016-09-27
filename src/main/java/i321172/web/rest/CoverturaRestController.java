package i321172.web.rest;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import i321172.bean.cobertura.ClassBean;
import i321172.bean.cobertura.PackageBean;
import i321172.service.cobertura.CoberTableService;
import i321172.service.cobertura.FetchCoverage;

@RestController
public class CoverturaRestController
{
    @Resource
    private FetchCoverage     fetch;
    @Resource
    private CoberTableService tableService;

    @RequestMapping(value = "/api/cober/package")
    public List<PackageBean> fetchPackageData(@RequestParam String period,
            @RequestParam(defaultValue = "9") String moduleId,
            @RequestParam(defaultValue = "Platform") String moduleName) throws Exception
    {
        return fetch.fetchPackageData(period, moduleId, moduleName);
    }

    @RequestMapping(value = "/api/cober/class")
    public List<ClassBean> fetchClassData(@RequestParam String period,
            @RequestParam(defaultValue = "9") String moduleId,
            @RequestParam(defaultValue = "Platform") String moduleName) throws Exception
    {
        return fetch.fetchClassData(period, moduleId, moduleName);
    }

    @RequestMapping(value = "/api/import/package")
    public String ImportPackageData(@RequestParam String period, @RequestParam(defaultValue = "9") String moduleId,
            @RequestParam(defaultValue = "Platform") String moduleName, @RequestParam String call) throws Exception
    {
        int count = fetch.importPackageData(period, moduleId, moduleName);
        return count + " Package Data Record imported";
    }

    @RequestMapping(value = "/api/import/class")
    public String importClassData(@RequestParam String period, @RequestParam(defaultValue = "9") String moduleId,
            @RequestParam(defaultValue = "Platform") String moduleName) throws Exception
    {
        int count = fetch.importClassData(period, moduleId, moduleName);
        return count + " Class Data Record imported";
    }

    @RequestMapping(value = "/api/table/create")
    public void createTable(@RequestParam String table, @RequestParam(required = false) String cp,
            @RequestParam(required = false) String pp) throws Exception
    {
        if (table.equalsIgnoreCase("feature"))
            tableService.createFeatureOwnerMappingTable();
        else if (table.equalsIgnoreCase("packagediff"))
            tableService.createPackageDiffTable(cp, pp);
        else if (table.equalsIgnoreCase("classdiff"))
            tableService.createClassDiffTable(cp, pp);
        else if (table.equalsIgnoreCase("periodpackage"))
            tableService.createPeriodPackageDataTable(cp);
        else if (table.equalsIgnoreCase("periodpackagediff"))
            tableService.createPeriodPackageCompareTable(cp, pp);
        else
            throw new Exception("No such Operation");

    }
}
