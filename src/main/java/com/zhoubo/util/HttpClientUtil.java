package com.zhoubo.util;


import com.zhoubo.model.CharEncoding;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装了采用HttpClient发送HTTP请求的方法
 *
 * @author Alone
 */
public class HttpClientUtil {
    private final static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 发送HTTP_GET请求
     *
     * @param reqURL 请求地址(含参数)
     * @return 远程主机响应正文
     */
    public static String sendGetRequest(String reqURL) {
        String respContent = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        HttpGet httpGet = new HttpGet(reqURL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                Charset respCharset = ContentType.getOrDefault(entity).getCharset();
                respContent = EntityUtils.toString(entity, respCharset);
                EntityUtils.consume(entity);
            }
            StringBuilder respHeaderDatas = new StringBuilder();
            for (Header header : response.getAllHeaders()) {
                respHeaderDatas.append(header.toString()).append("\r\n");
            }
            /*String respStatusLine = response.getStatusLine().toString();
			String respHeaderMsg = respHeaderDatas.toString().trim();
			String respBodyMsg = respContent;*/
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return respContent;
    }

    public static byte[] htppGetTobytes(String reqURL) {
        byte[] respContent = null;
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        HttpGet httpGet = new HttpGet(reqURL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                respContent = EntityUtils.toByteArray(entity);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return respContent;
    }

    /**
     * 发送HTTP_POST请求
     *
     * @param reqURL        请求地址
     * @param reqData       请求参数,若有多个参数则应拼接为param11=value11&22=value22&33=value33的形式
     * @param encodeCharset 编码字符集,编码请求数据时用之,此参数为必填项(不能为""或null)
     * @return 远程主机响应正文
     */
    public static String sendPostRequest(String reqURL, String reqData, String encodeCharset) {
        String reseContent = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        HttpPost httpPost = new HttpPost(reqURL);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + encodeCharset);
        try {
            httpPost.setEntity(new StringEntity(reqData == null ? "" : reqData, encodeCharset));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                reseContent = EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return reseContent;
    }

    /**
     * 发送HTTP_POST_SSL请求
     * add by mark 2015年6月30日
     *
     * @param reqURL        请求地址
     * @param params        请求参数
     * @param encodeCharset 编码字符集,编码请求数据时用之,当其为null时,则取HttpClient内部默认的ISO-8859-1编码请求参数
     * @return 远程主机响应正文
     * @see 1)该方法会自动关闭连接,释放资源
     * @see 2)该方法亦可处理普通的HTTP_POST请求
     * @see 3)当处理HTTP_POST_SSL请求时,默认请求的是对方443端口,除非reqURL参数中指明了SSL端口
     * @see 4)方法内设置了连接和读取超时时间,单位为毫秒,超时或发生其它异常时方法会自动返回"通信失败"字符串
     * @see 5)请求参数含中文等特殊字符时,可直接传入本方法,并指明其编码字符集encodeCharset参数,方法内部会自动对其转码
     * @see 6)方法内部会自动注册443作为SSL端口,若实际使用中reqURL指定的SSL端口非443,可自行尝试更改方法内部注册的SSL端口
     * @see 7)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html;charset=GBK]的charset值
     * @see 若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
     */
    public static String sendPostSSLRequest(String reqURL, Map<String, String> params, String encodeCharset) {
        String responseContent = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
            HttpPost httpPost = new HttpPost(reqURL);
            if (null != params) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset));
            }
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, CharEncoding.UTF_8);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }

    /**
     * add by mark
     * 2015年6月30日
     *
     * @param requestUrl
     * @param requestMethod
     * @param outputStr
     * @return
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        HttpsURLConnection conn = null;
        InputStream is = null;
        OutputStream out = null;
        BufferedReader bufReader = null;
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        try {
            URL url = new URL(requestUrl);
            SSLContext sc = SSLContext.getInstance("SSL");
            TrustManager[] tm = {trustManager};
            SecureRandom random = new SecureRandom();
            sc.init(null, tm, random);
            //当代理配置参数不为空时，自动设置代理（需要代理的才去配置代理参数）
            conn = (HttpsURLConnection) url.openConnection();

            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(hostnameVerifier);
            //设置请求方式:get/post
            conn.setRequestMethod(requestMethod);
            conn.setDoInput(true);//允许输入
            conn.setDoOutput(true);//允许输出
            conn.setUseCaches(false);//不许缓存
            conn.setReadTimeout(60 * 1000);//一分钟超时

            //需要传递流时，一定要添加的参数，而且ACTION中通过request.getInputStream获取流的情况下，也必须添加该参数
            conn.setRequestProperty("content-type", "text/html");//访问struts2Action时需要设置，否则request得不到inputStream
            conn.setRequestProperty("Charsert", "utf-8");  //访问struts2Action时需要设置，否则request得不到inputStream中文乱码
            //当outputStr不为null时，向输出流写数据
            if (null != outputStr) {
                out = conn.getOutputStream();//得到输出流
                //在Action中获流数据乱码时处理方法本地发送方式
                //String aa = URLDecoder.decode(str, "utf-8");
                //out.write(aa.getBytes());//发送数据
                out.write(outputStr.getBytes("UTF-8"));//linux发送方式
                out.flush();
            }
            is = conn.getInputStream();//得到输入流
            bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder buff = new StringBuilder();
            String line = null;
            if ((line = bufReader.readLine()) != null) {
                buff.append(line);
            }
            return buff.toString();
        } catch (ConnectException e) {
            log.error("连接超时：{}", e);
        } catch (IOException e) {
            log.error("IO异常：{}", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException SSL异常：{}", e);
        } catch (KeyManagementException e) {
            log.error("KeyManagementException SSL异常：{}", e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (out != null) {
                    out.close();
                }
                if (bufReader != null) {
                    bufReader.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * add by lee
     * 2015年6月30日
     * <p>
     * 初始化HTTPS的HttpClient
     */
    private static HttpClient initHttpSSLClient() {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory sslsf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", 443, sslsf);
            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            return httpclient;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 发送HTTPS_GET请求
     *
     * @param reqURL 请求地址(含参数)
     * @return 远程主机响应正文
     */
    public static String sendGetSSLRequest(String reqURL) {
        String respContent = "";
        HttpClient httpClient = initHttpSSLClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        HttpGet httpGet = new HttpGet(reqURL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    Charset respCharset = ContentType.getOrDefault(entity).getCharset();
                    respContent = EntityUtils.toString(entity, respCharset);
                    EntityUtils.consume(entity);
                }
                StringBuilder respHeaderDatas = new StringBuilder();
                for (Header header : response.getAllHeaders()) {
                    respHeaderDatas.append(header.toString()).append("\r\n");
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return respContent;
    }

    public static String httpRequest(String jsonParam, String requestUrl) throws Exception {
        String message = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒

            http.connect();
            OutputStream os = http.getOutputStream();

            os.write(jsonParam.getBytes("UTF-8"));// 传入参数
            os.flush();
            os.close();

            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            message = new String(jsonBytes, "UTF-8");
            System.out.println(message);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static String httpXMLPost(String urlStr, String xml, String encodeCharset) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String xmlInfo = "";
            out.write(new String(xmlInfo.getBytes(encodeCharset)));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                result.append(line);
            }
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * @return 所代表远程资源的响应结果
     */
    public static String httpSendPost(String url, String param, String encodeCharset) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();

            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在 http正文内，因此需要设为true, 默认情况下是false;
            conn.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            conn.setDoInput(true);
            // Post 请求不能使用缓存
            conn.setUseCaches(false);
            //设定请求的方法为"POST"，默认是GET
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒

            // getOutputStream会隐含的进行connect即conn.connect()
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(new String(param.getBytes(encodeCharset)));
            // flush输出流的缓冲
            out.flush();
            // getInputStream()函数执行时把准备好的http请求正式发送到服务器
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            log.debug("发送请求后获取的响应信息:" + result);
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static String uploadFile(String url, InputStream inputStream) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpClient httpClient = httpClientBuilder.build();
/*        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.info("url has some problem !");
            e.printStackTrace();
        }*/
        HttpPost request = new HttpPost(url);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody("media", inputStream);
        HttpEntity httpEntity = multipartEntityBuilder.build();
        request.setEntity(httpEntity);
        HttpResponse response;
        HttpEntity responseHttpEntity;
        String responseStr = null;
        try {
            response = httpClient.execute(request);
            responseHttpEntity = response.getEntity();
            responseStr = responseHttpEntity.getContent().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseStr;
    }

    public static String uploadFile(String url, File file) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpClient httpClient = httpClientBuilder.build();
/*        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.info("url has some problem !");
            e.printStackTrace();
        }*/
        HttpPost request = new HttpPost(url);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody("media", file);
        HttpEntity httpEntity = multipartEntityBuilder.build();
        request.setEntity(httpEntity);
        HttpResponse response;
        HttpEntity responseHttpEntity;
        String responseStr = null;
        try {
            response = httpClient.execute(request);
            responseHttpEntity = response.getEntity();
            responseStr = EntityUtils.toString(responseHttpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseStr;
    }

    public static InputStream htppGetToInputStream(String reqURL) {
//        byte[] respContent = null;
        InputStream inputStream = null;
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        HttpGet httpGet = new HttpGet(reqURL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                inputStream = entity.getContent();
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return inputStream;
    }
}