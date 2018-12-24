package fwcd.circuitbuilder.view.grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import fwcd.circuitbuilder.model.grid.CircuitGridModel;
import fwcd.circuitbuilder.model.grid.CircuitItemVisitor;
import fwcd.circuitbuilder.model.grid.cable.CableColor;
import fwcd.circuitbuilder.model.grid.cable.CableModel;
import fwcd.circuitbuilder.model.grid.components.AndModel;
import fwcd.circuitbuilder.model.grid.components.EqvModel;
import fwcd.circuitbuilder.model.grid.components.InverterModel;
import fwcd.circuitbuilder.model.grid.components.LampModel;
import fwcd.circuitbuilder.model.grid.components.LeverModel;
import fwcd.circuitbuilder.model.grid.components.NandModel;
import fwcd.circuitbuilder.model.grid.components.NorModel;
import fwcd.circuitbuilder.model.grid.components.OrModel;
import fwcd.circuitbuilder.model.grid.components.TickingClockModel;
import fwcd.circuitbuilder.model.grid.components.XorModel;
import fwcd.circuitbuilder.view.grid.tools.CircuitTool;
import fwcd.circuitbuilder.view.grid.tools.Place1x1ItemTool;
import fwcd.circuitbuilder.view.grid.tools.PlaceLargeItemTool;
import fwcd.circuitbuilder.view.grid.tools.Screwdriver;
import fwcd.fructose.Option;
import fwcd.fructose.swing.DrawGraphicsButton;
import fwcd.fructose.swing.Renderable;
import fwcd.fructose.swing.SelectedButtonPanel;
import fwcd.fructose.swing.View;

/**
 * A view where the user can select a circuit tool.
 */
public class CircuitToolsPanel implements View {
	private JToolBar view;
	private final CircuitTool[] tools;

	public CircuitToolsPanel(CircuitGridModel model, CircuitGridContext context) {
		CircuitItemVisitor<Option<Image>> imageProvider = context.getSelectedTheme().get().getItemImageProvider();
		tools = new CircuitTool[] {
			new Place1x1ItemTool<>(() -> new CableModel(context.getSelectedColor().get()), imageProvider),
			new Place1x1ItemTool<>(InverterModel::new, imageProvider),
			new Place1x1ItemTool<>(LampModel::new, imageProvider),
			new Place1x1ItemTool<>(LeverModel::new, imageProvider),
			new Place1x1ItemTool<>(TickingClockModel::new, imageProvider),
			new PlaceLargeItemTool<>(OrModel::new, imageProvider),
			new PlaceLargeItemTool<>(AndModel::new, imageProvider),
			new PlaceLargeItemTool<>(XorModel::new, imageProvider),
			new PlaceLargeItemTool<>(EqvModel::new, imageProvider),
			new PlaceLargeItemTool<>(NandModel::new, imageProvider),
			new PlaceLargeItemTool<>(NorModel::new, imageProvider),
			new Screwdriver()
		};
		
		view = new JToolBar(JToolBar.VERTICAL);
		view.setPreferredSize(new Dimension(50, 10));
		view.setFloatable(false);
		
		SelectedButtonPanel itemsPanel = new SelectedButtonPanel(false, Color.LIGHT_GRAY);
		
		for (CircuitTool tool : tools) {
			JButton button = tool.getImage()
				.map(ImageIcon::new)
				.map(JButton::new)
				.orElseGet(() -> new JButton(tool.getName()));
			itemsPanel.add(button, () -> context.getSelectedTool().set(Option.of(tool)));
		}
		
		view.add(itemsPanel.getComponent());
		
		SelectedButtonPanel colorsPanel = new SelectedButtonPanel(false, Color.LIGHT_GRAY);
		
		for (CableColor color : CableColor.values()) {
			Renderable circle = (g2d, canvasSize) -> {
				g2d.setColor(color.getColor(255).asAWTColor());
				
				int w = (int) canvasSize.getWidth();
				int h = (int) canvasSize.getHeight();
				int iconSize = Math.min(w, h);
				
				g2d.fillOval((w / 2) - (iconSize / 2), (h / 2) - (iconSize / 2), iconSize, iconSize);
			};
			
			JButton button = new DrawGraphicsButton(new Dimension(24, 24), circle);
			colorsPanel.add(button, () -> context.getSelectedColor().set(color));
		}
		
		view.add(colorsPanel.getComponent());
	}
	
	@Override
	public JComponent getComponent() {
		return view;
	}
}
