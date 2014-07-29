package org.clintonhealthaccess.lmis.app.watchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.app.activities.viewmodels.BaseCommodityViewModel;
import org.clintonhealthaccess.lmis.app.services.StockService;

import roboguice.RoboGuice;

import static org.clintonhealthaccess.lmis.app.utils.ViewHelpers.getIntFromString;

public class QuantityTextWatcher implements TextWatcher {
    private final EditText editTextQuantity;
    private final BaseCommodityViewModel commodityViewModel;

    @Inject
    StockService stockService;

    public QuantityTextWatcher(EditText editTextQuantity, BaseCommodityViewModel commodityViewModel) {
        this.editTextQuantity = editTextQuantity;
        this.commodityViewModel = commodityViewModel;
        RoboGuice.getInjector(editTextQuantity.getContext()).injectMembers(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String value = editable.toString();
        if (!value.isEmpty()) {
            int quantity = getIntFromString(value);
            Log.i("Entered", String.format("%d", quantity));
            Log.i("Entered", String.format("%s", value));
            int stock_level = stockService.getStockLevelFor(commodityViewModel.getCommodity());
            commodityViewModel.setQuantityEntered(quantity);
            if (quantity > stock_level) {
                editTextQuantity.setError(String.format("The quantity entered is greater than Stock available (%d)", stock_level));
            }
        }
    }
}
