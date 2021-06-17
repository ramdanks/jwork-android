package RamadhanKalih.jwork_android;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/** Request meminta bonus dengan kode referral
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class BonusRequest extends StringRequest
{
    /** URL untuk request Bonus */
    private static final String URL = "http://10.0.2.2/bonus/";

    /** ctor untuk membangun URL request Bonus dengan kode referral */
    public BonusRequest(String referralCode,
                        Response.Listener<String> listener,
                        Response.ErrorListener errorListener) {
        super(Method.GET, URL + referralCode, listener, errorListener);
    }
}