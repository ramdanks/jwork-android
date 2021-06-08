package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplyJobRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/invoice/";
    private static final String EWALLET_SUBDIR = "createEWalletPayment";
    private static final String BANK_SUBDIR = "createBankPayment";

    private Map<String, String> params = new HashMap<>();

    public ApplyJobRequest(ArrayList<Integer> jobIdList,
                           int jobseekerId,
                           Response.Listener<String> listener,
                           Response.ErrorListener errListener) {
        super(Method.POST, URL + BANK_SUBDIR, listener, errListener);
        params.put("jobIdList", formatJobIdList(jobIdList));
        params.put("jobseekerId", String.valueOf(jobseekerId));
    }

    public ApplyJobRequest(ArrayList<Integer> jobIdList,
                           int jobseekerId,
                           int adminFee,
                           Response.Listener<String> listener,
                           Response.ErrorListener errListener) {
        super(Method.POST, URL + BANK_SUBDIR, listener, errListener);
        params.put("jobIdList", formatJobIdList(jobIdList));
        params.put("jobseekerId", String.valueOf(jobseekerId));
        params.put("adminFee", String.valueOf(adminFee));
    }

    public ApplyJobRequest(ArrayList<Integer> jobIdList,
                           int jobseekerId,
                           String referralCode,
                           Response.Listener<String> listener,
                           Response.ErrorListener errListener) {
        super(Method.POST, URL + EWALLET_SUBDIR, listener, errListener);
        params.put("jobIdList", formatJobIdList(jobIdList));
        params.put("jobseekerId", String.valueOf(jobseekerId));
        if (referralCode != null && !referralCode.isEmpty())
            params.put("referralCode", referralCode);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    private String formatJobIdList(ArrayList<Integer> jobIdList) {
        if (jobIdList.isEmpty())
            return null;
        String idList = String.valueOf(jobIdList.get(0));
        for (int i = 1; i < jobIdList.size(); i++)
        {
            Integer jobId = jobIdList.get(i);
            idList += "," + jobId;
        }
        return idList;
    }
}
