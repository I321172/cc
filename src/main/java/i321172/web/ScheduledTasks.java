package i321172.web;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks
{
    @Scheduled(fixedRate = 9900000)
    public void test()
    {

    }

}
