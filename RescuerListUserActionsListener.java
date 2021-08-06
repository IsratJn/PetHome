
package com.example.pethome.storeapp.ui.rescuers;

import com.example.pethome.storeapp.data.local.models.RescuerLite;

public interface RescuerListUserActionsListener {
    void onEditRescuer(final int itemPosition, RescuerLite rescuer);
    void onDeleteRescuer(final int itemPosition, RescuerLite rescuer);
    void onDefaultPhoneClicked(final int itemPosition, RescuerLite rescuer);
    void onDefaultEmailClicked(final int itemPosition, RescuerLite rescuer);
}
