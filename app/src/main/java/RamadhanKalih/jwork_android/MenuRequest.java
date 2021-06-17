package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/** Request untuk meminta list job dalam database
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class MenuRequest extends StringRequest
{
    /** URL untuk meminta list job */
    private static final String URL = "http://10.0.2.2/job";

    /** ctor inisasi variabel */
    public MenuRequest(Response.Listener<String> listener,
                       Response.ErrorListener errListener) {
        super(Method.GET, URL, listener, errListener);
    }
}