package RamadhanKalih.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/** Request untuk mendaftarkan jobseeker dalam database
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class RegisterRequest extends StringRequest
{
    /** URL untuk mengubah mendaftar jobseeker */
    private static final String URL = "http://10.0.2.2/jobseeker/register";
    /** menyimpan key dan value dari RequestParam REST */
    private Map<String, String> params;

    /** ctor inisiasi RequestParam */
    public RegisterRequest(String name, String email, String password,
                           Response.Listener<String> listener,
                           Response.ErrorListener errListener) {
        super(Method.POST, URL, listener, errListener);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
    }

    /** memberikan RequestParam untuk Request melalui REST */
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
