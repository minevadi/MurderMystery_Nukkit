package cn.lanink.murdermystery.listener.base;

import cn.lanink.murdermystery.MurderMystery;
import cn.lanink.murdermystery.room.base.BaseRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public abstract class BaseMurderMysteryListener implements IMurderMysteryListener {

    protected MurderMystery murderMystery;
    private String listenerName;
    protected ConcurrentHashMap<String, BaseRoom> listenerRooms = new ConcurrentHashMap<>();

    public BaseMurderMysteryListener(MurderMystery murderMystery) {
        this.murderMystery = murderMystery;
    }

    @Override
    public void setListenerName(String name) {
        this.listenerName = name;
    }

    @Override
    public String getListenerName() {
        return this.listenerName;
    }

    public Map<String, BaseRoom> getListenerRooms() {
        return this.listenerRooms;
    }

    public void addListenerRoom(BaseRoom baseRoom) {
        this.listenerRooms.put(baseRoom.getLevelName(), baseRoom);
    }

    public void removeListenerRoom(String roomName) {
        this.listenerRooms.remove(roomName);
    }

    public void removeListenerRoom(BaseRoom baseRoom) {
        this.listenerRooms.remove(baseRoom.getLevelName());
    }

}
