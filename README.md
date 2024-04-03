# CaseGenTool

Goland插件，用于集成内部单元测试用例生成工具


## 版本说明
+ v1.0.0
  + 发布日期: 20231113
  + 说明：初始版本
+ v1.0.1
  + 发布日期: 20231114
  + 说明：弹出窗口取消固定置顶，可切换至其他窗口
+ v1.0.2
  + 发布日期: 20231121
  + 说明：
    1. 运行改为debug，便于断点调试
    2. <运行用例> 菜单顺序调整
+ v1.0.3
  + 发布日期: 20231122
  + 说明：
    1. 支持 MQ consumer事件
    2. 生成用例失败时，不关闭窗口弹出窗口取消固定置顶，可切换至其他窗口
+ v1.0.4
  + 发布日期: 20231127
  + 说明：设置默认tcp超时时间，避免窗口卡死
+ v1.0.5
  + 发布日期: 20231221
  + 说明：支持最新版本goland
+ v1.0.6
  + 发布日期: 20240111
  + 说明：
    1. 生成用例窗口增加滚动条
    2. 优化超时消息提示框
+ v1.0.7
  + 发布日期: 20230322
  + 说明：
    1. 优化：文件创建后自动从磁盘加载
    2. 预启动服务时，单测结果弹窗展示，并可双击跳转至用例文件
    3. 用例生成时，支持metadata输入
    4. 添加菜单开关，用于切换debug/run运行模式
    5. 添加菜单开关，用于开启/禁用代码变更检测
    6. tpl文件改为加密压缩包
    7. tcp超时由10秒改为30秒
+ v1.0.8
  + 发布日期: 20230403
  + 说明：优化jetbrains plugins 发布规则检测项