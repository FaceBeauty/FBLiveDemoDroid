package com.nimo.facebeauty.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nimo.facebeauty.R;

import java.lang.ref.WeakReference;

/**
 * 重置Dialog
 */
public class HtPermissionDialog extends DialogFragment {

  private View root;
  private Context context;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    context = new WeakReference<>(getActivity()).get();
    root = LayoutInflater.from(context).inflate(R.layout.dialog_permission, null);
    Dialog dialog = new Dialog(context, R.style.TiDialog);
    dialog.setContentView(root);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(true);

    Window window = dialog.getWindow();
    if (window != null) {
      WindowManager.LayoutParams params = window.getAttributes();
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      params.height = WindowManager.LayoutParams.WRAP_CONTENT;
      params.gravity = Gravity.TOP;
      window.setAttributes(params);
    }

    return dialog;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

  }

}
