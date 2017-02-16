package org.ei.opensrp.path.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ei.opensrp.path.R;

/**
 * Created by Jason Rogena - jrogena@ona.io on 16/02/2017.
 */

public class ChildImmunizationActivity extends BaseActivity {

    private static enum Gender {
        MALE,
        FEMALE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getActivityTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChildImmunizationActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGenderViews(Gender.MALE);
    }

    private String getActivityTitle() {
        //TODO: get the child's name
        return String.format("%s > %s", getString(R.string.app_name), "Child Name");
    }

    /**
     * Updates all gender affected views
     */
    private void updateGenderViews(Gender gender) {
        int darkShade = R.color.silver;
        int lightSade = R.color.white;
        String identifier = getString(R.string.neutral_sex_id);

        if (gender.equals(Gender.FEMALE)) {
            darkShade = R.color.female_pink;
            lightSade = R.color.female_light_pink;
            identifier = getString(R.string.female_sex_id);
        } else if (gender.equals(Gender.MALE)) {
            darkShade = R.color.male_blue;
            lightSade = R.color.male_light_blue;
            identifier = getString(R.string.male_sex_id);
        }

        getToolbar().setBackground(new ColorDrawable(getResources().getColor(darkShade)));
        ScrollView contentBase = (ScrollView) findViewById(R.id.content_base);
        contentBase.setBackground(new ColorDrawable(getResources().getColor(lightSade)));
        TextView childSiblingsTV = (TextView) findViewById(R.id.child_siblings_tv);
        childSiblingsTV.setText(
                String.format(getString(R.string.child_siblings), identifier).toUpperCase());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_child_immunization;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }
}
