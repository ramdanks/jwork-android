package RamadhanKalih.jwork_android;

import org.json.JSONArray;
import org.json.JSONObject;

public class InvoiceJob
{
    int id;
    String recruiter;
    String date;
    String status;
    String type;

    String jobName;
    String jobCategory;
    int jobFee;

    String referralCode = null;
    int extraFee = 0;

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
