package com.xctx.baidunavi.ui.activity;

import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanPreference;
import com.xctx.baidunavi.Constant;
import com.xctx.baidunavi.R;
import com.xctx.baidunavi.util.MyLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SetRoutePlanPreferenceActivity extends Activity {

	private RadioGroup routePlanGroup;
	private RadioButton routePlanRecommend; // 推荐
	private RadioButton routePlanMinTime; // 高速优先
	private RadioButton routePlanMinDist; // 少走高速
	private RadioButton routePlanMinToll; // 少收费
	private RadioButton routePlanAvoidJam; // 躲避拥堵

	private TextView textUpdateSD, textUpdateOL;

	private SharedPreferences preference;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_route_plan_preference);

		preference = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		editor = preference.edit();

		initialLayout();
		initialRadioButton();
	}

	private void initialLayout() {
		routePlanGroup = (RadioGroup) findViewById(R.id.routePlanGroup);
		routePlanGroup
				.setOnCheckedChangeListener(new MyRadioOnCheckedListener());

		routePlanRecommend = (RadioButton) findViewById(R.id.routePlanRecommend);
		routePlanMinTime = (RadioButton) findViewById(R.id.routePlanMinTime);
		routePlanMinDist = (RadioButton) findViewById(R.id.routePlanMinDist);
		routePlanMinToll = (RadioButton) findViewById(R.id.routePlanMinToll);
		routePlanAvoidJam = (RadioButton) findViewById(R.id.routePlanAvoidJam);

		textUpdateSD = (TextView) findViewById(R.id.textUpdateSD);
		textUpdateOL = (TextView) findViewById(R.id.textUpdateOL);
		textUpdateSD.setOnClickListener(new MyOnClickListener());
		textUpdateOL.setOnClickListener(new MyOnClickListener());

	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.textUpdateSD:
				Intent intentOffline = new Intent(
						SetRoutePlanPreferenceActivity.this,
						OfflineBaiduMapUpdateActivity.class);
				startActivity(intentOffline);
				finish();
				break;

			case R.id.textUpdateOL:
				Intent intentUpdate = new Intent(
						SetRoutePlanPreferenceActivity.this,
						OfflineBaiduMapActivity.class);
				startActivity(intentUpdate);
				finish();
				break;

			default:
				break;
			}
		}

	}

	private void initialRadioButton() {
		int nowMode = getRoutePlanPref();
		switch (nowMode) {
		case RoutePlanPreference.ROUTE_PLAN_MOD_MIN_TIME:
			routePlanMinTime.setChecked(true);
			break;

		case RoutePlanPreference.ROUTE_PLAN_MOD_MIN_DIST:
			routePlanMinDist.setChecked(true);
			break;

		case RoutePlanPreference.ROUTE_PLAN_MOD_MIN_TOLL:
			routePlanMinToll.setChecked(true);
			break;

		case RoutePlanPreference.ROUTE_PLAN_MOD_AVOID_TAFFICJAM:
			routePlanAvoidJam.setChecked(true);
			break;

		case RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND:
		default:
			routePlanRecommend.setChecked(true);
			break;
		}
	}

	class MyRadioOnCheckedListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.routePlanMinTime:
				saveRoutePlanPrefToSP(RoutePlanPreference.ROUTE_PLAN_MOD_MIN_TIME);
				break;

			case R.id.routePlanMinDist:
				saveRoutePlanPrefToSP(RoutePlanPreference.ROUTE_PLAN_MOD_MIN_DIST);
				break;

			case R.id.routePlanMinToll:
				saveRoutePlanPrefToSP(RoutePlanPreference.ROUTE_PLAN_MOD_MIN_TOLL);
				break;

			case R.id.routePlanAvoidJam:
				saveRoutePlanPrefToSP(RoutePlanPreference.ROUTE_PLAN_MOD_AVOID_TAFFICJAM);
				break;

			case R.id.routePlanRecommend:
			default:
				saveRoutePlanPrefToSP(RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND);
				break;
			}
		}
	}

	private void saveRoutePlanPrefToSP(int mode) {
		MyLog.v("[saveRoutePlanPrefToSP]mode:" + mode);
		editor.putInt(Constant.SPString.ROUTE_PLAN_PREF, mode);
		editor.commit();
	}

	private int getRoutePlanPref() {
		return preference.getInt(Constant.SPString.ROUTE_PLAN_PREF,
				RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND);
	}

}
