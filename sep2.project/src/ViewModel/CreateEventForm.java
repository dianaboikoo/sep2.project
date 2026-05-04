package ViewModel;

public class CreateEventForm
{
    private String name;
    private String description;
    private String dateTime;
    private String venue;
    private String address;
    private String ticketPrice;
    private String totalTickets;
    private String imageURL;

    public CreateEventForm()
    {
        this.name = "";
        this.description = "";
        this.dateTime = "";
        this.venue = "";
        this.address = "";
        this.ticketPrice = "";
        this.totalTickets = "";
        this.imageURL = "";
    }

    // setters — used by ViewModel.updateField
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setAddress(String address) { this.address = address; }
    public void setTicketPrice(String ticketPrice) { this.ticketPrice = ticketPrice; }
    public void setTotalTickets(String totalTickets) { this.totalTickets = totalTickets; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    // getters — used by EventValidator and ViewModel
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDateTime() { return dateTime; }
    public String getVenue() { return venue; }
    public String getAddress() { return address; }
    public String getTicketPrice() { return ticketPrice; }
    public String getTotalTickets() { return totalTickets; }
    public String getImageURL() { return imageURL; }
}