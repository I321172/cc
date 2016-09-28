package i321172.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class StringUtil
{
    private static Logger logger = Logger.getLogger(StringUtil.class);

    public static boolean isNullOrEmpty(String source)
    {
        return isNull(source) || isEmpty(source);
    }

    public static boolean isNull(Object source)
    {
        return source == null;
    }

    public static boolean isEmpty(String source)
    {
        return source.equals("");
    }

    public static String getMatchString(String source, String pattern)
    {
        if (source != null)
        {
            Pattern pat = Pattern.compile(pattern);
            Matcher matcher = pat.matcher(source);

            if (matcher.find())
            {
                return source.substring(matcher.start(), matcher.end());
            }
        }

        return null;
    }

    public static long convertLong(String num)
    {
        if (!isNull(num) && num.matches("-?\\d+"))
        {
            return Long.parseLong(num);
        } else
        {
            logger.error("Invalid Number: [" + num + "]; Set as -1");
            return -1;
        }
    }

    public static boolean isNumber(String text)
    {
        if (isNullOrEmpty(text) || !text.matches("\\d+"))
            return false;
        else
            return true;
    }

    /**
     * like JS join -- e1,e2,e3
     * 
     * @param elements
     * @return
     */
    public static String join(String... arrays)
    {
        return join(Arrays.asList(arrays));
    }

    public static String join(List<String> elements)
    {
        return join(elements, "");
    }

    public static String join(List<String> elements, String sign)
    {
        return convertToString("", sign, elements, "");
    }

    /**
     * ( a1,a2)
     * 
     * @param elements
     * @return
     */
    public static String joinInBrakets(List<String> elements)
    {
        return joinInBrakets(elements, "");
    }

    public static String joinInBrakets(List<String> elements, String sign)
    {
        return convertToString("(", sign, elements, ")");
    }

    public static String join(Map<String, String> entity, String sign)
    {
        List<String> ele = new ArrayList<String>();
        for (String key : entity.keySet())
        {
            ele.add(key + "=" + sign + entity.get(key) + sign);
        }
        return join(ele);
    }

    public static String convertToString(String prefix, String sign, List<String> elements, String suffix)
    {
        return convertToString(prefix, sign, sign, elements, suffix);
    }

    public static String convertToString(String prefix, String preSign, String suSign, List<String> elements,
            String suffix)
    {
        StringBuffer result = new StringBuffer(prefix);
        for (int i = 0; i < elements.size(); i++)
        {
            result.append(preSign);
            result.append(elements.get(i));
            result.append(suSign);
            if (i < elements.size() - 1)
            {
                result.append(",");
            }
        }
        result.append(suffix);
        return result.toString();
    }

    /**
     * Like CSV source<br>
     * Row split by \n<br>
     * Column split by ,
     * 
     * @param source
     * @return
     * @throws Exception
     */
    public static String[][] parseToArray(String source) throws Exception
    {
        String[] lines = source.split("\n");
        int row = lines.length;
        int col = lines[0].split(",").length;
        String[][] results = new String[row][col];
        for (int i = 0; i < row; i++)
        {
            String line = lines[i];
            String values[] = line.split(",");
            if (values.length != col)
            {
                throw new Exception("Column Number not same! Expected: " + col + ", Actual: " + values.length
                        + " Header: " + lines[0]);
            }
            for (int j = 0; j < col; j++)
            {
                results[i][j] = values[j];
            }
        }
        return results;
    }

}
