package dev.rivalcounter.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class RivalScreen extends Screen {
    private static final int PAGE_SIZE = 10;
    private int page;

    public RivalScreen() {
        super(Component.translatable("screen.rivalcounter.title"));
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(430, width - 28);
        int left = (width - panelWidth) / 2;
        int bottom = height - 38;
        addRenderableWidget(Button.builder(Component.translatable("screen.rivalcounter.previous"), button -> {
            page = Math.max(0, page - 1);
            rebuildWidgets();
        }).bounds(left + 12, bottom, 86, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.rivalcounter.next"), button -> {
            page++;
            rebuildWidgets();
        }).bounds(left + panelWidth - 98, bottom, 86, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(left + (panelWidth - 80) / 2, bottom, 80, 20).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        int panelWidth = Math.min(430, width - 28);
        int left = (width - panelWidth) / 2;
        graphics.fill(left, 20, left + panelWidth, height - 28, 0xF0161D17);
        graphics.fill(left, 20, left + panelWidth, 22, 0xFF85B85A);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int panelWidth = Math.min(430, width - 28);
        int left = (width - panelWidth) / 2;
        List<RivalRecord> records = RivalStore.sortedRecords();
        int pageCount = Math.max(1, (records.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        page = Math.min(page, pageCount - 1);

        graphics.text(font, "RAKİP SAYACI", left + 14, 34, 0xFFF3F6EF, true);
        graphics.text(font, "Seni kimin öldürdüğü ve bire bir PvP sayıları", left + 14, 53, 0xFFB5C6A5, true);
        graphics.text(font, "OYUNCU", left + 18, 78, 0xFF98A392, true);
        graphics.text(font, "SEN ÖLDÜRDÜN", left + panelWidth - 180, 78, 0xFF98A392, true);
        graphics.text(font, "SENİ ÖLDÜRDÜ", left + panelWidth - 86, 78, 0xFF98A392, true);

        int first = page * PAGE_SIZE;
        int end = Math.min(records.size(), first + PAGE_SIZE);
        if (records.isEmpty()) {
            graphics.text(font, "Henüz PvP kaydı yok.", left + 18, 108, 0xFFB8C0B5, true);
            graphics.text(font, "K tuşuyla bu listeyi açabilirsin.", left + 18, 126, 0xFF8D998B, true);
        }
        for (int i = first; i < end; i++) {
            RivalRecord record = records.get(i);
            int y = 96 + (i - first) * 23;
            if ((i - first) % 2 == 0) graphics.fill(left + 10, y - 2, left + panelWidth - 10, y + 17, 0x223C4D3A);
            graphics.text(font, record.name, left + 18, y, 0xFFE9EEE5, true);
            graphics.text(font, Integer.toString(record.kills), left + panelWidth - 158, y, 0xFF9FDB78, true);
            graphics.text(font, Integer.toString(record.deaths), left + panelWidth - 68, y, 0xFFE6A17C, true);
        }
        graphics.text(font, (page + 1) + " / " + pageCount, left + panelWidth / 2 - 14,
                height - 59, 0xFF98A392, true);
    }
}
