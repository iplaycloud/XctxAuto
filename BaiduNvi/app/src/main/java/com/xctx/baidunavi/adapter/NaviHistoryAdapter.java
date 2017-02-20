package com.xctx.baidunavi.adapter;

import java.util.ArrayList;

import com.xctx.baidunavi.R;
import com.xctx.baidunavi.model.NaviHistory;
import com.xctx.baidunavi.model.NaviHistoryDbHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NaviHistoryAdapter extends BaseAdapter {
	private ArrayList<NaviHistory> naviArray;

	private LayoutInflater layoutInflater;

	private NaviHistoryDbHelper naviDb;

	public NaviHistoryAdapter(Context context, ArrayList<NaviHistory> naviArray) {
		super();
		this.naviArray = naviArray;
		layoutInflater = LayoutInflater.from(context);
		naviArray = new ArrayList<NaviHistory>();

		naviDb = new NaviHistoryDbHelper(context);

	}

	@Override
	public int getCount() {
		return naviArray.size();
	}

	@Override
	public Object getItem(int position) {
		return naviArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class MyViewHolder {
		ImageView imageSignal;
		TextView textKey;
		RelativeLayout layoutDelete;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final MyViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyViewHolder();
			convertView = layoutInflater.inflate(
					R.layout.navi_history_list_item, null);

			viewHolder.imageSignal = (ImageView) convertView
					.findViewById(R.id.imageSignal);
			viewHolder.textKey = (TextView) convertView
					.findViewById(R.id.textKey);

			viewHolder.layoutDelete = (RelativeLayout) convertView
					.findViewById(R.id.layoutDelete);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}

		// Key
		String naviKey = naviArray.get(position).getKey();
		// City
		String naviCity = naviArray.get(position).getCity();
		if (naviCity != null && naviCity.trim().length() > 0) {
			viewHolder.textKey.setText(naviKey + "(" + naviCity + ")");
		} else {
			viewHolder.textKey.setText(naviKey);
		}

		viewHolder.layoutDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 从数据库中删除
				naviDb.deleteNaviHistoryById(naviArray.get(position).getId());
				remove(naviArray.get(position));
			}
		});

		return convertView;
	}

	public void remove(Object item) {
		naviArray.remove(item);
		notifyDataSetChanged();
	}

}
