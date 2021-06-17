package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Request untuk membuat sebuah Invoice dari sebuah Job
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class ApplyJobRequest extends StringRequest
{
    /** base URL untuk invoice */
    private static final String URL = "http://10.0.2.2/invoice/";
    /** sub-URL untuk membangun invoice jenis pembayaran Ewallet */
    private static final String EWALLET_SUBDIR = "createEWalletPayment";
    /** sub-URL untuk membangun invoice jenis pembayaran Bank */
    private static final String BANK_SUBDIR = "createBankPayment";

    /** menyimpan key dan value dari RequestParam REST */
    private Map<String, String> params = new HashMap<>();

    /** ctor untuk pembayaran Bank tanpa adminFee */
    public ApplyJobRequest(ArrayList<Integer> jobIdList,
                           int jobseekerId,
                           Response.Listener<String> listener,
                           Response.ErrorListener errListener) {
        super(Method.POST, URL + BANK_SUBDIR, listener, errListener);
        params.put("jobIdList", formatJobIdList(jobIdList));
        params.put("jobseekerId", String.valueOf(jobseekerId));
    }

    /** ctor untuk pembayaran Bank dengan adminFee */
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

    /** ctor untuk pembayaran Ewallet
     * @param referralCode kode referral untuk menggunakan bonus, boleh null / kosong */
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

    /** memberikan RequestParam untuk Request melalui REST */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    /** mengubah bentuk list menjadi string */
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
