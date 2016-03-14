package i321172.web;

import i321172.bean.FeatureCoverage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class CacheData
{
    private List<String>                 allFeatures = new ArrayList<String>();
    private Map<String, FeatureCoverage> featureData = new HashMap<String, FeatureCoverage>();

    @Value("${urlPrefix}")
    private String                       urlPrefix;

    @Value("${currentTime}")
    private String                       currentTime;

    public List<String> getAllFeatures()
    {
        return allFeatures;
    }

    public void setAllFeatures(List<String> allFeatures)
    {
        this.allFeatures = allFeatures;
    }

    public Map<String, FeatureCoverage> getFeatureData()
    {
        return featureData;
    }

    public void setFeatureData(Map<String, FeatureCoverage> featureData)
    {
        this.featureData = featureData;
    }

    public void putFeatureData(String feature, FeatureCoverage data)
    {
        featureData.put(feature, data);
    }

    public FeatureCoverage getFeatureData(String feature)
    {
        return featureData.get(feature);
    }

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
