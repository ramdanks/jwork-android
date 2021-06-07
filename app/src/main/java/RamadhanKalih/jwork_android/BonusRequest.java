package RamadhanKalih.jwork_android;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class BonusRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/bonus/";

    public BonusRequest(String referralCode,
                        Response.Listener<String> listener,
                        Response.ErrorListener errorListener) {
        super(Method.GET, URL + referralCode, listener, errorListener);
    }
}