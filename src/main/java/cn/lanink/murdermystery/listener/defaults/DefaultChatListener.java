package cn.lanink.murdermystery.listener.defaults;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.listener.base.BaseMurderMysteryListener;
import cn.lanink.murdermystery.room.base.BaseRoom;
import cn.lanink.murdermystery.room.base.IRoomStatus;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;

/**
 * @author lt_name
 */
public class DefaultChatListener extends BaseMurderMysteryListener {

    public DefaultChatListener(MurderMystery murderMystery) {
        super(murderMystery);
    }

    /**
     * 玩家执行命令事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (player == null || message == null) {
            return;
        }
        BaseRoom room = this.getListenerRooms().get(player.getLevel().getFolderName());
        if (room == null || !room.isPlaying(player)) {
            return;
        }
        if (message.startsWith(this.murderMystery.getCmdUser(), 1) ||
                message.startsWith(this.murderMystery.getCmdAdmin(), 1)) {
            return;
        }
        for (String string : this.murderMystery.getCmdUserAliases()) {
            if (message.startsWith(string, 1)) {
                return;
            }
        }
        for (String string : this.murderMystery.getCmdAdminAliases()) {
            if (message.startsWith(string, 1)) {
                return;
            }
        }
        event.setMessage("");
        event.setCancelled(true);
        player.sendMessage(this.murderMystery.getLanguage(player).useCmdInRoom);
    }

    /**
     * 发送消息事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (player == null || message == null) {
            return;
        }
        BaseRoom room = this.getListenerRooms().get(player.getLevel().getFolderName());
        if (room == null || (!room.isPlaying(player) && !room.isSpectator(player))) {
            return;
        }
        if (room.isSpectator(player)) {
            for (Player p : room.getSpectatorPlayers()) {
                p.sendMessage(this.murderMystery.getLanguage(p).playerSpectatorChat
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }
        }else if (room.getPlayers(player) == 0) {
            HashSet<Player> players = new HashSet<>(room.getPlayers().keySet());
            players.addAll(room.getSpectatorPlayers());
            for (Player p : players) {
                if (room.getPlayers(p) == 0) {
                    if (room.getStatus() == IRoomStatus.ROOM_STATUS_GAME) {
                        p.sendMessage(this.murderMystery.getLanguage(p).playerDeathChat
                                .replace("%player%", player.getName())
                                .replace("%message%", message));
                    }else {
                        p.sendMessage(this.murderMystery.getLanguage(p).playerChat
                                .replace("%player%", player.getName())
                                .replace("%message%", message));
                    }
                }
            }
        }else {
            for (Player p : room.getPlayers().keySet()) {
                p.sendMessage(this.murderMystery.getLanguage(p).playerChat
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }
            for (Player p : room.getSpectatorPlayers()) {
                p.sendMessage(this.murderMystery.getLanguage(p).playerChat
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }
        }
        event.setMessage("");
        event.setCancelled(true);
    }

}
