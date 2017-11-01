package skylerlovecraft.geocamera;

/**
 * Created by Skyler on 10/31/17.
 */

public class Photograph {
    double latitude, longitude;
    String timestamp, filePath, fileName;

    public Photograph(double latitude, double longitude, String fileName, String filePath, String timestamp)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.filePath = filePath;
    }

    public Photograph(){
        //do nothing
    }
}
