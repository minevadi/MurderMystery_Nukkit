package cn.lanink.murdermystery.listener.classic;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.listener.base.BaseMurderMysteryListener;
import cn.lanink.murdermystery.room.base.BaseRoom;
import cn.lanink.murdermystery.room.base.IRoomStatus;
import cn.lanink.murdermystery.tasks.game.ScanTask;
import cn.lanink.murdermystery.tasks.game.SwordMoveTask;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

/**
 * 游戏监听器
 *
 * @author lt_name
 */
public class ClassicGameListener extends BaseMurderMysteryListener {

    public ClassicGameListener(MurderMystery murderMystery) {
        super(murderMystery);
    }

    /**
     * 玩家手持物品事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (player == null || item == null) {
            return;
        }
        BaseRoom room = this.getListenerRooms().get(player.getLevel().getFolderName());
        if (room == null) {
            return;
        }
        CompoundTag tag = item.hasCompoundTag() ? item.getNamedTag() : null;
        if (room.getStatus() == BaseRoom.ROOM_STATUS_GAME && room.isPlaying(player) && room.getPlayers(player) == 3) {
            if (tag != null && tag.getBoolean("isMurderItem") && tag.getInt("MurderType") == 2) {
                if (room.effectCD < 1) {
                    Effect effect = Effect.getEffect(1);
                    effect.setAmplifier(2);
                    effect.setVisible(false);
                    effect.setDuration(40);
                    player.addEffect(effect);
                    room.effectCD = 10;
                }
            }else {
                player.removeEffect(1);
            }
        }
    }

    /**
     * 玩家点击事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player == null || block == null) {
            return;
        }
        BaseRoom room = this.getListenerRooms().get(player.getLevel().getFolderName());
        if (room == null || !room.isPlaying(player)) {
            return;
        }
        if (room.getStatus() == IRoomStatus.ROOM_STATUS_GAME &&
                event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR &&
                room.getPlayers(player) == 3) {
            CompoundTag tag = player.getInventory().getItemInHand() == null ? null : player.getInventory().getItemInHand().getNamedTag();
            if (tag != null && tag.getBoolean("isMurderItem")) {
                switch (tag.getInt("MurderType")) {
                    case 2:
                        if (room.swordCD < 1) {
                            Server.getInstance().getScheduler().scheduleAsyncTask(this.murderMystery,
                                    new SwordMoveTask(room, player));
                            room.swordCD = 5;
                        }else {
                            player.sendMessage(this.murderMystery.getLanguage(player).useItemSwordCD);
                        }
                        break;
                    case 3:
                        if (room.scanCD < 1) {
                            Server.getInstance().getScheduler().scheduleTask(this.murderMystery,
                                    new ScanTask(this.murderMystery, room, player));
                            room.scanCD = 60;
                        }else {
                            player.sendMessage(this.murderMystery.getLanguage(player).useItemScanCD);
                        }
                        break;
                }
            }
        }
    }

}
