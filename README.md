# Tiny logdsl for ANTLR 4

轻量级日志解析工具

## 启动


找到项目目录
```
cd tiny-logdsl
```

然后使用 antlr4 Maven 插件生成词法分析器、解析器和访问者类

```bash
mvn antlr4:antlr4
```

编译所有的classes:

```bash
mvn install
```

启动main函数

```bash
mvn -q exec:java
```
