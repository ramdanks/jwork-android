package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/** Request untuk mengubah invoice status menjadi Finished
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class JobSelesaiRequest extends StringRequest
{
    /** URL untuk mengubah status invoice */
    private static final String URL = "http://10.0.2.2/invoice/invoiceStatus/";
    /** menyimpan key dan value dari RequestParam REST */
    private Map<String, String> params = new HashMap<>();

    /** ctor inisiasi RequestParam */
    public JobSelesaiRequest(int invoiceId,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(Method.PUT, URL, listener, errorListener);
        params.put("id", String.valueOf(invoiceId));
        params.put("status", "Finished");
    }

    /** memberikan RequestParam untuk Request melalui REST */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
