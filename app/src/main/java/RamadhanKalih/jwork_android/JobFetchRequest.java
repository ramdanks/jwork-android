package RamadhanKalih.jwork_android;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/** Request untuk meminta list invoice dari jobseeker tertentu
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class JobFetchRequest extends StringRequest
{
    /** URL untuk request invoice melalui jobseeker tertentu */
    private static final String URL = "http://10.0.2.2/invoice/jobseeker/";

    /** ctor inisiasi dengan membangun URL jobseeker yang diminta */
    public JobFetchRequest(int jobseekerId,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(Method.GET, URL + jobseekerId, listener, errorListener);
    }
}
