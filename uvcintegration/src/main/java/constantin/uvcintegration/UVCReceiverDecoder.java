package constantin.uvcintegration;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;

import java.io.File;

import constantin.video.core.player.DecodingInfo;
import constantin.video.core.player.VideoSettings;

// Handles receiving & decoding of UVC devices that supply MJPEG frames (like ROTG02)
public class UVCReceiverDecoder {
    private static final String TAG= "UVCReceiverDecoder";
    static{
        System.loadLibrary("UVCReceiverDecoder");
    }
    public long nativeInstance;
    private boolean alreadyStreaming=false;

    public UVCReceiverDecoder(final Context context){
        nativeInstance=nativeConstruct(getDirectoryToSaveDataTo(), VideoSettings.getVS_GROUND_RECORDING(context));
    }

    public void startReceiving(final Context context,final UsbDevice device,final UsbDeviceConnection connection){
        if(alreadyStreaming){
            Log.d(TAG,"startReceiving() already called");
            return;
        }
        final String deviceName = device.getDeviceName();
        Log.d(TAG,"Device name is "+deviceName);
        final String[] v = !TextUtils.isEmpty(deviceName) ? deviceName.split("/") : null;
        int busnum = 0;
        int devnum = 0;
        String usbfs_str= "/dev/bus/usb";
        if (v != null) {
            busnum = Integer.parseInt(v[v.length-2]);
            devnum = Integer.parseInt(v[v.length-1]);
            StringBuilder usbfs_str2 = new StringBuilder();
            for(int i=0;i<v.length-2;i++){
                usbfs_str2.append("/").append(v[i]);
            }
            usbfs_str=usbfs_str2.toString();
        }
        //
        int success=nativeStartReceiving(nativeInstance,device.getVendorId(),device.getProductId(),connection.getFileDescriptor(),busnum,devnum, usbfs_str);
        if(success==0){
            alreadyStreaming=true;
        }
    }

    public void stopReceiving(final Context context){
        final String filenamePath=nativeStopReceiving(nativeInstance,context);
        if(filenamePath!=null){
            System.out.println("FilenamePath "+filenamePath);
            //TODO TranscodeService.startTranscoding(context,filenamePath);
        }
        alreadyStreaming=false;
    }

    public static String getDirectoryToSaveDataTo(){
        final String ret= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/FPV_VR/MJPEG/";
        File dir = new File(ret);
        if (!dir.exists()) {
            final boolean mkdirs = dir.mkdirs();
            //System.out.println("mkdirs res"+mkdirs);
        }
        return ret;
    }

    public DecodingInfo getDecodingInfo(){
        return new DecodingInfo(30,0,0,0,
                nativeGetDecodingTime(nativeInstance),0,0,0);
    }

    /**
     * If @param surface!=null a native reference is created and
     * future uvc frames will be decoded into the underlying buffer(s) of the surface
     * If @param surface==null the native reference will be deleted and future frames won't be decoded
     */
    public void setSurface(@Nullable Surface surface){
        nativeSetSurface(nativeInstance,surface);
    }

    private static native long nativeConstruct(String GroundRecordingDirectory,boolean enableGroundRecording);
    private static native void nativeDelete(long nativeInstance);
    // returns 0 on success
    private static native int nativeStartReceiving(long nativeInstance,int venderId, int productId, int fileDescriptor, int busNum, int devAddr, String usbfs);
    // return ground recording filenamePath if file was created
    private static native String nativeStopReceiving(long nativeInstance,Context context);
    private static native void nativeSetSurface(long nativeInstance,Surface surface);

    private static native float nativeGetDecodingTime(long nativeInstance);

}
