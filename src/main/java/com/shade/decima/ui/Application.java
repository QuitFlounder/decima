package com.shade.decima.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.shade.decima.cli.ApplicationCLI;
import com.shade.decima.model.app.Workspace;
import com.shade.decima.ui.menu.MenuConstants;
import com.shade.platform.model.Lazy;
import com.shade.platform.ui.menus.MenuService;
import com.shade.platform.ui.util.UIUtils;
import com.shade.util.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.prefs.Preferences;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final Lazy<MenuService> menuService = Lazy.of(MenuService::new);

    private static ApplicationFrame frame;

    public static void main(String[] args) {
        final Workspace workspace = new Workspace();

        if (args.length > 0) {
            ApplicationCLI.execute(workspace, args);
        }

        SwingUtilities.invokeLater(() -> {
            FlatLaf.registerCustomDefaultsSource("themes");
            FlatInspector.install("ctrl shift alt X");
            FlatUIDefaultsInspector.install("ctrl shift alt Y");

            setLookAndFeel(workspace.getPreferences());

            UIManager.put("Navigator.archiveIcon", new FlatSVGIcon("icons/nodes/archive.svg"));
            UIManager.put("Navigator.binaryIcon", new FlatSVGIcon("icons/nodes/binary.svg"));
            UIManager.put("Navigator.coreIcon", new FlatSVGIcon("icons/nodes/core.svg"));
            UIManager.put("Editor.editIcon", new FlatSVGIcon("icons/edit.svg"));
            UIManager.put("Editor.editModalIcon", new FlatSVGIcon("icons/edit_modal.svg"));
            UIManager.put("Editor.exportIcon", new FlatSVGIcon("icons/export.svg"));
            UIManager.put("Editor.importIcon", new FlatSVGIcon("icons/import.svg"));
            UIManager.put("Editor.packIcon", new FlatSVGIcon("icons/pack.svg"));
            UIManager.put("Editor.undoIcon", new FlatSVGIcon("icons/undo.svg"));
            UIManager.put("Editor.redoIcon", new FlatSVGIcon("icons/redo.svg"));
            UIManager.put("Editor.saveIcon", new FlatSVGIcon("icons/save.svg"));
            UIManager.put("Editor.searchIcon", new FlatSVGIcon("icons/search.svg"));
            UIManager.put("Editor.closeIcon", new FlatSVGIcon("icons/tab_close.svg"));
            UIManager.put("Editor.closeAllIcon", new FlatSVGIcon("icons/tab_close_all.svg"));
            UIManager.put("Editor.closeOthersIcon", new FlatSVGIcon("icons/tab_close_others.svg"));
            UIManager.put("Editor.closeUninitializedIcon", new FlatSVGIcon("icons/tab_close_uninitialized.svg"));
            UIManager.put("Editor.splitRightIcon", new FlatSVGIcon("icons/split_right.svg"));
            UIManager.put("Editor.splitDownIcon", new FlatSVGIcon("icons/split_down.svg"));
            UIManager.put("Editor.zoomInIcon", new FlatSVGIcon("icons/zoom_in.svg"));
            UIManager.put("Editor.zoomOutIcon", new FlatSVGIcon("icons/zoom_out.svg"));
            UIManager.put("Editor.zoomFitIcon", new FlatSVGIcon("icons/zoom_fit.svg"));
            UIManager.put("CoreEditor.decimalIcon", new FlatSVGIcon("icons/nodes/decimal.svg"));
            UIManager.put("CoreEditor.integerIcon", new FlatSVGIcon("icons/nodes/integer.svg"));
            UIManager.put("CoreEditor.stringIcon", new FlatSVGIcon("icons/nodes/string.svg"));
            UIManager.put("CoreEditor.booleanIcon", new FlatSVGIcon("icons/nodes/boolean.svg"));
            UIManager.put("CoreEditor.enumIcon", new FlatSVGIcon("icons/nodes/enum.svg"));
            UIManager.put("CoreEditor.uuidIcon", new FlatSVGIcon("icons/nodes/uuid.svg"));
            UIManager.put("CoreEditor.arrayIcon", new FlatSVGIcon("icons/nodes/array.svg"));
            UIManager.put("CoreEditor.objectIcon", new FlatSVGIcon("icons/nodes/object.svg"));
            UIManager.put("CoreEditor.referenceIcon", new FlatSVGIcon("icons/nodes/reference.svg"));
            UIManager.put("CoreEditor.addElementIcon", new FlatSVGIcon("icons/add_element.svg"));
            UIManager.put("CoreEditor.removeElementIcon", new FlatSVGIcon("icons/remove_element.svg"));
            UIManager.put("CoreEditor.duplicateElementIcon", new FlatSVGIcon("icons/duplicate_element.svg"));

            final MenuService menuService = getMenuService();

            frame = new ApplicationFrame(workspace);
            frame.setJMenuBar(menuService.createMenuBar(MenuConstants.APP_MENU_ID));
            frame.setIconImages(FlatSVGUtils.createWindowIconImages("/icons/application.svg"));

            menuService.createMenuKeyBindings(frame.getRootPane(), MenuConstants.APP_MENU_ID);

            Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
                UIUtils.showErrorDialog(getFrame(), exception);
                log.error("Unhandled exception", exception);
            });
        });
    }

    @Deprecated
    @NotNull
    public static ApplicationFrame getFrame() {
        return frame;
    }

    @NotNull
    public static MenuService getMenuService() {
        return menuService.get();
    }

    private static void setLookAndFeel(@NotNull Preferences prefs) {
        final String lafClassName = prefs.node("window").get("laf", FlatLightLaf.class.getName());

        try {
            UIManager.setLookAndFeel(lafClassName);
        } catch (Exception e) {
            log.error("Failed to setup look and feel '" + lafClassName + "'l: " + e);
        }
    }
}
