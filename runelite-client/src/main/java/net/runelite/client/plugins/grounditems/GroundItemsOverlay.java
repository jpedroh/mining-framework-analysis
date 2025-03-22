package net.runelite.client.plugins.grounditems;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import static net.runelite.client.plugins.grounditems.GroundItemsPlugin.MAX_QUANTITY;
import net.runelite.client.plugins.grounditems.config.DespawnTimerMode;
import static net.runelite.client.plugins.grounditems.config.ItemHighlightMode.MENU;
import static net.runelite.client.plugins.grounditems.config.ItemHighlightMode.NONE;
import net.runelite.client.plugins.grounditems.config.PriceDisplayMode;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.QuantityFormatter;

public class GroundItemsOverlay extends Overlay {
  private static final int MAX_DISTANCE = 2500;

  private static final int OFFSET_Z = 20;

  private static final int STRING_GAP = 15;

  private static final int RECTANGLE_SIZE = 8;

  private static final Color PUBLIC_TIMER_COLOR = Color.YELLOW;

  private static final Color PRIVATE_TIMER_COLOR = Color.GREEN;

  private static final int TIMER_OVERLAY_DIAMETER = 10;

  private static final Duration DESPAWN_TIME_INSTANCE = Duration.ofMinutes(30);

  private static final Duration DESPAWN_TIME_LOOT = Duration.ofMinutes(2);

  private static final Duration DESPAWN_TIME_DROP = Duration.ofMinutes(3);

  private static final Duration DESPAWN_TIME_TABLE = Duration.ofMinutes(10);

  private static final int KRAKEN_REGION = 9116;

  private static final int CLAN_HALL_REGION = 6997;

  private static final int KBD_NMZ_REGION = 9033;

  private static final int ZILYANA_REGION = 11602;

  private static final int GRAARDOR_REGION = 11347;

  private static final int KRIL_TSUTSAROTH_REGION = 11603;

  private static final int KREEARRA_REGION = 11346;

  private static final int NEX_REGION = 11601;

  private static final int NIGHTMARE_REGION = 15515;

  private static final int TEMPOROSS_REGION = 12078;

  private final Client client;

  private final GroundItemsPlugin plugin;

  private final GroundItemsConfig config;

  private final StringBuilder itemStringBuilder = new StringBuilder();

  private final BackgroundComponent backgroundComponent = new BackgroundComponent();

  private final TextComponent textComponent = new TextComponent();

  private final ProgressPieComponent progressPieComponent = new ProgressPieComponent();

  private final Map<WorldPoint, Integer> offsetMap = new HashMap<>();

