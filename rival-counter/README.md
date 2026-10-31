# Rakip Sayacı

Minecraft Java 26.3 için istemci taraflı Fabric modu. PvP ölüm mesajlarından rakip adını yakalar ve her oyuncuya karşı öldürme/ölüm sayını yerel olarak saklar.

## Kullanım

- Modu `.minecraft/mods` klasörüne koy.
- Oyunda **K** tuşuyla Rakip Sayacı ekranını aç. Tuşu Kontroller menüsünden değiştirebilirsin.
- Kayıtlar sunucuya özel olarak `.minecraft/config/rival-counter/` altında tutulur.

Sunucuya mod kurulması gerekmez. Sayaç, sunucunun standart PvP ölüm mesajını göndermesine bağlıdır; özel mesaj formatı kullanan veya ölüm mesajlarını kapatan sunucularda kayıt alınmayabilir.

## Derleme

```sh
./gradlew remapJar
```

Çıktı `build/libs/rival-counter-1.0.0.jar` konumundadır.
