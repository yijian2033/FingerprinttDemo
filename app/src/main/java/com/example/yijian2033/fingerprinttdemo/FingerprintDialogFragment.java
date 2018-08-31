package com.example.yijian2033.fingerprinttdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import javax.crypto.Cipher;

/**
 * Create by yijian2033 on 2018/8/31
 */
public class FingerprintDialogFragment extends DialogFragment {

    private LoginActivity mActivity;
    private FingerprintManager fingerprintManager;
    private TextView tvErrorMsg;

    /**
     * 标识是否是用户主动取消的认证。
     */
    private boolean isSelfCancelled;
    private CancellationSignal cancellationSignal;

    private Cipher mCipher;

    public void setCipher(Cipher cipher) {
        mCipher = cipher;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (LoginActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fingerprintManager = getContext().getSystemService(FingerprintManager.class);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_finger, container, false);
        tvErrorMsg = view.findViewById(R.id.error_msg);
        TextView cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                stopListening();
                mActivity.finish();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startListening(mCipher);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止指纹认证监听
        stopListening();
    }

    @SuppressLint("MissingPermission")
    private void startListening(Cipher cipher) {
        isSelfCancelled = false;
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(mActivity, "指纹认证成功", Toast.LENGTH_SHORT).show();
                mActivity.onAuthenticated();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                tvErrorMsg.setText("指纹认证失败，请再试一次");
            }
        }, null);


    }

    private void stopListening() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
            isSelfCancelled = true;
        }
    }

}
