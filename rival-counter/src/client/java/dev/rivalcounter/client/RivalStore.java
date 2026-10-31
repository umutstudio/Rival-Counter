package dev.rivalcounter.client;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RivalStore {
    private static final Map<String, RivalRecord> RECORDS = new LinkedHashMap<>();
    private static String serverKey = "";

    private RivalStore() { }

    public static void ensureServer(Minecraft client) {
        if (client.player == null) return;
        String nextKey = client.getCurrentServer() == null ? "singleplayer" : client.getCurrentServer().ip;
        if (nextKey == null || nextKey.isBlank()) nextKey = "unknown-server";
        if (nextKey.equals(serverKey)) return;
        save();
        serverKey = nextKey;
        RECORDS.clear();
        load();
    }

    public static void recordKill(String victim) {
        update(victim, true);
    }

    public static void recordDeath(String killer) {
        update(killer, false);
    }

    private static void update(String name, boolean kill) {
        if (serverKey.isBlank() || name.isBlank()) return;
        RivalRecord record = RECORDS.computeIfAbsent(name, n -> new RivalRecord(n, 0, 0));
        if (kill) record.kills++;
        else record.deaths++;
        save();
    }

    public static List<RivalRecord> sortedRecords() {
        List<RivalRecord> rows = new ArrayList<>(RECORDS.values());
        rows.sort(Comparator.comparingInt(RivalRecord::encounters).reversed()
                .thenComparing(r -> r.name.toLowerCase()));
        return rows;
    }

    private static Path file() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config/rival-counter")
                .resolve(fileName(serverKey) + ".txt");
    }

    private static String fileName(String value) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 8; i++) result.append(String.format("%02x", hash[i]));
            return result.toString();
        } catch (NoSuchAlgorithmException impossible) {
            return Integer.toHexString(value.hashCode());
        }
    }

    private static void load() {
        if (serverKey.isBlank()) return;
        Path path = file();
        if (!Files.exists(path)) return;
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String[] fields = line.split("\\t", -1);
                if (fields.length != 3) continue;
                String name = fields[0];
                try {
                    RECORDS.put(name, new RivalRecord(name, Integer.parseInt(fields[1]), Integer.parseInt(fields[2])));
                } catch (NumberFormatException ignored) {
                    // Skip a damaged record and keep the remaining history.
                }
            }
        } catch (IOException exception) {
            System.err.println("[Rival Counter] Could not read local history: " + exception.getMessage());
        }
    }

    private static void save() {
        if (serverKey.isBlank()) return;
        Path path = file();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            for (RivalRecord record : RECORDS.values()) {
                lines.add(record.name + "\t" + record.kills + "\t" + record.deaths);
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[Rival Counter] Could not save local history: " + exception.getMessage());
        }
    }
}
