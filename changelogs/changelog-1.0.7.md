## 加速渲染 1.0.7-1.21.1 ALPHA
- 添加了``过滤器配置 > 启用容器GUI过滤器``选项以阻止部分在渲染加速后会导致需渲染异常的容器GUI被加速, 提供更好的兼容性.
- 修复了玩家在物品栏GUI中以错误光照渲染导致的渲染异常.
- 修复了超出标准范围UV的面在静态剔除中被错误剔除导致的渲染异常.

## Accelerated Rendering 1.0.7-1.21.1 ALPHA
- Adds ``Filter Settings > Enable Menu Filter`` to filter menus that will cause glitches when accelerated.
- Fixes visual glitches on players rendered with wrong light direction in inventory GUI.
- Fixes visual glitches on static culler exceptionally culled geometries with UV outside regular range.