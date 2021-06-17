package RamadhanKalih.jwork_android;


/** menyimpan kumpulan variabel dari objek Recruiter
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class Recruiter
{
    private int id;
    private String name;
    private String email;
    private String phoneNumber;
    private Location location;

    public Recruiter(int id, String name, String email,
                     String phoneNumber, Location location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Location getLocation() { return location; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLocation(Location location) { this.location = location; }
}
