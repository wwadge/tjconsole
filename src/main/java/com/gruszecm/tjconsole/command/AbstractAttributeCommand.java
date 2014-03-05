package com.gruszecm.tjconsole.command;

import java.util.List;

import javax.management.MBeanAttributeInfo;

import jline.Completor;

import com.gruszecm.tjconsole.Output;
import com.gruszecm.tjconsole.TJContext;

public abstract class AbstractAttributeCommand extends AbstractCommand implements Completor {
	private final String PREFIX = getPrefix().toUpperCase();

	public AbstractAttributeCommand(TJContext context, Output output) {
		super(context, output);
	}

	@Override
	public void action(String input) throws Exception {
		String attribute = attribute(input);
		if (ctx.getEnviroment().containsKey(attribute)) {
			actionEvn(input, attribute);
		} else {
			actionBean(input, attribute);
		}
	}
	
	protected abstract void actionBean(String input, String attribute) throws Exception;
	
	protected abstract void actionEvn(String input, String attribute) throws Exception;
	
	
	protected abstract String getPrefix();


	@Override
	public final boolean matches(String input) {
		return input.toUpperCase().startsWith(PREFIX);
	}

	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {
		String cSufix = getCSuffix();
		if (matches(buffer)) {
			String att = attribute(buffer);
			try {
				for(MBeanAttributeInfo ai : ctx.getAttributes()) {
					if (ai.getName().startsWith(att) && correctAttribute(ai)) candidates.add(ai.getName() + cSufix);
				}
			} catch (Exception e) {
				throw new RuntimeException("Error getting attributes.", e);
			}
			for(String ai : ctx.getEnviroment().keySet()) {
				if (ai.startsWith(att)) candidates.add(ai + cSufix);
			}
			return PREFIX.length() +1;
		} else {
			return -1;
		}
	}

	protected String getCSuffix() {
		return "";
	}


	protected boolean correctAttribute(MBeanAttributeInfo ai) {
		return true;
	}


	protected String attribute(String in) {
		String att = in.substring(PREFIX.length()).trim();
		att = att.replaceFirst("=", " ");
		int ind = att.indexOf(' ');
		if (ind>0) att = att.substring(0, ind);
		return att;
	}

}
