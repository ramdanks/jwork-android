package RamadhanKalih.jwork_android;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class JobFetchRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/invoice/jobseeker/";

    public JobFetchRequest(int jobseekerId,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(Method.GET, URL + jobseekerId, listener, errorListener);
    }
}
