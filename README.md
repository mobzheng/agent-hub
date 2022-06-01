# agent-hub
各种agent合集
trace-agent模块，实现了链路追踪

# 使用方法
在需要采集的应用jvm启动参数中加 -javaagent:xx/transmittable-thread-local-2.12.2.jar -javaagent:xxx/trace-agent-1.0-SNAPSHOT.jar
> 需要注意的点
> 1. transmittable-thread-local-2.12.2.jar必须在trace-agent之前，因为trace中是用了ttl的特性才能采集到的使用了线程池的方法。
> 2. ttl jar包可以在github获取，也可使用resource目录提供的。使用自己获取的需要去验证，如果版本小于2.6，则需要增加-Xbootclasspath参数。
> 3. 如果业务系统也使用了ttl，这个还没有测试^_^


