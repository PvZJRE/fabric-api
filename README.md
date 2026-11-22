# fabric-api

PvZ-Portable 的模组 API，形制仿照 Fabric API：**多模块** Gradle 构建，自带 Fabric 形制的事件总线（`Event<T>` / `EventFactory`，包 `io.github.pvzjre.api.event`），其上再提供内容注册、重平衡与模组资源。

| 模块 | 内容 |
|---|---|
| `fabric-api` | 聚合器，无代码：模组依赖这一个坐标，聚合 jar 以 jar-in-jar 方式内嵌五个模块 |
| `api-base` | `PvzFabricApi`、`ApiState`、`ApiModInitializer`、`ModQuery` 与事件总线 `api.event` |
| `api-events` | `api.lawn.LawnEvents`（关卡开始/结束、波次、僵尸生成/死亡/受伤、植物放置/移除/受伤、收集阳光、种植校验、每 tick、绘制前后）与 `api.lifecycle.GameLifecycleEvents` |
| `api-plant` | 植物注册（`PlantType`、`PlantRegistry`、`PlantRules`、图鉴文本、行为钩子）与重平衡（定义行、生成血量/倒计时、发射与子弹伤害、棋盘级旋钮） |
| `api-resource` | 模组资源：`ModImageSlot`、`ModImages`、`GameArt`、`ModReanims`、`ResourceOverrides`（散文件遮蔽 pak 成员） |
| `api-zombie` | 僵尸注册（`ZombieType`、`ZombieRegistry`、重动画/血量/护甲/波次/tick 钩子）与重平衡（生成血量/阶段/类型替换/动画） |

## 特性

- **自研事件总线**：不依赖上游 Fabric API 的 base 模块——该模块 0.4.0 起的事件类编译针对 Minecraft/Guava，无法在游戏侧使用；本总线是纯 JDK 实现，形制与 Fabric 一致。
- **内容注册**：游戏对每类内容持有一张容量固定的定义表（`SeedType::MAX_SEED_TYPES` / `ZombieType::MAX_ZOMBIE_TYPES`，零售行在下、空行在上），注册即向表内写入一行；API 的 Mixin 把行为分派进游戏自己的方法。种子选择器与图鉴在原版网格之后的页面展示注册内容。
- **重平衡缝**：定义与出生数值之外的"游戏后续决策"也留有缝——全部是"游戏自己的值，直到模组设置一个"：僵尸的心控增益/燃烧免疫/受击护盾优先、植物的碾碎免疫/离地状态/额外发射种子、棋盘的出怪表/阳光上限/传送带种子列表等（`ZombieOverrides` / `PlantOverrides` / `BoardOverrides`）。
- **模组资源**：`ModImageSlot`（游戏从不绘制的零售图槽位，跨资源加载保持绑定）、`ModImages`/`GameArt`/`ModReanims`（重着色、按轨道覆盖、额外头部层），以及**散文件遮蔽 pak**（"95 版"式重皮肤：目录/单文件/原始字节按引擎的同名查找规则遮蔽 pak 成员）。
- **jar-in-jar 打包**：聚合 jar 把五个模块嵌在 `META-INF/jars/`，安装只需一个 jar；模组声明 `"fabric-api": ">=1.2.2"` 即可。

## 构建与发布

```bash
./gradlew build                    # 单元测试
./gradlew publishToMavenLocal      # 发布 io.github.pvzjre:fabric-api 与五个模块到 mavenLocal
./gradlew publishToMavenCentral    # 发布六个构件到 Maven Central（需要凭据与 GPG）
```

- 依赖：Fabric Loader 与本游戏（compileOnly + 测试，运行时由 loader 提供）；工具链插件 `io.github.pvzjre.loom` 从 Gradle Plugin Portal 解析。
- `publishToMavenCentral` 的凭据（Central Portal 令牌与 GPG 密钥）从 `~/.gradle/gradle.properties` 读取，不在本构建里；没有它们的检出仍可 `build`，只是无法发布到 Portal。

## 许可证

MIT（见 `LICENSE`）。游戏本体 `PvZ-Portable` 采用 LGPL-3.0-or-later 与 PopCap 框架许可证。
