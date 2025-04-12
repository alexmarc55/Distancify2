package Service;

import Networking.EmulatorNetworking;
import domain.Fire;
import repository.RepositoryFire;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FireService {
    private final RepositoryFire repositoryFire;
    private final EmulatorNetworking emulatorNetworking;

    public FireService(RepositoryFire repositoryFire, EmulatorNetworking emulatorNetworking) {
        this.repositoryFire = repositoryFire;
        this.emulatorNetworking = emulatorNetworking;
        init();
    }

    private void init() {
        List<Map<String, Object>> fireUnits;

        try {
            fireUnits = emulatorNetworking.getFires(); // Make sure this method exists
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch fire unit data from emulator", e);
        }

        for (Map<String, Object> fire : fireUnits) {
            addFire(fire);
        }
    }

    public void addFire(Map<String, Object> fire) {
        try {
            String county = (String) fire.get("county");
            String city = (String) fire.get("city");
            double lat = ((Number) fire.get("latitude")).doubleValue();
            double lng = ((Number) fire.get("longitude")).doubleValue();

            int quantity = 0;
            Object requestsObj = fire.get("requests");

            if (requestsObj instanceof List<?>) {
                List<?> requests = (List<?>) requestsObj;
                if (!requests.isEmpty() && requests.get(0) instanceof Map) {
                    Map<String, Object> request = (Map<String, Object>) requests.get(0);
                    Object qtyObj = request.get("Quantity");
                    if (qtyObj instanceof Number) {
                        quantity = ((Number) qtyObj).intValue();
                    }
                }
            }

            Fire fireUnit = new Fire(county, city, lat, lng, quantity);
            repositoryFire.save(fireUnit);
        } catch (Exception e) {
            throw new RuntimeException("Invalid fire unit data: " + fire, e);
        }
    }
}