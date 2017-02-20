package com.tchip.autosetting.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

public class TypefaceUtil {
	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

	public static Typeface get(Context context, String assetPath) {
		synchronized (cache) {
			if (!cache.containsKey(assetPath)) {
				try {
					Typeface typeface = Typeface.createFromAsset(
							context.getAssets(), assetPath);
					cache.put(assetPath, typeface);
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