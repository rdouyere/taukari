package net.airvantage.taukari.shell;

import java.util.ArrayList;
import java.util.List;

import asg.cliche.OutputConverter;

/**
 * View of a shell command. It may contain multiple messages that are either a
 * simple (info) message or and error message.
 * <p>
 * This view also defines the {@link OutputConverter} that is able to display
 * the messages as text.
 * </p>
 */
public class ShellView {

	private final List<Message> msgs = new ArrayList<Message>();

	public ShellView() {

	}

	public ShellView(List<String> msgs) {
		for (String msg : msgs) {
			addMsg(msg);
		}
	}

	public void addMsg(String msg) {
		msgs.add(new Message(false, msg));
	}

	public void addMsg(Object o) {
		if (o != null) {
			msgs.add(new Message(false, o.toString()));
		}
	}

	public void addErr(String msg) {
		msgs.add(new Message(true, msg));
	}

	protected class Message {
		boolean isError;
		String msg;

		public Message(boolean isError, String msg) {
			super();
			this.isError = isError;
			this.msg = msg;
		}
	}

	protected static class ReturnViewConvertor implements OutputConverter {
		@Override
		public Object convertOutput(Object toBeFormatted) {
			if (toBeFormatted instanceof ShellView) {
				StringBuilder sb = new StringBuilder();
				for (Message msg : ((ShellView) toBeFormatted).msgs) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					if (msg.isError) {
						sb.append("Err: ");
					}
					sb.append(msg.msg);
				}
				return sb.toString();
			} else {
				return toBeFormatted.toString();
			}
		}
	}
}