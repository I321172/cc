package i321172.service.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class HttpClient
{
    private static Logger logger = Logger.getLogger(HttpClient.class);

    /**
     * @param url
     * @param isProxy
     *            Default proxy : proxy:8080
     * @return
     * @throws Exception
     */
    public String fetchWebResponse(String url, boolean isProxy) throws Exception
    {
        return fetchWebResponse(new HttpClientBean.Builder(url).setProxy(isProxy).build());
    }

    public String fetchWebResponse(HttpClientBean httpBean) throws Exception
    {
        fetchWeb(httpBean);
        return httpBean.getResponseBody();
    }

    public HttpClientBean fetchWeb(HttpClientBean httpBean) throws IOException
    {
        getResponse(httpBean, false);
        return httpBean;
    }

    public InputStream getResponse(HttpClientBean httpBean) throws IOException
    {
        return getResponse(httpBean, true);
    }

    private InputStream getResponse(HttpClientBean httpBean, boolean isCopy) throws IOException
    {
        httpBean.clearResponseInfo();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig httpConfig = httpBean.getRequestConfig();
        HttpRequestBase method = null;
        String methodType = httpBean.getMethodType();
        String queryUrl = httpBean.getUrl();
        Map<String, String> paras = httpBean.getRequestParas();
        switch (methodType)
        {
            case "Get":
                queryUrl = appendParasToUrl(queryUrl, paras, "utf-8");
                method = new HttpGet(queryUrl);
                break;

            case "Delete":
                queryUrl = appendParasToUrl(queryUrl, paras, "utf-8");
                method = new HttpDelete(queryUrl);
                break;

            case "Post":
                // headers.put("Content-Type",
                // "application/x-www-form-urlencoded");
                method = new HttpPost(queryUrl);
                ((HttpPost) method).setEntity(this.getHttpEntity(httpBean));
                break;
        }
        method.setConfig(httpConfig);
        addHeader(httpBean, method);
        log(httpBean.toString(), true);
        CloseableHttpResponse response = httpClient.execute(method);
        httpBean.setStatus(response.getStatusLine().getStatusCode());
        log(httpBean.toString() + " with status: " + response.getStatusLine().getStatusCode(), true);
        boolean info = httpBean.getStatus() != 200;
        convertResponseHeaders(response.getAllHeaders(), info, httpBean);
        // String responseBody = EntityUtils.toString(response.getEntity());
        InputStream is = response.getEntity().getContent();
        InputStream ret;
        if (isCopy)
        {
            ret = IOUtils.toBufferedInputStream(is);
            return ret;
        } else
        {
            httpBean.setResponseBody(IOUtils.toString(is));
        }
        close(response);
        close(httpClient);
        return null;
    }

    private String appendParasToUrl(String url, Map<String, String> paras, String encoding)
            throws UnsupportedEncodingException
    {
        // first, check the paramters
        if (paras.keySet().size() > 0)
        {
            if (url.indexOf("?") < 0)
            {
                url += "?";
            } else
            {
                url += "&";
            }
            for (String para : paras.keySet())
            {
                url += URLEncoder.encode(para, encoding) + "=" + URLEncoder.encode(paras.get(para), encoding) + "&";
            }
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private void addHeader(HttpClientBean httpBean, HttpRequestBase method)
    {
        Map<String, String> headers = httpBean.getHeaders();
        for (String header : headers.keySet())
        {
            method.addHeader(header, headers.get(header));
            log("Add header: " + header + " = " + headers.get(header));
        }
    }

    /**
     * Append Default Proxy :proxy:8080
     * 
     * @param url
     * @param cookieName
     * @return
     * @throws Exception
     */
    public String getCookieValue(String url, String cookieName) throws Exception
    {
        return getCookieValue(url, true, cookieName);
    }

    public String getCookieValue(String url, boolean isProxy, String cookieName) throws Exception
    {
        HttpClientBean httpBean = new HttpClientBean.Builder(url).setProxy(isProxy).build();
        return getCookieValue(httpBean, cookieName);
    }

    public String getCookieValue(HttpClientBean httpBean, String cookieName) throws IOException
    {
        fetchWeb(httpBean);
        Map<String, String> respHeaders = httpBean.getResponseHeaders();
        String cookie = respHeaders.get("Set-Cookie");
        if (isNull(cookie))
            return null;
        String result = null;
        if (cookie.contains(cookieName))
        {
            result = cookie.replaceAll(".*(" + cookieName + ".*?);.*", "$1");
        }
        return result;
    }

    private Map<String, String> convertResponseHeaders(Header[] headers, boolean info, HttpClientBean httpBean)
    {
        Map<String, String> result = httpBean.getResponseHeaders();
        for (Header header : headers)
        {
            String name = header.getName();
            String headerValue = result.get(name);
            httpBean.addResponseHeaderString(name + "=" + header.getValue() + "\n");
            if (name.equalsIgnoreCase("location"))
            {
                httpBean.setRedirect(header.getValue());
            }
            if (headerValue == null)
            {
                headerValue = header.getValue();
            } else
            {
                headerValue += "; " + header.getValue();
            }
            result.put(name, headerValue);
            log(name + " = " + headerValue, info);
        }
        return result;
    }

    private boolean isNull(String text)
    {
        return text == null;
    }

    private HttpEntity getHttpEntity(HttpClientBean clientBean)
    {
        if (!isNull(clientBean.getBody()))
        {
            HttpEntity entity = new StringEntity(clientBean.getBody(), Charset.forName("UTF-8"));
            return entity;
        } else
        {
            return null;
        }
    }

    private void close(Closeable toClose)
    {
        try
        {
            toClose.close();
        } catch (IOException e)
        {
            this.log(e.getMessage());
        }
    }

    private void log(String msg)
    {
        log(msg, false);
    }

    private void log(String msg, boolean info)
    {
        if (info)
        {
            logger.info(msg);
        } else
        {
            logger.debug(msg);
        }
    }

}
