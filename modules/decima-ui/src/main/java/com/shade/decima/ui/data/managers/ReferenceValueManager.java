package com.shade.decima.ui.data.managers;

import com.shade.decima.model.rtti.objects.RTTIReference;
import com.shade.decima.ui.data.ValueController;
import com.shade.decima.ui.data.ValueEditor;
import com.shade.decima.ui.data.ValueManager;
import com.shade.decima.ui.data.editors.ReferenceValueEditor;
import com.shade.decima.ui.data.registry.ValueHandlerRegistration.Selector;
import com.shade.decima.ui.data.registry.ValueHandlerRegistration.Type;
import com.shade.decima.ui.data.registry.ValueManagerRegistration;
import com.shade.util.NotNull;

@ValueManagerRegistration({
    @Selector(type = @Type(type = RTTIReference.class))
})
public class ReferenceValueManager implements ValueManager<RTTIReference> {
    @NotNull
    @Override
    public ValueEditor<RTTIReference> createEditor(@NotNull ValueController<RTTIReference> controller) {
        if (controller.getEditType() == ValueController.EditType.DIALOG) {
            return new ReferenceValueEditor(controller);
        } else {
            throw new IllegalArgumentException("Unsupported edit type: " + controller.getEditType());
        }
    }

    @Override
    public boolean canEdit(@NotNull ValueController.EditType type) {
        return type == ValueController.EditType.DIALOG;
    }
}
