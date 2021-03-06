package eu.the5zig.mod.chat.network.packets;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.Announcement;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PacketAnnouncementList implements Packet {

	private List<Announcement> announcements;

	@Override
	public void read(ByteBuf buffer) throws IOException {
		int size = PacketBuffer.readVarIntFromBuffer(buffer);
		announcements = new ArrayList<Announcement>(size);
		for (int i = 0; i < size; i++) {
			announcements.add(new Announcement(buffer.readLong(), PacketBuffer.readString(buffer)));
		}
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);

		The5zigMod.getConversationManager().setAnnouncementMessages(announcements);
	}
}
