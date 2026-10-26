package com.draco18s.harderores.client;

import com.mojang.blaze3d.platform.NativeImage;

public class ImageOverlay {
	/**
	 * The ore texture where the overlay is opaque, transparent where it isn't. The result is as large as the larger
	 * of the two (by their smaller sides, so an animated strip counts as one frame); the other is scaled up to match.
	 */
	public static NativeImage combinedImage(NativeImage rImg, NativeImage oImg) {
		int rDim = Math.min(rImg.getHeight(),rImg.getWidth());
		int oDim = Math.min(oImg.getHeight(),oImg.getWidth());
		int w = Math.max(rDim, oDim);
		int rs = Math.max(w/rImg.getWidth(), 1);
		int os = Math.max(w/oImg.getWidth(), 1);
		NativeImage outImg = new NativeImage(NativeImage.Format.RGBA, w, w, false);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < w; y++) {
				int overlayC = oImg.getPixel(x/os,y/os);
				// Alpha below 0.1: that part of the ore shows through.
				if((overlayC >>> 24) < 26) {
					outImg.setPixel(x, y, overlayC);
				}
				else {
					outImg.setPixel(x, y, rImg.getPixel(x/rs,y/rs));
				}
			}
		}
		return outImg;
	}
}
