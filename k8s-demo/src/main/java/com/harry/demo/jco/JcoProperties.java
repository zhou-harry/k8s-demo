package com.harry.demo.jco;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhouhong
 * @version 1.0
 * @title: JcoProperties
 * @description: sap服务器连接配置
 * @date 2019/8/9 17:22
 */
@Getter
@Setter
@Data
@ConfigurationProperties(prefix = "sap.jco.provider")
public class JcoProperties {

    private String destName = "FSCD-DEV";

    private String ashost = "10.1.118.73";

    private String client = "140";

    private String sysnr = "06";

    private String lang = "zh";

    private String user = "ILAS";

    private String passwd = "Tplhk12345";

    private String poolCapacity = "10";
    //同时可创建的最大活动连接数，0表示无限制，默认为JCO_POOL_CAPACITY的值
    private String peakLimit = "50";
}
