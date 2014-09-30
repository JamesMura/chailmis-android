/*
 * Copyright (c) 2014, Thoughtworks Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package org.clintonhealthaccess.lmis.app.views.graphs;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.clintonhealthaccess.lmis.app.R;

public class StockOnHandGraphBar {
    private String commodityName;
    private int maximumThreshold, minimumThreshold, stockOnHand, color;

    public StockOnHandGraphBar(String commodityName, int min, int max, int soh, int color) {
        this.commodityName = commodityName;
        this.minimumThreshold = min;
        this.maximumThreshold = max;
        this.stockOnHand = soh;
        this.color = color;
    }

    public RelativeLayout getView(Context applicationContext) {
        int barWidth = Math.round(applicationContext.getResources().getDimension(R.dimen.bar_width));


        RelativeLayout relativeLayout = new RelativeLayout(applicationContext);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
        relativeLayout.setPadding(5, 5, 5, 5);

        TextView textViewName = new TextView(applicationContext);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(barWidth, barWidth);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textViewName.setLayoutParams(params2);
        textViewName.setText(commodityName.toUpperCase());
        textViewName.setId(R.id.textViewCommodityname);
//        textViewName.setRotation(270);
        textViewName.setGravity(Gravity.CENTER_HORIZONTAL);
        textViewName.setTextColor(applicationContext.getResources().getColor(R.color.black));
        textViewName.setTextSize(12);
        textViewName.setTypeface(null, Typeface.BOLD);

        relativeLayout.addView(textViewName);

        View colorHolderView = new View(applicationContext);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(barWidth, getHeightForHolder());
        params1.addRule(RelativeLayout.ABOVE, R.id.textViewCommodityname);
        colorHolderView.setLayoutParams(params1);
        colorHolderView.setBackgroundColor(color);

        relativeLayout.addView(colorHolderView);

        ImageView imageViewMinimumThreshold = new ImageView(applicationContext);
        imageViewMinimumThreshold.setImageDrawable(applicationContext.getResources().getDrawable(R.drawable.dotted));
        imageViewMinimumThreshold.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(barWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.textViewCommodityname);
        params.setMargins(0, 0, 0, getHeightForMinThreshold());
        imageViewMinimumThreshold.setLayoutParams(params);

        relativeLayout.addView(imageViewMinimumThreshold);

        ImageView imageViewMaximumThreshold = new ImageView(applicationContext);
        imageViewMaximumThreshold.setImageDrawable(applicationContext.getResources().getDrawable(R.drawable.dotted));
        imageViewMaximumThreshold.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        RelativeLayout.LayoutParams maxParams = new RelativeLayout.LayoutParams(barWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        maxParams.setMargins(0, 0, 0, getHeightForMaxThreshold());
        maxParams.addRule(RelativeLayout.ABOVE, R.id.textViewCommodityname);
        imageViewMaximumThreshold.setLayoutParams(maxParams);
        imageViewMaximumThreshold.setId(R.id.imageMaximumThreshold);

        relativeLayout.addView(imageViewMaximumThreshold);

        TextView textViewMax = new TextView(applicationContext);
        RelativeLayout.LayoutParams maxTextViewParams = new RelativeLayout.LayoutParams(barWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        maxTextViewParams.addRule(RelativeLayout.ABOVE, R.id.imageMaximumThreshold);
        textViewMax.setLayoutParams(maxTextViewParams);
        textViewMax.setText("Max");
        textViewMax.setGravity(Gravity.CENTER_HORIZONTAL);
        textViewMax.setTextColor(applicationContext.getResources().getColor(R.color.white));
        textViewMax.setTypeface(null, Typeface.BOLD);

        relativeLayout.addView(textViewMax);

        TextView textViewSOH = new TextView(applicationContext);
        RelativeLayout.LayoutParams textViewSOHParams = new RelativeLayout.LayoutParams(barWidth, barWidth);
        textViewSOHParams.addRule(RelativeLayout.ABOVE, R.id.textViewCommodityname);
        textViewSOH.setLayoutParams(textViewSOHParams);
        textViewSOH.setText(getSOH());
        textViewSOH.setGravity(Gravity.CENTER_HORIZONTAL);
        textViewSOH.setTextSize(12);
        textViewSOH.setTypeface(null, Typeface.BOLD);
        textViewSOH.setTextColor(applicationContext.getResources().getColor(R.color.white));

        relativeLayout.addView(textViewSOH);

        return relativeLayout;
    }

    private String getSOH() {
        return "SOH  " + stockOnHand;
    }

    private int getHeightForMinThreshold() {
        return minimumThreshold;
    }

    private int getHeightForMaxThreshold() {
        return maximumThreshold;
    }

    private int getHeightForHolder() {
        return stockOnHand;
    }
}
