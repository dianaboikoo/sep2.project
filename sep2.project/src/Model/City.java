package Model;

public class City
{
  private int zipCode;
  private String cityName;

  public City(int cityId, String cityName)
  {
    this.zipCode = cityId;
    this.cityName = cityName;
  }

  public int getCityId() { return zipCode; }
  public String getCityName() { return cityName; }
}
