# ShootEXP

这是一个能让玩家之间通过进行深入交流而变得更加了解彼此，我觉得它是一个交友插件。当一个玩家对另一个玩家不停蹲起达到一定次数后，这个插件可以让该玩家射出“一滩粘稠的经验”，其中包含了一定量的经验值，当玩家吃掉“一滩粘稠的经验”时，就会获得其中蕴含的经验。

## 版本区别

| 分支    | 描述     | 支持MC版本    | 下载                  |
| ------- | -------- | ------------- | --------------------- |
| master  | 普通版本 | 1.14+         | [ShootEXP-1.1.jar](https://github.com/R-Josef/ShootEXP/releases/download/1.1/ShootEXP-1.1.jar) |
| nms     | NMS版本  | 1.12.2 1.16.4 | [ShootEXP-1.1-nms.jar](https://github.com/R-Josef/ShootEXP/releases/download/1.1-nms/ShootEXP-1.1-nms.jar) |

## 使用方法

- 将插件.jar文件复制进plugins文件夹
- 重启服务器

## 命令与权限

| 命令                                       | 权限             | 描述                 |
| ------------------------------------------ | ---------------- | -------------------- |
| /shootexp help                             | 无               | 获取帮助             |
| /shootexp status \[玩家\]                  | shootexp.status  | 查看玩家或自己的状态 |
| /shootexp item <所有者> <赠予者> <数量>    | shootexp.item    | 获取一个经验物品     |
| /shootexp restore all <玩家>               | shootexp.restore | 恢复玩家的全部状态   |
| /shootexp restore times <玩家> <次数>      | shootexp.restore | 恢复玩家的射出次数   |
| /shootexp restore stock <玩家> <数量>      | shootexp.restore | 恢复玩家的经验存量   |
| /shootexp set <玩家> <射出次数> <经验存量> | shootexp.set     | 设置玩家的状态       |
| /shootexp reload                           | shootexp.reload  | 重载插件             |
玩家默认拥有`shootexp.status`权限，op默认拥有所有权限。

## 插件配置

一般来说默认的配置就适合大多数服务器了，但是ShootEXP也同样提供了各种设置来满足服主的个性化需求。配置文件和大多数插件一样就在插件文件夹下的config.yml。方便起见，我先解释一些在下面的介绍中会使用的词语：
- “施法” 指玩家不停蹲起，最后射出经验这一过程
- “攻击” 指玩家一次蹲起
- “攻击者” 指施法过程中蹲起的玩家
- “防守者” 指施法过程中被攻击的玩家
- “所有者” 指经验物品是由哪个攻击者产生的
- “赠予者” 指经验物品是在所有者在对谁施法时产生的

### 语言

这个插件中文叫射出经验，并没有任何谐音，所以该插件也适用于国际玩家，虽然目前只有zh_CN一种语言，但是我相信开源社区会送我更多的语言的（发出了白嫖的声音）。在`config.yml`文件中，`lang`选项即是语言选项，它会让插件读取`lang/<语言>.yml`选项，例如：
```yaml
# 语言设置
lang: zh_CN
```
插件会使用`lang/zh_CN.yml`作为语言文件。

### 最大经验存量

一个玩家身上所储存的粘稠经验并不是无限的，`max-stock`设置设定了玩家最大的经验存量，这个值应该是一个整数：
```yaml
# 最大经验存量
max-stock: 1000
```

### 射出经验函数

玩家需要不停的蹲起达到指定次数之后，就可以射出指定数量的经验，这个插件本来将射出经验的条件、射出的数量都定义好了，可是很多对射出经验很有研究的专家表示经验不该这样射。为了满足这些高端用户，我特别提供了自定义射出经验函数的功能，多亏了exp4j这一个强大的字符串解析库，我让这个想法成为了现实，现在玩家可以在以下两个选项中定义自己的射出经验函数：
```yaml
# 施法成功所需的攻击次数
required-attack-times: '1.618^SHOOT + 10'

# 每次施法可以射出的经验数额
shoot-amount: 'STOCK / 2'
```
`SHOOT`表示已施法次数，`STOCK`表示经验存量，`MAXSTOCK`表示最大经验存量，大部分的数学符号都能在这里使用。

### 可施法实体类型

一开始这个插件插件只能对玩家施法，但后来有一个玩家声称他很爱他家的旺财，希望也可以和旺财深入交流，于是现在的ShootEXP支持自定义可施法的实体对象：
```yaml
# 可施法实体的类型，Entity表示全部，Creature表示生物，更多分类请查看下方网址
entity-type:
- 'Player'
- 'Creature'
```
[这个网页](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/package-summary.html) 包含了所有bukkit中的实体，只要是这里面的实体类型都可以填写。我知道有些xp系统比较特殊的玩家甚至想对非实体施法，以后我可能会添加这方面的更新。

### 经验类型

为了能让RPG服主也用上这个插件，为了让世界充满❤，这个插件也支持SkillApi与MMOCore的经验：
```yaml
# 经验类型：VANILLA（原版）、SKILLAPI、MMOCORE
exp-type: 'VANILLA'
```

### 攻击设置

我想到了“鞭长莫及”这个成语，纵使你的鞭再长也会有够不着的地方，同一个道理，就算你的鞭再硬，也会有软下来的一天：
```yaml
# 攻击设置
attack:
  # 有效攻击距离（单位：方块）
  distance: 2.0
  # 超时时间（单位tick）
  timeout: 100
```
当目标超过`distance`所定义的距离后，玩家就无法再攻击到他，当超过`timeout`定义的时间没有攻击，施法就算超时中断。

### 恢复设置

我有个朋友做过一个实验，他和方块人比恢复能力，结果方块人被碾压，最后他还嘲讽说我们都是老年人。为了打消这类玩家的气焰，我让恢复的设置完全可自定义，服主可以将恢复调得非常快，让这些人精尽人亡：
```yaml
# 恢复设置
restore:
  # 施法次数
  shoot:
    # 恢复一次施法次数的时间间隔（单位：tick）
    period: 6000
    # 恢复数量
    amount: 1
  # 经验存量
  stock:
    # 恢复一次经验存量的时间间隔（单位：tick）
    period: 6000
    # 恢复数量
    amount: 200
```
### 自定义模型ID

由于原版的骨粉看起来其实不那么让人有食欲，这个插件还提供了一个设置能让启用了材质包的服主通过1.14新增的CustomModelData让经验物品使用材质：
```yaml
# 经验物品的自定义模型数据
custom-model-data:
  enable: false
  value: 0
```
做一个看起来鲜嫩可口的“粘稠的经验”给玩家吃吧！

### 自定义音效

“攻击”听起来是不是很可怕？其实这个插件根本不是什么打打杀杀的插件，这是一个温柔的插件，所以需要一个温柔的攻击音效。默认的攻击声音是鹦鹉学史莱姆的声音，史莱姆软软萌萌的多温柔啊，很适合作为这个插件的攻击音效，你能想象那种啪啪啪的声音吗？如果你有其他更好的声音，也可以通过下方的设置来更改，甚至使用材质包中的声音：
```yaml
# 音效设置
sound:
  # 攻击时的声音
  attack: 'minecraft:entity.parrot.imitate.slime'
  # 射出时的声音
  shoot: 'minecraft:block.slime_block.step'
  # 没东西射的声音
  shoot-no-exp: 'minecraft:entity.llama.eat'
  # 吃掉经验时的声音
  eat: 'minecraft:entity.generic.drink'
```