package i321172.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class CacheData
{
    /**
     * Cobertura site
     */
    @Value("${urlPrefix}")
    private String urlPrefix;

    /**
     * Period time
     */
    @Value("${currentTime}")
    private String currentTime;

    public String getUrlPrefix()
    {
        return urlPrefix;
    }

    public void setUrlPrefix(String url)
    {
        urlPrefix = url;
    }

    public String getCurrentTime()
    {
        return currentTime;
    }

    public void setCurrentTime(String time)
    {
        currentTime = time;
    }

    public String getCoverageFilePrefix()
    {
        return getUrlPrefix() + getCurrentTime();
    }

}
