package Service;

import Networking.EmulatorNetworking;
import domain.Ambulances;
import repository.RepositoryAmbulances;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AmbulanceService {
    private final RepositoryAmbulances repositoryAmbulances;
    private final EmulatorNetworking emulatorNetworking;

    public AmbulanceService(RepositoryAmbulances repositoryAmbulances, EmulatorNetworking emulatorNetworking) {
        this.repositoryAmbulances = repositoryAmbulances;
        this.emulatorNetworking = emulatorNetworking;
        init();
    }

    private void init() {
        List<Map<String, Object>> ambulanceData;

        try {
            ambulanceData = emulatorNetworking.getAmbulances();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch ambulance data from emulator", e);
        }

        for (Map<String, Object> data : ambulanceData) {
            addAmbulance(data);
        }
    }

    public void addAmbulance(Map<String, Object> ambulance) {
        try {
            String county = (String) ambulance.get("county");
            String city = (String) ambulance.get("city");
            double lat = ((Number) ambulance.get("latitude")).doubleValue();
            double lng = ((Number) ambulance.get("longitude")).doubleValue();

            List<Map<String, Object>> requests = (List<Map<String, Object>>) ambulance.get("requests");

            int quantity = 0;
            if (requests != null && !requests.isEmpty()) {
                Map<String, Object> request = requests.get(0);
                quantity = ((Number) request.get("Quantity")).intValue();
            }

            Ambulances a = new Ambulances(county, city, lat, lng, quantity);

            repositoryAmbulances.save(a);
        } catch (Exception e) {
            throw new RuntimeException("Invalid ambulance data: " + ambulance, e);
        }
    }
}
