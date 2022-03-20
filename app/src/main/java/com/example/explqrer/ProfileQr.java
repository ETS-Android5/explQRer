package com.example.explqrer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Link: https://www.android-examples.com/generate-qr-code-in-android-using-zxing-library-in-android-studio/
 * Author: Juned Mughal
 * generates a QR for the player to sign in to new device or for someone else to view their account
 */
public class ProfileQr extends AppCompatActivity {
    private PlayerProfile player;
    public final static int QRcodeWidth = 500 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_qr);

        // get the player from main activity
        player = MainActivity.getPlayer();

        ImageView imageView = (ImageView)findViewById(R.id.imageView);


        try {
            Bitmap bitmap = TextToImageEncode("Player QR:" + player.getName());
            System.out.println(player.getName());

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.white):getResources().getColor(R.color.cool_dark);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
//        // get the player from main activity
//        player = MainActivity.getPlayer();
//
//        ImageView imageView = (ImageView)findViewById(R.id.qrImageView);
//
//
//        try {
//            Bitmap bitmap = TextToImageEncode("Player QR:"+ player.getName());
//
//            imageView.setImageBitmap(bitmap);
//
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//
//
//
//    }
//
//
//    Bitmap TextToImageEncode(String Value) throws WriterException {
//        BitMatrix bitMatrix;
//        try {
//            bitMatrix = new MultiFormatWriter().encode(
//                    Value,
//                    BarcodeFormat.DATA_MATRIX.QR_CODE,
//                    QRcodeWidth, QRcodeWidth, null
//            );
//
//        } catch (IllegalArgumentException Illegalargumentexception) {
//
//            return null;
//        }
//        int bitMatrixWidth = bitMatrix.getWidth();
//
//        int bitMatrixHeight = bitMatrix.getHeight();
//
//        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
//
//        for (int y = 0; y < bitMatrixHeight; y++) {
//            int offset = y * bitMatrixWidth;
//
//            for (int x = 0; x < bitMatrixWidth; x++) {
//
//                pixels[offset + x] = bitMatrix.get(x, y) ?
//                        getResources().getColor(R.color.white):getResources().getColor(R.color.cool_dark);
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
//
//        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
//        return bitmap;
//
//    }
//}
