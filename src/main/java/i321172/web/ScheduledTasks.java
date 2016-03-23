package i321172.web;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import i321172.utils.DBUtil;

@Component
public class ScheduledTasks
{
    @Resource
    private CacheData         cacheData;
    @Resource
    private DBUtil            dbUtil;
    @Resource
    private RequestController controller;
    private Logger            logger = Logger.getLogger(ScheduledTasks.class);

    @Scheduled(fixedRate = 72000000)
    public void getAllFeatures()
    {
        cacheData.setAllFeatures(dbUtil.getAllFeature());
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void refreshFeatureData()
    {
        cacheData.clearFeatureData();
        for (String feature : cacheData.getAllFeatures())
        {
            try
            {
                controller.getFeature(feature, true);
            } catch (Exception e)
            {
                logger.error("Failed to get Feature data of " + feature, e);
            }
        }
    }
}