package com.wecloud.plugin;

import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class PaginationPlugin extends PluginAdapter
{

    public PaginationPlugin()
    {
    }

    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        addLimit(topLevelClass, introspectedTable, "limitStart");
        addLimit(topLevelClass, introspectedTable, "limitEnd");
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
    {
        XmlElement isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test", "limitStart != null"));
        isNotNullElement.addElement(new TextElement("limit ${limitStart} , ${limitEnd}"));
        element.addElement(isNotNullElement);
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
    {
        XmlElement isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test", "limitStart != null"));
        isNotNullElement.addElement(new TextElement("limit ${limitStart} , ${limitEnd}"));
        element.addElement(isNotNullElement);
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name)
    {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(FullyQualifiedJavaType.getStringInstance());
        field.setName(name);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = (new StringBuilder(String.valueOf(Character.toUpperCase(c)))).append(name.substring(1)).toString();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName((new StringBuilder("set")).append(camel).toString());
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), name));
        method.addBodyLine((new StringBuilder("this.")).append(name).append("=").append(name).append(";").toString());
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName((new StringBuilder("get")).append(camel).toString());
        method.addBodyLine((new StringBuilder("return ")).append(name).append(";").toString());
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    public boolean validate(List warnings)
    {
        System.out.println(warnings);
        return true;
    }
}
