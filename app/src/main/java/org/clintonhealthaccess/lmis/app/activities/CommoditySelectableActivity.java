package org.clintonhealthaccess.lmis.app.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.activities.viewmodels.BaseCommodityViewModel;
import org.clintonhealthaccess.lmis.app.activities.viewmodels.CommoditiesToViewModelsConverter;
import org.clintonhealthaccess.lmis.app.adapters.strategies.CommodityDisplayStrategy;
import org.clintonhealthaccess.lmis.app.events.CommodityToggledEvent;
import org.clintonhealthaccess.lmis.app.fragments.ItemSelectFragment;
import org.clintonhealthaccess.lmis.app.models.Category;
import org.clintonhealthaccess.lmis.app.services.CategoryService;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import roboguice.inject.InjectView;

import static android.view.View.OnClickListener;
import static com.google.common.collect.Lists.newArrayList;

abstract public class CommoditySelectableActivity extends BaseActivity {
    @InjectView(R.id.gridViewSelectedCommodities)
    GridView gridViewSelectedCommodities;
    ArrayAdapter arrayAdapter;
    ArrayList<BaseCommodityViewModel> selectedCommodities = newArrayList();
    @Inject
    private CategoryService categoryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.transparent);
        setContentView(getLayoutId());
        setupCategories();

        arrayAdapter = getArrayAdapter();
        gridViewSelectedCommodities.setAdapter(arrayAdapter);

        afterCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    public void onEvent(CommodityToggledEvent event) {
        BaseCommodityViewModel commodity = event.getCommodity();
        if (selectedCommodities.contains(commodity)) {
            selectedCommodities.remove(commodity);
            arrayAdapter.remove(commodity);
        } else {
            arrayAdapter.add(commodity);
            selectedCommodities.add(commodity);
        }
        onCommoditySelectionChanged(selectedCommodities);
    }

    protected void onEachSelectedCommodity(SelectedCommodityHandler handler) {
        for (int i = 0; i < gridViewSelectedCommodities.getChildCount(); i++) {
            View view = gridViewSelectedCommodities.getChildAt(i);
            BaseCommodityViewModel commodityViewModel = (BaseCommodityViewModel) gridViewSelectedCommodities.getAdapter().getItem(i);
            handler.operate(view, commodityViewModel);
        }
    }

    protected void onCommoditySelectionChanged(List<BaseCommodityViewModel> selectedCommodities) {
        Button submitButton = getSubmitButton();
        if (selectedCommodities.size() > 0) {
            submitButton.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.INVISIBLE);
        }
    }

    abstract protected Button getSubmitButton();

    abstract protected int getLayoutId();

    abstract protected CommodityDisplayStrategy getCheckBoxVisibilityStrategy();

    abstract protected ArrayAdapter getArrayAdapter();

    abstract protected void afterCreate(Bundle savedInstanceState);

    abstract protected CommoditiesToViewModelsConverter getViewModelConverter();

    private void setupCategories() {
        Drawable commodityButtonBackground = createCommodityButtonBackground();
        LinearLayout categoriesLayout = (LinearLayout) findViewById(R.id.layoutCategories);
        for (final Category category : categoryService.all()) {
            Button button = createCommoditySelectionButton(category, commodityButtonBackground);
            categoriesLayout.addView(button);
        }
    }

    private Drawable createCommodityButtonBackground() {
        Drawable commodityButtonBackground = getResources().getDrawable(R.drawable.arrow_black_right);
        commodityButtonBackground.setBounds(0, 0, 20, 30);
        return commodityButtonBackground;
    }

    private Button createCommoditySelectionButton(final Category category, Drawable background) {
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.category_button_on_overlay);
        button.setCompoundDrawables(null, null, background, null);
        button.setText(category.getName());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ItemSelectFragment dialog = ItemSelectFragment.newInstance(category, selectedCommodities,
                        getCheckBoxVisibilityStrategy(), getViewModelConverter());
                dialog.show(fragmentManager, "selectCommodities");
            }
        });
        return button;
    }

    protected interface SelectedCommodityHandler {
        void operate(View view, BaseCommodityViewModel commodityViewModel);
    }
}
