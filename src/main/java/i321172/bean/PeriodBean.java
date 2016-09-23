package i321172.bean;

import org.springframework.stereotype.Repository;

@Repository
public class PeriodBean
{
    private String period;
    private String periodTime;

    public String getPeriodTime()
    {
        return periodTime;
    }

    public void setPeriodTime(String periodTime)
    {
        this.periodTime = periodTime;
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod(String period)
    {
        this.period = period;
    }

}
