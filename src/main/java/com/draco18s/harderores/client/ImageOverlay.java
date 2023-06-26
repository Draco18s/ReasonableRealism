package com.draco18s.harderores.client;

import com.mojang.blaze3d.platform.NativeImage;

import dev.lukebemish.dynamicassetgenerator.impl.client.NativeImageHelper;
import dev.lukebemish.dynamicassetgenerator.impl.client.palette.ColorHolder;

public class ImageOverlay {
	public static NativeImage combinedImage(NativeImage rImg, NativeImage oImg) {
		int rDim = Math.min(rImg.getHeight(),rImg.getWidth());
        int oDim = Math.min(oImg.getHeight(),oImg.getWidth());
        int w = Math.max(rDim, oDim);
        int rs = w/rImg.getWidth();
        int os = w/oImg.getWidth();
        NativeImage outImg = NativeImageHelper.of(NativeImage.Format.RGBA,w,w,false);
        
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < w; y++) {
            	ColorHolder overlayC = ColorHolder.fromColorInt(oImg.getPixelRGBA(x/os,y/os));
            	ColorHolder oreC = ColorHolder.fromColorInt(rImg.getPixelRGBA(x/rs,y/rs));
            	
            	ColorHolder outVal;
            	if(overlayC.getA() < 0.1) {
            		outVal = overlayC;
            	}
            	else {
            		outVal = oreC;
            	}
                outImg.setPixelRGBA(x,y,ColorHolder.toColorInt(outVal));
            }
        }
        return outImg;
	}
}
