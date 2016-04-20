package com.uniform_imperials.herald.fragments.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sean Johnson on 4/19/2016.
 */
public class AppSettingContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<AppSettingItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, AppSettingItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createAppSettingItem(i));
        }
    }

    private static void addItem(AppSettingItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static AppSettingItem createAppSettingItem(int position) {
        return new AppSettingItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class AppSettingItem {
        public final String id;
        public final String content;
        public final String details;

        public AppSettingItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
