package i321172.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil
{
    public static JsonNode parseNode(String source) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(source);
        return jsonNode;
    }

}
