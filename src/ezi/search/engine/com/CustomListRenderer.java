package ezi.search.engine.com;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

public class CustomListRenderer implements ListCellRenderer {


	@Override
	   public Component getListCellRendererComponent(JList list, Object value, int index,
		        boolean isSelected, boolean cellHasFocus) {

		        JTextArea renderer = new JTextArea(3,10);
		        renderer.setText(value.toString());
		        renderer.setLineWrap(true);
		        list.setFixedCellWidth(60);
		        return renderer;
		   }
	}
