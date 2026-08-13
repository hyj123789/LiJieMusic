# 分支介绍
不同于其他项目，本项目的两个分支 main分支（用XML语言写）和 compose分支（逐步用compose重构）都是主分支。

# 注意
因为本仓库两个分支有几乎完全独立的两套代码，所以不要在同一个文件夹下把两个分支都拉下来，那样会导致物理磁盘混乱
git clone -b 分支名 仓库地址
git clone -b compose https://github.com/hyj123789/LiJieMusic.git

# compose分支
这个分支是为了学习compose而存在的，这是学习笔记 https://gcnefx3i7hsd.feishu.cn/docx/CfgddmgfnoKAM7xFSGkcb2Rknhg?from=from_copylink
目前项目正处于重构的第一个阶段，用composeView重构（只重构了UI层），导航以及业务逻辑的代码能完全复用之前的。等UI重构完后面可以考虑完全用compose，然后导航用navigation3