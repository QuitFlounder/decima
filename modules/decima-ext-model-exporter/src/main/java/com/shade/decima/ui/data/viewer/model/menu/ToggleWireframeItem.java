package com.shade.decima.ui.data.viewer.model.menu;

import com.shade.decima.model.viewer.MeshViewerCanvas;
import com.shade.platform.ui.menus.MenuItem;
import com.shade.platform.ui.menus.MenuItemContext;
import com.shade.platform.ui.menus.MenuItemRegistration;
import com.shade.util.NotNull;

import static com.shade.decima.ui.menu.MenuConstants.*;

@MenuItemRegistration(parent = BAR_MODEL_VIEWER_ID, icon = "Action.wireframeIcon", description = "Toggle wireframe rendering", group = BAR_MODEL_VIEWER_GROUP_RENDER, order = 2000)
public class ToggleWireframeItem extends MenuItem implements MenuItem.Check {
    @Override
    public void perform(@NotNull MenuItemContext ctx) {
        final MeshViewerCanvas canvas = ctx.getData(MeshViewerCanvas.CANVAS_KEY);
        canvas.setShowWireframe(!canvas.isShowWireframe());
    }

    @Override
    public boolean isChecked(@NotNull MenuItemContext ctx) {
        return ctx.getData(MeshViewerCanvas.CANVAS_KEY).isShowWireframe();
    }
}
