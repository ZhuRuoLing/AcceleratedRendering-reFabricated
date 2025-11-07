## 加速渲染 1.0.2-1.21.1 ALPHA
- 添加了新的Shader Overrides API功能, 以实现更好MOD兼容性.
- 添加了新的Batching Layer API功能, 以实现更好的MOD兼容性.
- 添加了``ModelBlockRenderer#renderModel``的渲染加速以支持展示实体渲染加速.
- 添加了``加速物品渲染配置 > 启用手中物品加速``选项以支持加速在手中物品的渲染加速.
- 添加了``加速物品渲染配置 > 启用GUI物品加速``选项以支持加速在GUI中物品的渲染加速.
- 添加了``加速物品渲染配置 > 启用GUI物品合批``选项以支持合并GUI中物品的绘制, 更好的支持在GUI中物品的渲染加速.
- 添加了``核心配置 > 合批层储存类型``选项以控制是否将半透明和不透明模型合并渲染
- 添加了``过滤器设置 > 启用物品过滤器``选项以阻止部分在渲染加速后会导致需渲染异常的物品被加速, 提供更好的兼容性.
- 修复了在Iris环境下开启部分光影包时半透明模型与不透明模型合并渲染导致的半透明模型渲染异常
- 修复了在ImmediatelyFast环境下渲染HUD时导致的渲染异常.
- 修复了在机械动力环境下实体与机械动力方块/结构一同出现时导致的渲染异常.

## Accelerated Rendering 1.0.2-1.21.1 ALPHA
- Adds Shader Overrides API to achieve better MODs compatibility.
- Adds Batching Layer API to achieve better MODs compatibility.
- Adds ``ModelBlockRenderer#renderModel`` acceleration support to accelerate rendering of display entities.
- Adds ``Accelerated Item Rendering Settings > Enable Hand Acceleration`` to support the acceleration of items rendering in hands.
- Adds ``Accelerated Item Rendering Settings > Enable GUI Acceleration`` to support the acceleration of items rendering in GUI.
- Adds ``Accelerated Item Rendering Settings > Enable GUI Item Batching`` to support batching draw calls of items in GUI to ensure better acceleration of item rendering in GUI.
- Adds ``Core Settings > Batching Layer Storage Type`` to control if the pipeline should combine the rendering of opaque and translucent geometries together.
- Adds ``Filter Settings > Enable Item Filter`` to filter items that will cause glitches when accelerated.
- Fixes visual glitches on translucent geometries with specific shader pack due to combined rendering of opaque and translucent geometries when Iris is installed.
- Fixes visual glitches on HUD rendering when ImmediatelyFast is installed.
- Fixes visual glitches when entities and flywheel accelerated geometries rendering on screen at the same time when Create is installed.