  @Inject private GroundItemsOverlay(Client client, GroundItemsPlugin plugin, GroundItemsConfig config) {
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.ABOVE_SCENE);
    this.client = client;
    this.plugin = plugin;
    this.config = config;
  }

  @Override public Dimension render(Graphics2D graphics) {
    final boolean dontShowOverlay = (config.itemHighlightMode() == MENU || config.itemHighlightMode() == NONE || plugin.isHideAll()) && !plugin.isHotKeyPressed();
    if (dontShowOverlay && !config.highlightTiles()) {
      return null;
    }
    final FontMetrics fm = graphics.getFontMetrics();
    final Player player = client.getLocalPlayer();
    if (player == null) {
      return null;
    }
    offsetMap.clear();
    final LocalPoint localLocation = player.getLocalLocation();
    final Point mousePos = client.getMouseCanvasPosition();
    Collection<GroundItem> groundItemList = plugin.getCollectedGroundItems().values();
    GroundItem topGroundItem = null;
    if (plugin.isHotKeyPressed()) {
      groundItemList = new ArrayList<>(groundItemList);
      final java.awt.Point awtMousePos = new java.awt.Point(mousePos.getX(), mousePos.getY());
      GroundItem groundItem = null;
      for (GroundItem item : groundItemList) {
        item.setOffset(offsetMap.compute(item.getLocation(), (k, v) -> v != null ? v + 1 : 0));
        if (groundItem != null) {
          continue;
        }
        if (plugin.getTextBoxBounds() != null && item.equals(plugin.getTextBoxBounds().getValue()) && plugin.getTextBoxBounds().getKey().contains(awtMousePos)) {
          groundItem = item;
          continue;
        }
        if (plugin.getHiddenBoxBounds() != null && item.equals(plugin.getHiddenBoxBounds().getValue()) && plugin.getHiddenBoxBounds().getKey().contains(awtMousePos)) {
          groundItem = item;
          continue;
        }
        if (plugin.getHighlightBoxBounds() != null && item.equals(plugin.getHighlightBoxBounds().getValue()) && plugin.getHighlightBoxBounds().getKey().contains(awtMousePos)) {
          groundItem = item;
        }
      }
      if (groundItem != null) {
        groundItemList.remove(groundItem);
        groundItemList.add(groundItem);
        topGroundItem = groundItem;
      }
    }
    plugin.setTextBoxBounds(null);
    plugin.setHiddenBoxBounds(null);
    plugin.setHighlightBoxBounds(null);
    final boolean onlyShowLoot = config.onlyShowLoot();
    final DespawnTimerMode groundItemTimers = config.groundItemTimers();
    final boolean outline = config.textOutline();
    for (GroundItem item : groundItemList) {
      final LocalPoint groundPoint = LocalPoint.fromWorld(client, item.getLocation());
      if (groundPoint == null || localLocation.distanceTo(groundPoint) > MAX_DISTANCE || (onlyShowLoot && !item.isMine())) {
        continue;
      }
      final GroundItemsPlugin.PriceHighlight highlighted = plugin.getHighlighted(new NamedQuantity(item), item.getGePrice(), item.getHaPrice());
      final Color hidden = plugin.getHidden(new NamedQuantity(item), item.getGePrice(), item.getHaPrice(), item.isTradeable());
      if (highlighted == null && !plugin.isHotKeyPressed()) {
        if (hidden != null) {
          continue;
        }
        if (config.showHighlightedOnly()) {
          continue;
        }
      }
      final Color color = plugin.getItemColor(highlighted, hidden);
      final Color fillColor = plugin.getItemFillColor(highlighted, hidden);
      if (config.highlightTiles()) {
        final Polygon poly = Perspective.getCanvasTilePoly(client, groundPoint, item.getHeight());
        if (poly != null) {
          OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke(2));
        }
      }
      if (dontShowOverlay) {
        continue;
      }
      itemStringBuilder.append(item.getName());
      if (item.getQuantity() > 1) {
        if (item.getQuantity() >= MAX_QUANTITY) {
          itemStringBuilder.append(" (Lots!)");
        } else {
          itemStringBuilder.append(" (").append(QuantityFormatter.quantityToStackSize(item.getQuantity())).append(')');
        }
      }
      if (config.priceDisplayMode() == PriceDisplayMode.BOTH) {
        if (item.getGePrice() > 0) {
          itemStringBuilder.append(" (GE: ").append(QuantityFormatter.quantityToStackSize(item.getGePrice())).append(" gp)");
        }
        if (item.getHaPrice() > 0) {
          itemStringBuilder.append(" (HA: ").append(QuantityFormatter.quantityToStackSize(item.getHaPrice())).append(" gp)");
        }
      } else {
        if (config.priceDisplayMode() != PriceDisplayMode.OFF) {
          final int price = config.priceDisplayMode() == PriceDisplayMode.GE ? item.getGePrice() : item.getHaPrice();
          if (price > 0) {
            itemStringBuilder.append(" (").append(QuantityFormatter.quantityToStackSize(price)).append(" gp)");
          }
        }
      }
      final String itemString = itemStringBuilder.toString();
      itemStringBuilder.setLength(0);
      final Point textPoint = Perspective.getCanvasTextLocation(client, graphics, groundPoint, itemString, item.getHeight() + OFFSET_Z);
      if (textPoint == null) {
        continue;
      }
      final int offset = plugin.isHotKeyPressed() ? item.getOffset() : offsetMap.compute(item.getLocation(), (k, v) -> v != null ? v + 1 : 0);
      final int textX = textPoint.getX();
      final int textY = textPoint.getY() - (STRING_GAP * offset);
      if (plugin.isHotKeyPressed()) {
        final int stringWidth = fm.stringWidth(itemString);
        final int stringHeight = fm.getHeight();
        int x = textX - 2;
        int y = textY - stringHeight - 2;
        int width = stringWidth + 4;
        int height = stringHeight + 4;
        final Rectangle itemBounds = new Rectangle(x, y, width, height);
        x += width + 2;
        y = textY - (RECTANGLE_SIZE + stringHeight) / 2;
        width = height = RECTANGLE_SIZE;
        final Rectangle itemHiddenBox = new Rectangle(x, y, width, height);
        x += width + 2;
        final Rectangle itemHighlightBox = new Rectangle(x, y, width, height);
        boolean mouseInBox = itemBounds.contains(mousePos.getX(), mousePos.getY());
        boolean mouseInHiddenBox = itemHiddenBox.contains(mousePos.getX(), mousePos.getY());
        boolean mouseInHighlightBox = itemHighlightBox.contains(mousePos.getX(), mousePos.getY());
        if (mouseInBox) {
          plugin.setTextBoxBounds(new SimpleEntry<>(itemBounds, item));
        } else {
          if (mouseInHiddenBox) {
            plugin.setHiddenBoxBounds(new SimpleEntry<>(itemHiddenBox, item));
          } else {
            if (mouseInHighlightBox) {
              plugin.setHighlightBoxBounds(new SimpleEntry<>(itemHighlightBox, item));
            }
          }
        }
        boolean topItem = topGroundItem == item;
        if (topItem && (mouseInBox || mouseInHiddenBox || mouseInHighlightBox)) {
          backgroundComponent.setRectangle(itemBounds);
          backgroundComponent.render(graphics);
        }
        drawRectangle(graphics, itemHiddenBox, topItem && mouseInHiddenBox ? Color.RED : color, hidden != null, true);
        drawRectangle(graphics, itemHighlightBox, topItem && mouseInHighlightBox ? Color.GREEN : color, highlighted != null, false);
      }
      if (groundItemTimers == DespawnTimerMode.PIE || plugin.isHotKeyPressed()) {
        drawTimerPieOverlay(graphics, textX, textY, item);
      } else {
        if (groundItemTimers == DespawnTimerMode.SECONDS || groundItemTimers == DespawnTimerMode.TICKS) {
          Instant despawnTime = calculateDespawnTime(item);
          Color timerColor = getItemTimerColor(item);
          if (despawnTime != null && timerColor != null) {
            long despawnTimeMillis = despawnTime.toEpochMilli() - Instant.now().toEpochMilli();
            final String timerText;
            if (groundItemTimers == DespawnTimerMode.SECONDS) {
              timerText = String.format(" - %.1f", despawnTimeMillis / 1000f);
            } else {
              timerText = String.format(" - %d", despawnTimeMillis / 600);
            }
            textComponent.setText(timerText);
            textComponent.setColor(timerColor);
            textComponent.setOutline(outline);
            textComponent.setPosition(new java.awt.Point(textX + fm.stringWidth(itemString), textY));
            textComponent.render(graphics);
          }
        }
      }
      textComponent.setText(itemString);
      textComponent.setColor(color);
      textComponent.setOutline(outline);
      textComponent.setPosition(new java.awt.Point(textX, textY));
      textComponent.render(graphics);
    }
    return null;
  }

  private Instant calculateDespawnTime(GroundItem groundItem) {
    if (groundItem.getLootType() != LootType.PVM && groundItem.getLootType() != LootType.DROPPED && groundItem.getLootType() != LootType.TABLE) {
      return null;
    }
    Instant spawnTime = groundItem.getSpawnTime();
    if (spawnTime == null) {
      return null;
    }
    final Instant despawnTime;
    Instant now = Instant.now();
    if (client.isInInstancedRegion()) {
      final int playerRegionID = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
      if (playerRegionID == KRAKEN_REGION) {
        return null;
      } else {
        if (playerRegionID == KBD_NMZ_REGION) {
          if (client.getLocalPlayer().getWorldLocation().getPlane() == 0) {
            despawnTime = spawnTime.plus(groundItem.getLootType() == LootType.DROPPED ? DESPAWN_TIME_DROP : DESPAWN_TIME_LOOT);
          } else {
            if (groundItem.getLootType() == LootType.DROPPED) {
              return null;
            } else {
              despawnTime = spawnTime.plus(DESPAWN_TIME_LOOT);
            }
          }
        } else {
          if (playerRegionID == ZILYANA_REGION || playerRegionID == GRAARDOR_REGION || playerRegionID == KRIL_TSUTSAROTH_REGION || playerRegionID == KREEARRA_REGION || playerRegionID == NEX_REGION || playerRegionID == NIGHTMARE_REGION || playerRegionID == TEMPOROSS_REGION || playerRegionID == CLAN_HALL_REGION) {
            despawnTime = spawnTime.plus(groundItem.getLootType() == LootType.DROPPED ? DESPAWN_TIME_DROP : DESPAWN_TIME_LOOT);
          } else {
            despawnTime = spawnTime.plus(DESPAWN_TIME_INSTANCE);
          }
        }
      }
    } else {
      switch (groundItem.getLootType()) {
        case DROPPED:
        despawnTime = spawnTime.plus(DESPAWN_TIME_DROP);
        break;
        case TABLE:
        despawnTime = spawnTime.plus(DESPAWN_TIME_TABLE);
        break;
        default:
        despawnTime = spawnTime.plus(DESPAWN_TIME_LOOT);
        break;
      }
    }
    if (now.isBefore(spawnTime) || now.isAfter(despawnTime)) {
      return null;
    }
    return despawnTime;
  }

  private Color getItemTimerColor(GroundItem groundItem) {
    if (groundItem.getLootType() != LootType.PVM && groundItem.getLootType() != LootType.DROPPED && groundItem.getLootType() != LootType.TABLE) {
      return null;
    }
    final Instant spawnTime = groundItem.getSpawnTime();
    if (spawnTime == null) {
      return null;
    }
    final Instant now = Instant.now();
    if (client.isInInstancedRegion() || spawnTime.plus(1, ChronoUnit.MINUTES).isAfter(now)) {
      return PRIVATE_TIMER_COLOR;
    } else {
      return PUBLIC_TIMER_COLOR;
    }
  }

  private void drawTimerPieOverlay(Graphics2D graphics, int textX, int textY, GroundItem groundItem) {
    Instant now = Instant.now();
    Instant spawnTime = groundItem.getSpawnTime();
    Instant despawnTime = calculateDespawnTime(groundItem);
    Color fillColor = getItemTimerColor(groundItem);
    if (spawnTime == null || despawnTime == null || fillColor == null) {
      return;
    }
    float percent = (float) (now.toEpochMilli() - spawnTime.toEpochMilli()) / (despawnTime.toEpochMilli() - spawnTime.toEpochMilli());
    progressPieComponent.setDiameter(TIMER_OVERLAY_DIAMETER);
    int x = textX - TIMER_OVERLAY_DIAMETER;
    int y = textY - TIMER_OVERLAY_DIAMETER / 2;
    progressPieComponent.setPosition(new Point(x, y));
    progressPieComponent.setFill(fillColor);
    progressPieComponent.setBorderColor(fillColor);
    progressPieComponent.setProgress(1 - percent);
    progressPieComponent.render(graphics);
  }

  private void drawRectangle(Graphics2D graphics, Rectangle rect, Color color, boolean inList, boolean hiddenBox) {
    graphics.setColor(Color.BLACK);
    graphics.drawRect(rect.x + 1, rect.y + 1, rect.width, rect.height);
    graphics.setColor(color);
    graphics.draw(rect);
    if (inList) {
      graphics.fill(rect);
    }
    graphics.setColor(Color.WHITE);
    graphics.drawLine(rect.x + 2, rect.y + (rect.height / 2), rect.x + rect.width - 2, rect.y + (rect.height / 2));
    if (!hiddenBox) {
      graphics.drawLine(rect.x + (rect.width / 2), rect.y + 2, rect.x + (rect.width / 2), rect.y + rect.height - 2);
    }
  }
}