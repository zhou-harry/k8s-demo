package com.harry.demo.jco;

import java.util.Map;

/**
 * @author zhouhong
 * @version 1.0
 * @title: CustomJcoService
 * @description: TODO
 * @date 2019/8/7 10:31
 */
public interface CustomJcoService {

    //测试连接是否连通
    R pingCalls(String destName);

    R callRFC(String functionName, Map<String, Object> paramMap);

}
