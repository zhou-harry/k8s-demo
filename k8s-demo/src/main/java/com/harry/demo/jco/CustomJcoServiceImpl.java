package com.harry.demo.jco;


import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: CustomJcoServiceImpl
 * @description: TODO
 * @date 2019/8/7 10:33
 */

@Service
public class CustomJcoServiceImpl implements CustomJcoService {

    @Autowired
    private JcoProperties jcoProperties;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    //构造方法之后执行
    @PostConstruct
    public void init() {
        /**
         * 初始化配置参数 连接池
         */
        Properties connectProperties = new Properties();
        //ERP服务器IP地址
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, jcoProperties.getAshost());
        //实例编号
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, jcoProperties.getSysnr());
        //客户端
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, jcoProperties.getClient());
        //用户名
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, jcoProperties.getUser());
        //密码
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, jcoProperties.getPasswd());
        //语言
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, jcoProperties.getLang());
        // 如果小于JCO_POOL_CAPACITY的值，则自动设置为该值，在没有设置JCO_POOL_CAPACITY的情况下为0
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, jcoProperties.getPoolCapacity());
        //同时可创建的最大活动连接数，0表示无限制，默认为JCO_POOL_CAPACITY的值
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, jcoProperties.getPeakLimit());

        JcoDestinationDataProvider provider = new JcoDestinationDataProvider();
        try {
//            Environment.registerDestinationDataProvider(provider);
        } catch (IllegalStateException providerAlreadyRegisteredException) {
            //somebody else registered its implementation,
            //stop the execution
            throw new Error(providerAlreadyRegisteredException);
        }
        provider.addDestinationProperties(jcoProperties.getDestName(), connectProperties);

    }

    //测试连接是否连通
    @Override
    public R pingCalls(String destName) {
        JCoDestination dest;
        try {
            dest = JCoDestinationManager.getDestination(destName);
            dest.ping();
            return new R<>(R.SUCCESS, "success", dest.getAttributes());
        } catch (JCoException e) {
            return new R<>(R.FAIL, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 传入功能名称和Map类型参数
     *
     * @param functionName
     * @param paramMap
     * @return
     */
    @Override
    public R callRFC(String functionName, Map<String, Object> paramMap) {
        Map resultMap = new HashMap();
        try {
            JCoDestination conn = JCoDestinationManager.getDestination(jcoProperties.getDestName());
            JCoFunction fun = conn.getRepository().getFunction(functionName);
            if (fun == null) {
                return new R(R.FAIL, functionName + "不存在");
            }
            //传入参数
            JCoParameterList input = fun.getImportParameterList();
            if (paramMap != null) {
                for (Iterator<Map.Entry<String, Object>> it = paramMap.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> param = it.next();
                    if (param.getValue() instanceof List) {
                        setTableParamList(fun, param);
                    } else if (param.getValue() instanceof Map) {
                        setImportParameterList(fun, param);
                    } else {
                        input.setValue("" + param.getKey(), param.getValue());
                    }
                }
            }
            JCoContext.begin(conn);
            try {
                //执行方法
                fun.execute(conn);
            } finally {
                JCoContext.end(conn);
            }
            //获取返回结果
            if (fun.getExportParameterList() != null) {
                getExportParameterList(fun, resultMap);
            }
            if (fun.getTableParameterList() != null) {
                getTableParameterList(fun, resultMap);
            }
        } catch (JCoException e) {
            return new R(R.FAIL, ExceptionUtils.getStackTrace(e));
        } catch (CustomJcoException e) {
            return new R(R.FAIL, ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            return new R(R.FAIL, ExceptionUtils.getStackTrace(e));
        }
        return new R<Map>(resultMap);
    }


    //设置表格传入参数
    private void setTableParamList(JCoFunction fun, Map.Entry<String, Object> pairs) throws CustomJcoException {
        JCoTable tb = fun.getImportParameterList().getTable("" + pairs.getKey());
        List ls = (List) pairs.getValue();

        for (int i = 0; i < ls.size(); i++) {
            Map<String, String> m = (Map<String, String>) ls.get(i);
            tb.appendRow();
            for (JCoFieldIterator jft = tb.getFieldIterator(); jft.hasNextField(); ) {
                JCoField p = jft.nextField();
                if ("date".equalsIgnoreCase(p.getTypeAsString())) {
                    if (m.containsKey(p.getName())) {
                        if (!"".equals(m.get(p.getName()))) {
                            try {
                                p.setValue(simpleDateFormat.parse(m.get(p.getName())));
                            } catch (ParseException e) {
                                throw new CustomJcoException(e);
                            }
                        } else {
                            p.setValue("");
                        }
                    }
                } else {
                    if (m.containsKey(p.getName())) {
                        if (m.get(p.getName()) == null) {
                            throw new CustomJcoException("参数" + p.getName() + "为null");
                        }
                        p.setValue(m.get(p.getName()));
                    }
                }
            }
        }
    }

    //设置列表传入参数
    private void setImportParameterList(JCoFunction fun, Map.Entry<String, Object> pairs) throws CustomJcoException {
        Map<String, String> pairsMap = (Map<String, String>) pairs.getValue();
        JCoStructure jcos = fun.getImportParameterList().getStructure("" + pairs.getKey());

        for (JCoFieldIterator jft = jcos.getFieldIterator(); jft.hasNextField(); ) {
            JCoField jf = jft.nextField();

            if ("date".equalsIgnoreCase(jf.getTypeAsString())) {
                if (pairsMap.containsKey(jf.getName())) {
                    if (!"".equals(pairsMap.get(jf.getName()))) {
                        try {
                            jf.setValue(simpleDateFormat.parse(pairsMap.get(jf.getName())));
                        } catch (ParseException e) {
                            throw new CustomJcoException(e);
                        }
                    } else {
                        jf.setValue("");
                    }
                } else {
                    throw new CustomJcoException("参数错误，没有准备参数【" + jf.getName() + "】");
                }
            } else {

                if (pairsMap.containsKey(jf.getName())) {
                    if (pairsMap.get(jf.getName()) == null) {
                        throw new CustomJcoException("参数" + jf.getName() + "为null");
                    }
                    jf.setValue(pairsMap.get(jf.getName()));
                }
            }
        }
    }

    /**
     * 获取输出参数列表
     *
     * @param fun
     * @param resultMap
     */
    private static void getExportParameterList(JCoFunction fun, Map resultMap) {
        for (Iterator<JCoField> it = fun.getExportParameterList().iterator(); it.hasNext(); ) {
            parseParameter(it.next(), resultMap);
        }
    }

    private static void getTableParameterList(JCoFunction fun, Map resultMap) {
        for (Iterator<JCoField> it = fun.getTableParameterList().iterator(); it.hasNext(); ) {
            parseParameter(it.next(), resultMap);
        }
    }

    /**
     * 解析参数
     *
     * @param jCoField
     * @param resultMap
     * @return
     */
    private static void parseParameter(JCoField jCoField, Map resultMap) {
        if (jCoField.isTable()) {
            JCoTable tb = jCoField.getTable();
            List resultList = new ArrayList();
            for (int i = 0; i < tb.getNumRows(); i++) {
                Map retMap = new HashMap();
                tb.setRow(i);
                retMap = new HashMap();
                for (JCoRecordFieldIterator itA = tb.getRecordFieldIterator(); itA.hasNextField(); ) {
                    JCoField field = itA.nextField();
                    retMap.put(field.getName(), tb.getString(field.getName()));
                }
                resultList.add(retMap);
            }
            resultMap.put("" + jCoField.getName(), resultList);
        } else if (jCoField.isStructure()) {
            JCoStructure st = jCoField.getStructure();
            Map resutStructureMap = new HashMap();
            for (JCoFieldIterator jft = st.getFieldIterator(); jft.hasNextField(); ) {
                JCoField jf = jft.nextField();
                resutStructureMap.put(jf.getName(), jf.getValue());
            }
            resultMap.put("" + jCoField.getName(), resutStructureMap);
        } else {
            resultMap.put("" + jCoField.getName(), jCoField.getValue());
        }
    }
}
