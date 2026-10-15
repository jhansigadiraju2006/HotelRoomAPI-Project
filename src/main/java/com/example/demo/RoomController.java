package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final Map<Long, Room> rooms = new LinkedHashMap<>();
    private long nextId = 1;
    private static final Set<String> TYPES =
            Set.of("Single", "Double", "Suite", "Deluxe", "Family");

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    private String validateRoom(Room room) {
        if (room == null) return "Invalid room";
        if (room.getName() != null) room.setName(room.getName().trim());
        if (room.getLocation() != null) room.setLocation(room.getLocation().trim());
        if (room.getRoomNumber() != null) room.setRoomNumber(room.getRoomNumber().trim());
        if (room.getType() == null || !TYPES.contains(room.getType())) return "Invalid type";
        if (room.getPricePerNight() <= 0 || room.getPricePerNight() > 100000) return "Invalid pricePerNight";
        if (room.getCapacity() < 1 || room.getCapacity() > 10) return "Invalid capacity";
        return null;
    }

    private boolean roomNumberExists(String number, Long exceptId) {
        return rooms.values().stream().anyMatch(r ->
                !Objects.equals(r.getId(), exceptId)
                && r.getRoomNumber() != null
                && r.getRoomNumber().equalsIgnoreCase(number));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Room room) {
        String problem = validateRoom(room);
        if (problem != null) return error(HttpStatus.BAD_REQUEST, problem);
        if (roomNumberExists(room.getRoomNumber(), null))
            return error(HttpStatus.CONFLICT, "roomNumber already exists");
        room.setId(nextId++);
        rooms.put(room.getId(), room);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String available,
            @RequestParam(required = false) Double minPricePerNight,
            @RequestParam(required = false) Double maxPricePerNight,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "asc") String order) {

        if (page < 1 || limit < 1 || limit > 50)
            return error(HttpStatus.BAD_REQUEST, "page must be >= 1 and limit must be between 1 and 50");
        if (type != null && !TYPES.contains(type))
            return error(HttpStatus.BAD_REQUEST, "Invalid type");
        if (available != null && !available.equals("true") && !available.equals("false"))
            return error(HttpStatus.BAD_REQUEST, "available must be true or false");
        if (minPricePerNight != null && minPricePerNight < 0)
            return error(HttpStatus.BAD_REQUEST, "Invalid minimum price");
        if (maxPricePerNight != null && maxPricePerNight < 0)
            return error(HttpStatus.BAD_REQUEST, "Invalid maximum price");
        if (minPricePerNight != null && maxPricePerNight != null
                && minPricePerNight > maxPricePerNight)
            return error(HttpStatus.BAD_REQUEST, "Invalid price range");
        if (sort != null && !Set.of("name", "pricePerNight", "capacity").contains(sort))
            return error(HttpStatus.BAD_REQUEST, "sort must be name, pricePerNight, or capacity");
        if (!order.equals("asc") && !order.equals("desc"))
            return error(HttpStatus.BAD_REQUEST, "order must be asc or desc");

        List<Room> result = new ArrayList<>(rooms.values());
        if (type != null) result.removeIf(r -> !type.equals(r.getType()));
        if (available != null) {
            boolean wanted = Boolean.parseBoolean(available);
            result.removeIf(r -> r.isAvailable() != wanted);
        }
        if (minPricePerNight != null) result.removeIf(r -> r.getPricePerNight() < minPricePerNight);
        if (maxPricePerNight != null) result.removeIf(r -> r.getPricePerNight() > maxPricePerNight);
        if (!search.isBlank()) {
            String q = search.toLowerCase(Locale.ROOT);
            result.removeIf(r ->
                    !r.getName().toLowerCase(Locale.ROOT).contains(q)
                    && !r.getLocation().toLowerCase(Locale.ROOT).contains(q));
        }

        if (sort != null) {
            Comparator<Room> comparator = switch (sort) {
                case "name" -> Comparator.comparing(r -> r.getName().toLowerCase(Locale.ROOT));
                case "capacity" -> Comparator.comparingInt(Room::getCapacity);
                default -> Comparator.comparingDouble(Room::getPricePerNight);
            };
            if (order.equals("desc")) comparator = comparator.reversed();
            result.sort(comparator);
        }

        int total = result.size();
        int totalPages = (int) Math.ceil((double) total / limit);
        int start = Math.min((page - 1) * limit, total);
        int end = Math.min(start + limit, total);
        List<Room> data = result.subList(start, end);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("page", page);
        response.put("limit", limit);
        response.put("total", total);
        response.put("totalPages", totalPages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Room room = rooms.get(id);
        if (room == null) return error(HttpStatus.NOT_FOUND, "Record not found");
        return ResponseEntity.ok(room);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replace(@PathVariable Long id, @Valid @RequestBody Room room) {
        if (!rooms.containsKey(id)) return error(HttpStatus.NOT_FOUND, "Record not found");
        String problem = validateRoom(room);
        if (problem != null) return error(HttpStatus.BAD_REQUEST, problem);
        if (roomNumberExists(room.getRoomNumber(), id))
            return error(HttpStatus.CONFLICT, "roomNumber already exists");
        room.setId(id);
        rooms.put(id, room);
        return ResponseEntity.ok(room);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(@PathVariable Long id, @RequestBody Map<String, Object> changes) {
        Room old = rooms.get(id);
        if (old == null) return error(HttpStatus.NOT_FOUND, "Record not found");

        Set<String> allowed = Set.of("name", "location", "type", "pricePerNight",
                "capacity", "available", "roomNumber");
        if (changes.isEmpty() || changes.keySet().stream().noneMatch(allowed::contains))
            return error(HttpStatus.BAD_REQUEST, "No recognised fields");

        Room room = new Room(old.getId(), old.getName(), old.getLocation(), old.getType(),
                old.getPricePerNight(), old.getCapacity(), old.isAvailable(), old.getRoomNumber());

        try {
            for (Map.Entry<String, Object> entry : changes.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!allowed.contains(key)) continue;
                if (value == null) return error(HttpStatus.BAD_REQUEST, "Null value is not allowed");
                switch (key) {
                    case "name" -> room.setName((String) value);
                    case "location" -> room.setLocation((String) value);
                    case "type" -> room.setType((String) value);
                    case "roomNumber" -> room.setRoomNumber((String) value);
                    case "available" -> {
                        if (!(value instanceof Boolean)) return error(HttpStatus.BAD_REQUEST, "Invalid available");
                        room.setAvailable((Boolean) value);
                    }
                    case "pricePerNight" -> {
                        if (!(value instanceof Number)) return error(HttpStatus.BAD_REQUEST, "Invalid pricePerNight");
                        room.setPricePerNight(((Number) value).doubleValue());
                    }
                    case "capacity" -> {
                        if (!(value instanceof Number) ||
                                ((Number) value).doubleValue() != ((Number) value).intValue())
                            return error(HttpStatus.BAD_REQUEST, "Invalid capacity");
                        room.setCapacity(((Number) value).intValue());
                    }
                }
            }
        } catch (ClassCastException ex) {
            return error(HttpStatus.BAD_REQUEST, "Invalid field type");
        }

        String problem = validateRoom(room);
        if (problem != null) return error(HttpStatus.BAD_REQUEST, problem);
        if (roomNumberExists(room.getRoomNumber(), id))
            return error(HttpStatus.CONFLICT, "roomNumber already exists");
        rooms.put(id, room);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (rooms.remove(id) == null) return error(HttpStatus.NOT_FOUND, "Record not found");
        return ResponseEntity.noContent().build();
    }
}
