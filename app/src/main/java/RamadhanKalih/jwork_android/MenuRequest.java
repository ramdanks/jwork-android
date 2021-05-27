package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class MenuRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/job";

    public MenuRequest(Response.Listener<String> listener,
                       Response.ErrorListener errListener) {
        super(Method.GET, URL, listener, errListener);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }
}