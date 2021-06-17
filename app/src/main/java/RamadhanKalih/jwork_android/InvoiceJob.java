package RamadhanKalih.jwork_android;

import org.json.JSONArray;
import org.json.JSONObject;

/** Menyimpan keterangan Invoice dan Job untuk ditampilkan dalam beberapa Activity
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class InvoiceJob
{
    /** id invoice */
    int id;
    /** nama recruiter */
    String recruiter;
    /** tanggal invoice */
    String date;
    /** status invoice */
    String status;
    /** tipe pembayaran invoice */
    String type;
    /** nama job */
    String jobName;
    /** kategori job */
    String jobCategory;
    /** fee job */
    int jobFee;
    /** kode referral bonus */
    String referralCode = null;
    /** extra fee bonus */
    int extraFee = 0;

    /** konversi response dari VolleyRequest menjadi objek InvoiceJob */
    public static InvoiceJob parseJSONResponse(String response) {
        try {
            InvoiceJob inv = new InvoiceJob();

            JSONObject invoiceJSON = new JSONObject(response);
            JSONArray jobsJSON = invoiceJSON.getJSONArray("jobs");

            JSONObject jobJSON = jobsJSON.getJSONObject(0);
            JSONObject recJSON = jobJSON.getJSONObject("recruiter");

            inv.id = invoiceJSON.getInt("id");
            inv.status = invoiceJSON.getString("invoiceStatus");
            inv.date = invoiceJSON.getString("date");
            inv.recruiter = recJSON.getString("name");
            inv.jobName = jobJSON.getString("name");
            inv.jobFee = jobJSON.getInt("fee");
            inv.jobCategory = jobJSON.getString("category");
            inv.type = invoiceJSON.getString("paymentType");

            if (!invoiceJSON.isNull("bonus")) {
                JSONObject bonusJSON = invoiceJSON.getJSONObject("bonus");
                inv.referralCode = bonusJSON.getString("referralCode");
                inv.extraFee = bonusJSON.getInt("extraFee");
            }

            return inv;
        } catch (Exception e) {}
        return null;
    }
}
