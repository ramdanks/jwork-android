package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class JobBatalRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/invoice/";
    private Map<String, String> params = new HashMap<>();

    public JobBatalRequest(int invoiceId,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(Method.PUT, URL, listener, errorListener);
        params.put("id", String.valueOf(invoiceId));
        params.put("status", "Cancelled");
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
