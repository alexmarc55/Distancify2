package Service;

import Networking.EmulatorNetworking;
import domain.Police;
import repository.RepositoryPolice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PoliceService {
    private final RepositoryPolice repositoryPolice;
    private final EmulatorNetworking emulatorNetworking;

    public PoliceService(RepositoryPolice repositoryPolice, EmulatorNetworking emulatorNetworking) {
        this.repositoryPolice = repositoryPolice;
        this.emulatorNetworking = emulatorNetworking;
        init();
    }

    private void init() {
        List<Map<String, Object>> policeUnits;

        try {
            policeUnits = emulatorNetworking.getPolice(); // Make sure this exists in EmulatorNetworking
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch police unit data from emulator", e);
        }

        for (Map<String, Object> unit : policeUnits) {
            addPolice(unit);
        }
    }

    public void addPolice(Map<String, Object> police) {
        try {
            String county = (String) police.get("county");
            String city = (String) police.get("city");
            double lat = ((Number) police.get("lat")).doubleValue();
            double lng = ((Number) police.get("long")).doubleValue();

            List<Map<String, Object>> requests = (List<Map<String, Object>>) police.get("requests");

            int quantity = 0;
            if (requests != null && !requests.isEmpty()) {
                Map<String, Object> request = requests.get(0);
                quantity = ((Number) request.get("Quantity")).intValue();
            }

            Police policeUnit = new Police(county, city, lat, lng, quantity);
            repositoryPolice.save(policeUnit);
        } catch (Exception e) {
            throw new RuntimeException("Invalid police unit data: " + police, e);
        }
    }

}
