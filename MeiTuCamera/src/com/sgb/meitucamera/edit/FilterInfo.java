package com.sgb.meitucamera.edit;

import android.graphics.Bitmap;

import com.sgb.meitucamera.imageFilter.IImageFilter;

public class FilterInfo {

	public Bitmap bitmapIcon;
	public boolean isSelect;

	public FilterInfo(Bitmap bitmapIcon,boolean isSelect) {
		this.bitmapIcon = bitmapIcon;
		this.isSelect = isSelect;
	}

}
