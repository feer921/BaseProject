package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import common.base.R;
import common.base.utils.Util;


/**
 * This is a slider with a description TextView.
 */
public class TextSliderView extends BaseSliderView{
    public TextSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text,null);
        ImageView target = (ImageView)v.findViewById(R.id.daimajia_slider_image);
        LinearLayout descLayout = (LinearLayout) v.findViewById(R.id.description_layout);
        String descText = getDescription();
        if (Util.isEmpty(descText)) {
            descLayout.setVisibility(View.GONE);
        }
        else{
            TextView description = (TextView)v.findViewById(R.id.description);
            description.setText(getDescription());
        }
        bindEventAndShow(v, target);
        return v;
    }
}
