package eu.the5zig.mod.chat;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.ConversationChat;
import eu.the5zig.mod.chat.network.packets.PacketTyping;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.gui.GuiConversations;
import eu.the5zig.mod.manager.ChatTypingManager;
import eu.the5zig.mod.util.Display;

import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ChatTypingListener {

	/**
	 * Listeners for every client tick. If the Client is typing something into the Chat-Field, the Friend will receive a Packet, that shows that the Client is currently typing a message.
	 */
	@EventHandler
	public void onTick(TickEvent event) {
		ChatTypingManager manager = The5zigMod.getDataManager().getChatTypingManager();
		if (!The5zigMod.getNetworkManager().isConnected()) {
			unType();
			return;
		}

		if (!(The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations)) {
			unType();
			return;
		}
		GuiConversations gui = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
		if (!(gui.getSelectedConversation() instanceof ConversationChat) || (manager.getTypingTo() != null && !((ConversationChat) gui.getSelectedConversation()).getFriendUUID().equals(
				manager.getTypingTo()))) {
			unType();
			return;
		}
		if (!Display.isActive()) {
			unType();
			return;
		}

		ConversationChat conversation = (ConversationChat) gui.getSelectedConversation();
		boolean isTextfieldEmpty = gui.getTextfieldById(300).getText().isEmpty(); // Chat Field has id #300
		if (manager.getTypingTo() == null && !isTextfieldEmpty) {
			type(conversation.getFriendUUID());
		} else if (manager.getTypingTo() != null && isTextfieldEmpty) {
			unType();
		}
	}

	/**
	 * Checks if the Player is currently Typing to a Player. If so, we send a PacketTyping with typing = false to unregister the Friend.
	 */
	private void unType() {
		ChatTypingManager manager = The5zigMod.getDataManager().getChatTypingManager();
		if (manager.getTypingTo() != null) {
			The5zigMod.getNetworkManager().sendPacket(new PacketTyping(manager.getTypingTo(), false));
			manager.setTypingTo(null); // Reset the Friend.
		}
	}

	/**
	 * Checks if the Player is currently Typing to a Player. If he isn't, we send a PacketTyping with typing = true to register the Friend.
	 *
	 * @param friend The Friend of the Player.
	 */
	private void type(UUID friend) {
		ChatTypingManager manager = The5zigMod.getDataManager().getChatTypingManager();
		if (manager.getTypingTo() == null) {
			manager.setTypingTo(friend);
			The5zigMod.getNetworkManager().sendPacket(new PacketTyping(manager.getTypingTo(), true));
		}
	}

}
