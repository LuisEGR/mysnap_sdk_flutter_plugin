package com.miteksystems.creditcardcontroller;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.miteksystems.misnap.cardio.CardIoWrapperActivity;
import com.miteksystems.misnap.mibidata.MibiData;
import com.miteksystems.misnap.mibidata.MibiDataException;
import com.miteksystems.misnap.params.BaseParamMgr;
import com.miteksystems.misnap.params.CreditCardApi;
import com.miteksystems.misnap.params.CreditCardParamMgr;
import com.miteksystems.misnap.params.MiSnapApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreditCardFragment extends Fragment {

    private static final int CC_SCAN_REQUEST_CODE = 499;


    @Override
    public void onResume() {
        super.onResume();

        try {
            String params = getActivity().getIntent().getStringExtra(MiSnapApi.JOB_SETTINGS);
            CreditCardParamMgr ccParamsMgr = new CreditCardParamMgr(new JSONObject(params));

            Intent scanIntent = new Intent(getActivity(), CardIoWrapperActivity.class);

            // customize these values to suit your needs.
            scanIntent.putExtra(CreditCardApi.CreditCardGuideColor, ccParamsMgr.getCreditCardGuideColor());
            scanIntent.putExtra(CreditCardApi.CreditCardSuppressConfirmScreen, ccParamsMgr.getCreditCardSuppressConfirmScreen());
            scanIntent.putExtra(CreditCardApi.CreditCardRequireCVV, ccParamsMgr.getCreditCardRequireCVV());
            scanIntent.putExtra(CreditCardApi.CreditCardRequireExpiry, ccParamsMgr.getCreditCardRequireExpiry());

            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
            startActivityForResult(scanIntent, CC_SCAN_REQUEST_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String misnapResultCode = data.getStringExtra(MiSnapApi.RESULT_CODE);

        Intent output = null;
        final int UNKNOWN = -1;
        Log.i("Card", "result = " + resultCode);
        switch (requestCode) {
            case CC_SCAN_REQUEST_CODE:
                // Pass credit card information back
                output = new Intent();

                // Add MIBI data
                output.putExtra(MiSnapApi.RESULT_MIBI_DATA, buildMibiData(misnapResultCode));

                // Forward MiSnap result code
                output.putExtra(MiSnapApi.RESULT_CODE, misnapResultCode);

                // Forward credit card info
                output.putExtra(CreditCardApi.CREDIT_CARD_NUMBER, data.getStringExtra(CreditCardApi.CREDIT_CARD_NUMBER));
                output.putExtra(CreditCardApi.CREDIT_CARD_REDACTED_NUMBER, data.getStringExtra(CreditCardApi.CREDIT_CARD_REDACTED_NUMBER));
                output.putExtra(CreditCardApi.CREDIT_CARD_FORMATTED_NUMBER, data.getStringExtra(CreditCardApi.CREDIT_CARD_FORMATTED_NUMBER));
                output.putExtra(CreditCardApi.CREDIT_CARD_TYPE, data.getStringExtra(CreditCardApi.CREDIT_CARD_TYPE));
                output.putExtra(CreditCardApi.CREDIT_CARD_CVV, data.getIntExtra(CreditCardApi.CREDIT_CARD_CVV, UNKNOWN));
                output.putExtra(CreditCardApi.CREDIT_CARD_EXPIRY_MONTH, data.getIntExtra(CreditCardApi.CREDIT_CARD_EXPIRY_MONTH, UNKNOWN));
                output.putExtra(CreditCardApi.CREDIT_CARD_EXPIRY_YEAR, data.getIntExtra(CreditCardApi.CREDIT_CARD_EXPIRY_YEAR, UNKNOWN));

                break;
        }

        // Forward Activity result code and Intent
        getActivity().setResult(resultCode, output);

        getActivity().finish(); // always exit the app (?)
    }

    private String buildMibiData(String resultCode) {
        MibiData mibiData = MibiData.getInstance();

        String params = getActivity().getIntent().getStringExtra(MiSnapApi.JOB_SETTINGS);

        try {
            BaseParamMgr paramMgr = new BaseParamMgr(new JSONObject(params));
            String appVersion = paramMgr.getAppVersion();
            if (appVersion.length() > 0) {
                mibiData.setAppVersion(appVersion);
            }

            mibiData.setMiSnapVersion(getActivity().getString(R.string.misnap_versionName));

            mibiData.setMiSnapResultCode(resultCode)
                    .setDocument(MiSnapApi.PARAMETER_DOCTYPE_CREDIT_CARD)
                    .setAutocapture("1");

            mibiData.setWarnings(new JSONArray());


            if (params != null) {
                mibiData.addParameters(new JSONObject(params))
                        .removeParameter("RequiredMaxImageSize")
                        .removeParameter("CameraViewFinderMinVericalFill")
                        .removeParameter("TutorialBrandNewUserDuration")
                        .removeParameter("SecurityResult")
                        .removeParameter(MiSnapApi.AppVersion);
            }
        } catch (JSONException e) {
            JSONObject error = new JSONObject();
            String err = "Error preparing the MIBI data";
            try {
                error.put("Error", err);
                return error.toString();
            } catch (JSONException e2) {
                return err;
            }
        }

        try {
            String finalMibiData = mibiData.getMibiData();
            Log.i("Final MibiData", "Mibi: " + finalMibiData);

            return finalMibiData;
        } catch (MibiDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
