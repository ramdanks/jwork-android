package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class JobSelesaiRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/invoice/invoiceStatus/";
    private Map<String, String> params = new HashMap<>();

    public JobSelesaiRequest(int invoiceId,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(Method.PUT, URL, listener, errorListener);
        params.put("id", String.valueOf(invoiceId));
        params.put("status", "Finished");
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
