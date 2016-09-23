package i321172.bean.cobertura;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class CoberturaBean
{
    @Value("${cober.site}")
    private String site;
    @Value("${cober.package.path}")
    private String packageDownload;
    @Value("${cober.class.path}")
    private String classDownload;
    @Value("${cober.download.method}")
    private String downMethod;
    @Value("${cober.getrunid.path}")
    private String getRunIdPath;
    @Value("${cober.getrunid.method}")
    private String getRunIdMethod;

    public String getSite()
    {
        return site;
    }

    public void setSite(String site)
    {
        this.site = site;
    }

    public String getPackageDownloadUrl(String moduleId, String runId, String moduleName)
    {
        return getDownloadUrl(site, packageDownload, moduleId, runId, moduleName);
    }

    public String getClassDownloadUrl(String moduleId, String runId, String moduleName)
    {
        return getDownloadUrl(site, classDownload, moduleId, runId, moduleName);
    }

    public String getRunIdUrl(String releaseTag)
    {
        return convertToString(site, getRunIdPath, "?method=", getRunIdMethod, "&runType=Full&releaseTag=", releaseTag);
    }

    public void setPackageDownload(String packageDownload)
    {
        this.packageDownload = packageDownload;
    }

    public void setClassDownload(String classDownload)
    {
        this.classDownload = classDownload;
    }

    private String getDownloadUrl(String site, String path, String moduleId, String runId, String moduleName)
    {
        return convertToString(site, path, "?method=", downMethod, "&moduleId=", moduleId, "&runId=", runId,
                "&moduleName=", moduleName);
    }

    private String convertToString(String... args)
    {
        StringBuilder sb = new StringBuilder();
        for (String ar : args)
        {
            sb.append(ar);
        }
        return sb.toString();
    }

}
