package i321172.web;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import i321172.utils.DBUtil;

@Component
public class ScheduledTasks
{
    @Resource
    private CacheData cacheData;
    @Resource
    private DBUtil    dbUtil;

    @Scheduled(fixedRate = 72000000)
    public void getAllFeatures()
    {
        cacheData.setAllFeatures(dbUtil.getAllFeature());
    }

}
