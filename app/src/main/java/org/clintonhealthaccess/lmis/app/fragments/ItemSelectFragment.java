package org.clintonhealthaccess.lmis.app.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.adapters.CommoditiesAdapter;
import org.clintonhealthaccess.lmis.app.models.Category;
import org.clintonhealthaccess.lmis.app.persistence.CommoditiesRepository;

import java.util.List;

import roboguice.fragment.RoboDialogFragment;

public class ItemSelectFragment extends RoboDialogFragment {
    private static final String CATEGORY = "param_category";

    @Inject
    CommoditiesRepository commoditiesRepository;

    private Category category;

    ListView listViewCommodities;


    private LinearLayout categoriesLayout;

    public ItemSelectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = (Category) getArguments().getSerializable(CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupDialog();

        final View overlayView = inflater.inflate(R.layout.fragment_item_select, container, false);


        categoriesLayout = (LinearLayout) overlayView.findViewById(R.id.itemSelectOverlayCategories);

        listViewCommodities = (ListView) overlayView.findViewById(R.id.listViewCommodities);

        List<Category> categoryList = commoditiesRepository.allCategories();


        for (final Category category : categoryList) {
            Button button = new CategoryButton(getActivity(), category);

            button.setBackgroundResource(R.drawable.category_button_on_overlay);

            button.setText(category.getName());

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCommodities(category);
                }
            });

            categoriesLayout.addView(button);
        }

        showCommodities(category);
        return overlayView;
    }

    private void setupDialog() {
        Window window = getDialog().getWindow();

        window.setGravity(Gravity.TOP | Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 200;
        params.y = 50;
        window.setAttributes(params);

        getDialog().setCanceledOnTouchOutside(false);
    }

    private void showCommodities(Category currentCategory) {
        final CommoditiesAdapter adapter = new CommoditiesAdapter(getActivity(), R.layout.commodity_list_item, currentCategory.getCommodities());
        listViewCommodities.setAdapter(adapter);
        listViewCommodities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.getItem(i).toggleSelected();
                adapter.notifyDataSetChanged();
            }
        });

        for (int i = 0; i < categoriesLayout.getChildCount(); i++) {
            CategoryButton button = (CategoryButton) categoriesLayout.getChildAt(i);
            if (button.isOf(currentCategory)) {
                  button.setSelected(true);
            } else {
                  button.setSelected(false);
            }
        }
    }

    public static ItemSelectFragment newInstance(Category category) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(CATEGORY, category);

        ItemSelectFragment fragment = new ItemSelectFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

}