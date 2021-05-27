package RamadhanKalih.jwork_android;

public class Location
{
    private String province;
    private String description;
    private String city;

    public Location(String province, String description, String city) {
        this.province = province;
        this.description = description;
        this.city = city;
    }

    public String getProvince() { return province; }
    public String getCity() { return city; }
    public String getDescription() { return description; }

    public void setProvince(String province) { this.province = province; }
    public void setCity(String city) { this.city = city; }
    public void setDescription(String province) { this.description = description; }
}
