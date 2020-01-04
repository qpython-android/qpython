package org.qpython.qpy.texteditor.androidlib.ui.activity;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityDecorator {

	/**
	 * Adds a Menu item either in the Option Menu (pre Honeycomb) or in the
	 * action bar
	 * 
	 * @param menu
	 *            the menu to fill
	 * @param id
	 *            the id of this item
	 * @param title
	 *            the title resource
	 * @param icon
	 *            the drawable (for pre honeycomb option menu)
	 * @return the menu item
	 */
	public static MenuItem addMenuItem(Menu menu, int id, int title, int icon) {
		MenuItem item;

		item = menu.add(0, id, Menu.NONE, title);
		if ((icon != -1) && (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB)) {
			item.setIcon(icon);
		}
		return item;
	}

	/**
	 * Show the menu item as an action in the action bar
	 * 
	 * @param item
	 *            the item to show
	 * @param icon
	 *            the item icon resource
	 * 
	 */
	@TargetApi(11)
	public static void showMenuItemAsAction(MenuItem item, int icon) {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			if (icon != -1) {
				item.setIcon(icon);
			}
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
	}
}
