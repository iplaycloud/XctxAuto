package com.tchip.carlauncher.model;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

import com.tchip.carlauncher.util.MyLog;

public class Typefaces {
	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

	public static Typeface get(Context c, String assetPath) {
		synchronized (cache) {
			if (!cache.containsKey(assetPath)) {
				try {
					Typeface t = Typeface.createFromAsset(c.getAssets(),
							assetPath);
					cache.put(assetPath, t);
				} catch (Exception e) {
					MyLog.e("Typefaces：Could not get typeface '" + assetPath
							+ "' because " + e.getMessage());
					return null;
				}
			}

			return cache.get(assetPath);
		}
	}
}