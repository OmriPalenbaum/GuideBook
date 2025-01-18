

public class WeatherResponse {
     private Main main;

     public Main getMain() {
         return main;
     }

     public static class Main {
         private float temp;
         private int humidity;

         public float getTemp() {
             return temp;
         }

         public int getHumidity() {
             return humidity;
         }
     }
 }