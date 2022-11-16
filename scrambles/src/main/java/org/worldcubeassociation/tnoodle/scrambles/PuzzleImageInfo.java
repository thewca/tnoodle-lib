package org.worldcubeassociation.tnoodle.scrambles;

import org.worldcubeassociation.tnoodle.svglite.Color;
import org.worldcubeassociation.tnoodle.svglite.Dimension;
import java.util.HashMap;
import java.util.Map;

public class PuzzleImageInfo {
    public Map<String, Color> colorScheme;
    public Dimension size;

    public PuzzleImageInfo() {}
    public PuzzleImageInfo(Puzzle p) {
        colorScheme = p.getDefaultColorScheme();
        size = p.getPreferredSize();
    }

    public Map<String, Object> toJsonable() {
        Map<String, Object> jsonable = new HashMap<>();
        Map<String, Integer> dim = new HashMap<>();
        dim.put("width", size.width);
        dim.put("height", size.height);
        jsonable.put("size", dim);

        Map<String, String> jsonColorScheme = new HashMap<>();
        for(String key : this.colorScheme.keySet()) {
            jsonColorScheme.put(key, this.colorScheme.get(key).toHex());
        }
        jsonable.put("colorScheme", jsonColorScheme);

        return jsonable;
    }
}
