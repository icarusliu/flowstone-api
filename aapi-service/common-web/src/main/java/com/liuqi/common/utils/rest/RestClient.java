package com.liuqi.common.utils.rest;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.exception.CommErrorCodes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * Rest请求客户端
 *
 * @author LiuQi 2019/9/4-10:00
 * @version V1.0
 **/
public class RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private RestTemplate restTemplate;
    private Map<String, String> headerMap;
    private String url;
    private Map<String, Object> bodyParams;
    private String strBody;
    private Map<String, Object> urlParams;
    private Boolean isMultipartRequest = false;

    public static RestClient of(String url) {
        return new RestClient(url);
    }

    private RestClient(String url) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60*1000);
        requestFactory.setReadTimeout(600*1000);

        this.headerMap = new HashMap<>(16);
        this.restTemplate = new RestTemplate(requestFactory);
        this.url = url;
        this.bodyParams = new HashMap<>(16);
        this.urlParams = new HashMap<>(16);

        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (item.getClass() == StringHttpMessageConverter.class) {
                converterTarget = item;
                break;
            }
        }

        if (converterTarget != null) {
            converterList.remove(converterTarget);
        }
        HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converterList.add(converter);
    }

    /**
     * 执行Post请求，返回单个对象
     */
    public JSONObject post() {
        return post(JSONObject.class);
    }

    private void printLog() {
        if (logger.isTraceEnabled() && !isMultipartRequest) {
            // 如果是multipart请求时，此处的打印会导致body参数异常，原因未知
            String printStr = "开始执行http请求" +
                    "\nURL: " + url +
                    "\nURL参数：" + JSONObject.toJSONString(this.urlParams) +
                    "\nBody参数：" + JSONObject.toJSONString(this.bodyParams) +
                    "\n报文头：" + this.headerMap;
            logger.debug(printStr);
        }
    }

    /**
     * 检查返回结果，并获取返回数据
     *
     * @param responseEntity 请求返回对象
     * @param <T>            返回对象类型
     * @return 成功时的返回值
     */
    private <T> T checkAndGet(ResponseEntity<T> responseEntity) {
        if (logger.isDebugEnabled()) {
            logger.debug("请求返回: {}", responseEntity);
        }

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }

        logger.error("服务调用失败，返回结果：{}", responseEntity);
        throw AppException.of(CommErrorCodes.HTTP_FAILED);
    }

    /**
     * 执行Post请求，返回单个对象
     */
    public <T> T post(Class<T> clazz) {
        printLog();
        HttpEntity<?> entity = createPostEntity();

        ResponseEntity<T> responseEntity = restTemplate.exchange(parseUrlWithUrlParams(), HttpMethod.POST, entity, clazz, urlParams);
        return checkAndGet(responseEntity);
    }

    public byte[] postForData() {
        return this.post(byte[].class);
    }

    /**
     * 根据URLParams组装请求地址
     *
     * @return 组装后的请求地址
     */
    private String parseUrlWithUrlParams() {
        return url + urlParams.keySet().stream()
                .map(k -> k + "={" + k + "}")
                .reduce("?", (s1, s2) -> s1.concat("&").concat(s2));

    }

    /**
     * 执行Post请求，返回列表对象
     */
    public <T> List<T> postForList(Class<T> clazz) {
        return Optional.ofNullable(post(JSONArray.class))
                .map(array -> array.toJavaList(clazz))
                .orElse(new ArrayList<>(0));
    }

    /**
     * 执行无返回类型的post请求
     */
    public void voidPost() {
        printLog();
        ResponseEntity<Void> responseEntity = restTemplate.exchange(this.parseUrlWithUrlParams(), HttpMethod.POST, createPostEntity(), Void.class, urlParams);
        this.checkAndGet(responseEntity);
    }

    /**
     * 执行get请求，获取单个对象
     */
    public JSONObject get() {
        return get(JSONObject.class);
    }

    /**
     * 执行get请求，获取单个对象
     */
    public <T> T get(Class<T> clazz) {
        printLog();
        HttpEntity<?> entity = createGetEntity();
        ResponseEntity<T> responseEntity = restTemplate.exchange(this.parseUrlWithUrlParams(), HttpMethod.GET, entity, clazz, urlParams);
        return this.checkAndGet(responseEntity);
    }

    /**
     * 执行get请求，获取列表对象
     */
    public <T> List<T> getList(Class<T> clazz) {
        return Optional.ofNullable(get(JSONArray.class))
                .map(jsonArray -> jsonArray.toJavaList(clazz))
                .orElse(new ArrayList<>(0));
    }

    /**
     * 执行无返回的get请求
     */
    public void voidGet() {
        printLog();
        ResponseEntity<?> responseEntity = restTemplate.exchange(this.parseUrlWithUrlParams(), HttpMethod.GET, createGetEntity(), Void.class, urlParams);
        this.checkAndGet(responseEntity);
    }

    private HttpEntity<Map<String, ?>> createGetEntity() {
        HttpHeaders headers = new HttpHeaders();
        headerMap.forEach(headers::add);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?> createPostEntity() {
        HttpHeaders headers = new HttpHeaders();
        headerMap.forEach(headers::add);

        Object params;
        if (StringUtils.isNotBlank(strBody)) {
            params = strBody;
        } else {
            params = bodyParams;
            if (isMultipartRequest) {
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>(16);
                bodyParams.forEach(multiValueMap::add);
                params = multiValueMap;
            } else {
                headers.setContentType(MediaType.APPLICATION_JSON);
            }
        }


        return new HttpEntity<>(params, headers);
    }

    public RestClient peek(Consumer<RestClient> consumer) {
        consumer.accept(this);
        return this;
    }

    public RestClient isMultipartRequest(Boolean isMultipartRequest) {
        this.isMultipartRequest = isMultipartRequest;
        return this;
    }

    /**
     * 修改请求路径
     */
    public RestClient url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 获取请求路径
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * 指定body参数
     */
    public RestClient bodyParams(Map<String, Object> bodyParams) {
        this.bodyParams = bodyParams;
        return this;
    }

    /**
     * 指定body参数
     */
    public RestClient body(Object obj) {
        if (null == obj) {
            return this;
        }

        if (obj instanceof String || obj.getClass().isPrimitive()) {
            this.strBody = obj.toString();
        } else {
            this.bodyParams.putAll(JSONObject.parseObject(JSONObject.toJSONString(obj)));
        }
        return this;
    }

    /**
     * 增加body参数
     */
    public RestClient addBodyParam(String k, Object v) {
        this.bodyParams.put(k, v);
        return this;
    }

    /**
     * 增加body参数
     */
    public RestClient addBodyParams(Map<String, Object> param) {
        this.bodyParams.putAll(param);
        return this;
    }

    /**
     * 增加Multipart参数
     * 上传文件时使用
     */
    public RestClient addMultipartParam(String k, Object v) {
        this.isMultipartRequest = true;
        this.bodyParams.put(k, v);
        return this;
    }

    /**
     * 增加Multipart文件参数
     *
     * @param name     上传的MultipartItem中文件名称
     * @param filePath 本地文件路径
     */
    public RestClient addMultipartFile(String name, String filePath) {
        File file = new File(filePath);
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        return this.addMultipartParam(name, fileSystemResource);
    }

    public RestClient addMultipartFile(String fileName, InputStream inputStream) throws IOException {
        return addMultipartFile(fileName, "file", inputStream);
    }

    public RestClient addMultipartFile(String fileName, String paramName, InputStream inputStream) throws IOException {
        MemoryInputStreamResource inputStreamResource = new MemoryInputStreamResource(fileName, inputStream.available(), inputStream);
        return this.addMultipartParam(paramName, inputStreamResource);
    }

//    public RestClient addMultipartFile(String fileName, Workbook workbook) throws IOException {
//        return this.addMultipartFile(fileName, "file", workbook);
//    }
//
//    public RestClient addMultipartFile(String fileName, String paramName, Workbook workbook) throws IOException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        workbook.write(outputStream);
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
//        return this.addMultipartFile(fileName, paramName, inputStream);
//    }

    /**
     * 增加Multipart文件参数
     *
     * @param filePath 本地文件路径
     */
    public RestClient addMultipartFile(String filePath) {
        return addMultipartFile("file", filePath);
    }

    /**
     * 指定url参数
     */
    public RestClient urlParams(Map<String, Object> urlParams) {
        this.urlParams = urlParams;
        return this;
    }

    /**
     * 增加url参数
     */
    public RestClient addUrlParam(String k, Object v) {
        this.urlParams.put(k, v);
        return this;
    }

    /**
     * 增加url参数
     */
    public RestClient addUrlParams(Map<String, Object> values) {
        this.urlParams.putAll(values);
        return this;
    }

    /**
     * 指定restTemplate
     */
    public RestClient restTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        return this;
    }

    /**
     * 指定请求头内容
     */
    public RestClient headerMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
        return this;
    }

    /**
     * 新增报文头
     */
    public RestClient addHeader(String k, String v) {
        this.headerMap.put(k, v);
        return this;
    }

    /**
     * 新增报文头
     */
    public RestClient addHeaders(Map<String, String> headers) {
        this.headerMap.putAll(headers);
        return this;
    }
}
