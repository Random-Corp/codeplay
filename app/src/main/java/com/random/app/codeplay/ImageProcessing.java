package com.random.app.codeplay;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


public class ImageProcessing
{

    public static void processImage(Bitmap inputImage)
    {
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(inputImage);

        FirebaseVisionTextDetector textDetector = FirebaseVision.getInstance()
                                                                .getVisionTextDetector();

        Task<FirebaseVisionText> result = textDetector.detectInImage(visionImage)
                                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                                @Override
                                                public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
    }

}
