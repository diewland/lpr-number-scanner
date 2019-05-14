// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.diewland.lpr_no_scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.diewland.lpr_no_scanner.helper.FrameMetadata;
import com.diewland.lpr_no_scanner.helper.GraphicOverlay;
import com.diewland.lpr_no_scanner.helper.TextGraphic;
import com.diewland.lpr_no_scanner.helper.VisionProcessorBase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/** Processor for the text recognition demo. */
public class TextRecognitionProcessor extends VisionProcessorBase<FirebaseVisionText> {

  private static final String TAG = "TextRecognitionPCS";

  private final FirebaseVisionTextDetector detector;
  private Context previewCtx;

  public TextRecognitionProcessor(Context ctx) {
    detector = FirebaseVision.getInstance().getVisionTextDetector();
    previewCtx = ctx;
  }

  @Override
  public void stop() {
    try {
      detector.close();
    } catch (IOException e) {
      Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
    }
  }

  @Override
  protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
    return detector.detectInImage(image);
  }

  @Override
  protected void onSuccess(
      @NonNull FirebaseVisionText results,
      @NonNull FrameMetadata frameMetadata,
      @NonNull GraphicOverlay graphicOverlay) {
    graphicOverlay.clear();
    List<FirebaseVisionText.Block> blocks = results.getBlocks();

    String text_idcard = "";

    for (int i = 0; i < blocks.size(); i++) {
      List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
      for (int j = 0; j < lines.size(); j++) {
        List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
        for (int k = 0; k < elements.size(); k++) {
          // uncomment to draw detection boxes on screen
          GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, elements.get(k));
          graphicOverlay.add(textGraphic);
          /*
          */
          String t_str = elements.get(k).getText();
          if(android.text.TextUtils.isDigitsOnly(t_str)){
              text_idcard += t_str;
          }
          else {
              if(text_idcard.length() == 13){ // detect Thai ID logic
                  Log.d(TAG, text_idcard);
                  Activity act = (Activity) previewCtx;
                  Intent intent = new Intent();
                  intent.putExtra("ACC_ID", text_idcard);
                  act.setResult(RESULT_OK, intent);
                  act.finish();
              }
              else {
                  text_idcard = "";
              }
          }
        }
      }
    }
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.w(TAG, "Text detection failed." + e);
  }
}
