package bg.sofia.uni.fmi.mjt.flightscanner;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.comparators.FlightDestinationComparator;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.Flight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


public class FlightScanner implements FlightScannerAPI {
    Set<Flight> flights = new HashSet<>();

    @Override
    public void add(Flight flight) {
        this.flights.add(flight);
    }

    @Override
    public void addAll(Collection<Flight> flights) {
        this.flights.addAll(flights);
    }

    @Override
    public List<Flight> searchFlights(Airport from, Airport to) {
        if (from == null || to == null || from.equals(to)) {
            throw new IllegalArgumentException();
        }

        List<Flight> result = new ArrayList<>();

        Queue<Airport> queue = new LinkedList<>();
        Set<Airport> visited = new HashSet<>();
        Map<Airport, Flight> parent = new HashMap<>();
        Map<Airport, List<Flight>> adjList = new HashMap<>();

        for (Flight flight : this.flights) {
            if (!adjList.containsKey(flight.getFrom())) {
                adjList.put(flight.getFrom(), new ArrayList<>());
            }
            adjList.get(flight.getFrom()).add(flight);
        }

        queue.add(from);
        visited.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Airport currentAirport = queue.peek();
            queue.poll();


            if (currentAirport.equals(to)) {
                break;
            }

            visited.add(currentAirport);

            List<Flight> airportFlights = adjList.getOrDefault(currentAirport, null);

            if (airportFlights != null) {

                for (Flight currentFlight : airportFlights) {
                    if (!visited.contains(currentFlight.getTo())) {
                        queue.add(currentFlight.getTo());
                        parent.put(currentFlight.getTo(), currentFlight);
                    }
                }
            }
        }

        Airport current = to;

        while (parent.get(current) != null) {
            result.add(parent.get(current));
            current = parent.get(current).getFrom();
        }

        Collections.reverse(result);

        return result;
    }

    @Override
    public List<Flight> getFlightsSortedByFreeSeats(Airport from) {
        if (from == null) {
            throw new IllegalArgumentException();
        }

        List<Flight> result = new ArrayList<>();

        for (Flight flight : this.flights) {
            if (flight.getFrom().equals(from)) {
                result.add(flight);
            }
        }

        Comparator c = Collections.reverseOrder(new FlightDestinationComparator());
        Collections.sort(result, c);

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Flight> getFlightsSortedByDestination(Airport from) {
        if (from == null) {
            throw new IllegalArgumentException();
        }
        List<Flight> result = new ArrayList<>();

        for (Flight flight : this.flights) {
            if (flight.getFrom().equals(from)) {
                result.add(flight);
            }
        }

        Collections.sort(result, new FlightDestinationComparator());

        return Collections.unmodifiableList(result);
    }
}
