package com.qqonline.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.qqonline.mpf.PicPlayActivity2;
import com.qqonline.mpf.R;

import org.w3c.dom.Text;

/**
 * Created by YE on 2015/9/7 0007.
 */
public class DeleteDialogFragment extends DialogFragment implements View.OnClickListener
        ,DialogInterface.OnClickListener{
    public static final String BUNDLE_KEY_TITLE="BUNDLE_KEY_TITLE";
    public static final String BUNDLE_KEY_CONTENT="BUNDLE_KEY_CONTENT";
    private static final String TAG="DeleteDialogFragment";
    private String mTitle;
    private String mContent;
    private View.OnClickListener mOnDeleteClicked;
    private DialogInterface.OnClickListener mOnAlertDeleteClicked;
    private Activity activity;
    private CheckBox cb;
    public static DeleteDialogFragment newInstance(Bundle bundle) {
        DeleteDialogFragment fragment=new DeleteDialogFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
        try {
            mOnDeleteClicked=(View.OnClickListener)activity;
            mOnAlertDeleteClicked=(DialogInterface.OnClickListener)activity;
        } catch (ClassCastException e) {
            Log.w(TAG,"activity is no implement mOnDeleteClicked");
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        Resources resources=getResources();
        String defaultTitle=resources.getString(R.string.default_delete_dialog_title);
        String defaultContent=resources.getString(R.string.default_delete_dialog_content);
        if (bundle != null) {
            mTitle = bundle.getString(BUNDLE_KEY_TITLE, defaultTitle);
            mContent = bundle.getString(BUNDLE_KEY_CONTENT, defaultContent);
        } else {
            mTitle=defaultTitle;
            mContent=defaultContent;
        }
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view=inflater.inflate(R.layout.layout_delete_dialog_fragment,container,false);
        TextView tv=(TextView)view.findViewById(R.id.tvContent);
        Button btnDelete=(Button)view.findViewById(R.id.btnDelete);
        Button btnCancle=(Button)view.findViewById(R.id.btnCancle);

        btnDelete.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        return view;
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_delete_select, null, false);
        cb = (CheckBox)v.findViewById(R.id.cbDeleteSource);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity)
                .setMessage(R.string.default_delete_dialog_content)
                .setView(v)
                .setPositiveButton(R.string.delete,this)
                .setNegativeButton(R.string.Cancle, null);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnDelete && mOnDeleteClicked != null) {
            if (cb != null && cb.isChecked()) {
                view.setTag(true);
            }
            mOnDeleteClicked.onClick(view);
        }
        dismiss();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) {
            if (mOnAlertDeleteClicked == null) {
                return;
            }
            if (cb != null && cb.isChecked()) {
                mOnAlertDeleteClicked.onClick(dialog, PicPlayActivity2.DELETE_PICTURE_WITH_SOURCE);
            } else {
                mOnAlertDeleteClicked.onClick(dialog, PicPlayActivity2.DELETE_PICTURE_WITHOUT_SOURCE);
            }
        } else {
            mOnAlertDeleteClicked.onClick(dialog, which);
        }
        dismiss();
    }
}
