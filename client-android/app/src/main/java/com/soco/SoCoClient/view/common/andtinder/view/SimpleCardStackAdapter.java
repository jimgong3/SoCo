package com.soco.SoCoClient.view.common.andtinder.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.andtinder.R;
//import com.andtinder.model.CardModel;
import com.soco.SoCoClient.R;
import com.soco.SoCoClient.view.common.andtinder.model.CardModel;

public final class SimpleCardStackAdapter extends CardStackAdapter {

	public SimpleCardStackAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.std_card_inner, parent, false);
			assert convertView != null;
		}

		((ImageView) convertView.findViewById(R.id.banner)).setImageDrawable(model.getCardImageDrawable());
		((TextView) convertView.findViewById(R.id.title)).setText(model.getTitle());
		((TextView) convertView.findViewById(R.id.description)).setText(model.getDescription());

		return convertView;
	}
}