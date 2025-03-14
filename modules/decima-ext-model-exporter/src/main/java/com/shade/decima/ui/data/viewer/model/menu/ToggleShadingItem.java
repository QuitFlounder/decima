package com.shade.decima.ui.data.viewer.model.menu;

import com.shade.decima.model.viewer.MeshViewerCanvas;
import com.shade.platform.ui.menus.MenuItem;
import com.shade.platform.ui.menus.MenuItemContext;
import com.shade.platform.ui.menus.MenuItemRegistration;
import com.shade.util.NotNull;

import static com.shade.decima.ui.menu.MenuConstants.*;

@MenuItemRegistration(parent = BAR_MODEL_VIEWER_ID, icon = "Action.shadingIcon", description = "Toggle flat/smooth shading", group = BAR_MODEL_VIEWER_GROUP_RENDER, order = 1000)
public class ToggleShadingItem extends MenuItem implements MenuItem.Check {
    @Override
    public void perform(@NotNull MenuItemContext ctx) {
        final MeshViewerCanvas canvas = ctx.getData(MeshViewerCanvas.CANVAS_KEY);
        canvas.setSoftShading(!canvas.isSoftShading());
    }

    @Override
    public boolean isChecked(@NotNull MenuItemContext ctx) {
        return ctx.getData(MeshViewerCanvas.CANVAS_KEY).isSoftShading();
    }
}
