package i321172.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ParamsBean
{
    private int queryStart;
    private int totalCount;

    public int getQueryStart()
    {
        return queryStart;
    }

    public void setQueryStart(int queryStart)
    {
        this.queryStart = queryStart;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
}
