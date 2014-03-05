package com.gruszecm.tjconsole.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.ObjectName;

import jline.Completor;

import com.gruszecm.tjconsole.Output;
import com.gruszecm.tjconsole.TJContext;

public class BeanCommand extends AbstractCommand implements Completor {
	private static final String PREFIX = "\\b";
	
	
	public BeanCommand(TJContext ctx, Output output) {
		super(ctx, output);
	}
	
	@Override
	public void action(String input)	throws Exception {
		StringBuilder sb = new StringBuilder();
		String bname = extractURL(input);
		if (bname.length() == 0) {
			for(String bn : names()) {
				sb.append("\t* ").append(bn).append('\n');
				output.outInfo(sb.toString());
			}
		} else {
			output.outInfo("Connecting to bean " + bname + "...");
			ObjectName objectName = new ObjectName(bname);
			if (ctx.getServer().isRegistered(objectName)) {
				output.outInfo("Connected to bean " + bname);
				ctx.setObjectName(objectName);
			} else {
				output.outError("Bean " + bname + " not found.");
				ctx.setObjectName(null);
			}
		}
	}

	@Override
	public boolean matches(String input) {
		return input.startsWith(PREFIX);
	}

	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {
		if (matches(buffer) && ctx.isConnected()) {
			String urlprefix = extractURL(buffer);
			try {
				for(String s : names()) {
					if (s.startsWith(urlprefix)) candidates.add(s);
				}
			} catch (IOException e) {	}
			return PREFIX.length();
		} else {
			return -1;
		}
	}

	@Override
	public CommandHelp getHelp() {
		return new CommandHelp("Select bean.", "\\b beanName", "\\b"){
			private static final long serialVersionUID = 7806368129076996728L;
			@Override
			public boolean isProper(TJContext ctx) {
				return ctx.isConnected();
			}
		};
	}
	
	private static String extractURL(String in) {
		return in.substring(PREFIX.length()).trim();
	}
	
	private Collection<String> names() throws IOException {
		ArrayList<String> l = new ArrayList<String>();
		for(Object on : ctx.getServer().queryNames(null, null)) {
			l.add(on.toString());
		}
		return l;
	}

}
