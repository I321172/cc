package i321172.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimeDuration
{
    private static Map<String, Integer> units = new LinkedHashMap<String, Integer>();
    static
    {
        units.put("s", 60);
        units.put("m", 60);
        units.put("H", 24);
        units.put("D", 7);
        units.put("W", 52);
        units.put("Y", 100);
    }

    public static String getDiff(long leftTime, String startUnit)
    {
        List<String> usedUnit = new ArrayList<String>();
        List<Long> num = new ArrayList<Long>();
        StringBuffer sb = new StringBuffer();
        boolean isStart = false;
        Iterator<String> ite = units.keySet().iterator();
        for (; ite.hasNext();)
        {
            String unit = ite.next();
            int divide = units.get(unit);
            long cur = leftTime % divide;
            leftTime = leftTime / divide;

            if (unit.equals(startUnit))
            {
                isStart = true;
            }
            if (isStart)
            {
                usedUnit.add(unit);
                num.add(cur);
                if (leftTime <= 0)
                {
                    break;
                }
            }
        }
        for (int i = usedUnit.size() - 1; i >= 0; i--)
        {
            sb.append(num.get(i)).append(usedUnit.get(i));
        }
        return sb.toString();
    }

    public static String getDiff(Date start, Date end, String startUnit)
    {
        // seconds
        if (start == null || end == null)
            return "";
        long diff = (end.getTime() - start.getTime()) / 1000;
        return getDiff(diff, startUnit);
    }

}
