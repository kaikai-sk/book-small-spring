package com.springframework.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.springframework.beans.BeansException;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.factory.config.AbstractBeamDefinitionReader;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanReference;
import com.springframework.beans.factory.config.api.BeanDefinitionRegistry;
import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

public class XmlBeanDefinitionReader extends AbstractBeamDefinitionReader {
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try (InputStream inputStream = resource.getInputStream()){
            doLoadBeanDefinitions(inputStream);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {
        for (Resource resource : resources) {
            loadBeanDefinitions(resource);
        }
    }

    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        loadBeanDefinitions(resource);
    }

    private void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException {
        Document doc = XmlUtil.readXML(inputStream);
        Element root = doc.getDocumentElement();
        NodeList childNodes = root.getChildNodes();

        for (int i = 0; i< childNodes.getLength(); i++) {
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }
            // 检查标签名字
            if (!"bean".equals(childNodes.item(i).getNodeName())) {
                continue;
            }

            parseEveryBean(childNodes, i);
        }
    }


    private void parseEveryBean(NodeList childNodes, int beanIndex) throws ClassNotFoundException {
        Element bean = (Element) childNodes.item(beanIndex);
        String id = bean.getAttribute("id");
        String name = bean.getAttribute("name");
        String className = bean.getAttribute("class");

        Class<?> clazz = Class.forName(className);
        String beanName = StrUtil.isNotEmpty(id) ? id : name;
        if (StrUtil.isEmpty(beanName)) {
            beanName = StrUtil.lowerFirst(clazz.getSimpleName());
        }

        BeanDefinition beanDefinition = new BeanDefinition(clazz);
        // 读取属性并填充
        for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
            if (!(bean.getChildNodes().item(j) instanceof Element)) {
                continue;
            }
            if (!"property".equals(bean.getChildNodes().item(j).getNodeName())) {
                continue;
            }
            parseEveryProperty(bean, j, beanName, beanDefinition);
        }

        if (getRegistry().containsBeanDefinition(beanName)) {
            throw new BeansException(String.format("Duplicate beanName [%s] is not allowed", beanName));
        }
        getRegistry().registerBeanDefinition(beanName, beanDefinition);
    }


    // 解析property标签
    private void parseEveryProperty(Element bean, int propetyIndex, String beanName,
        BeanDefinition beanDefinition) {
        Element property = (Element) bean.getChildNodes().item(propetyIndex);
        String attrName = property.getAttribute("name");
        String attrValue = property.getAttribute("value");
        String attrRef = property.getAttribute("ref");
        Object value = StrUtil.isNotEmpty(attrRef) ? new BeanReference(attrRef) : attrValue;
        PropertyValue propertyValue = new PropertyValue(attrName, value);
        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
    }
}
