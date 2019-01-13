package fwcd.circuitbuilder.model.grid.timediagram;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import fwcd.circuitbuilder.model.grid.cable.CableNetwork;
import fwcd.circuitbuilder.model.utils.SignalFunctionSegment;
import fwcd.circuitbuilder.model.utils.ValueOverTime;
import fwcd.fructose.Closer;
import fwcd.fructose.EventListenerList;
import fwcd.fructose.ListenerList;
import fwcd.fructose.Observable;
import fwcd.fructose.Option;
import fwcd.fructose.function.Subscription;
import fwcd.fructose.structs.ObservableList;

public class TimeDiagramModel {
	private static final int VALUE_COUNT = 20;
	private final Map<CableNetwork, TimeDiagramRow> rows = new HashMap<>();
	private final Observable<Double> phase = new Observable<>(0.0);
	
	private final EventListenerList<Collection<? extends SignalFunctionSegment>> segmentListeners = new EventListenerList<>();
	private final ListenerList tickListeners = new ListenerList();
	
	public void onAdd(CableNetwork network) {
		Observable<Option<String>> name = network.getName();
		ValueOverTime segment = new ValueOverTime(name.get().orElse(""), VALUE_COUNT);
		rows.put(network, new TimeDiagramRow(segment, name.subscribe(it -> segment.setName(name.get().orElse("Unnamed network")))));
		fireSegmentListeners();
	}
	
	public void onRemove(CableNetwork network) {
		TimeDiagramRow removed = rows.remove(network);
		if (removed != null) {
			removed.close();
			fireSegmentListeners();
		}
	}
	
	public void onClear() {
		Iterator<Map.Entry<CableNetwork, TimeDiagramRow>> entries = rows.entrySet().iterator();
		while (entries.hasNext()) {
			entries.next().getValue().close();
			entries.remove();
		}
		fireSegmentListeners();
	}
	
	public void onTick() {
		for (Map.Entry<CableNetwork, TimeDiagramRow> entry : rows.entrySet()) {
			CableNetwork network = entry.getKey();
			entry.getValue().getSegment().add(network.getStatus().isPowered());
		}
		
		tickListeners.fire();
	}
	
	private void fireSegmentListeners() {
		segmentListeners.fire(rows.entrySet().stream()
			.sorted(Comparator.comparing(Object::hashCode))
			.map(Map.Entry::getValue)
			.map(TimeDiagramRow::getSegment)
			.collect(Collectors.toList()));
	}
	
	public EventListenerList<Collection<? extends SignalFunctionSegment>> getSegmentListeners() { return segmentListeners; }
	
	public ListenerList getTickListeners() { return tickListeners; }
	
	public Observable<Double> getPhase() { return phase; }
	
	private static class TimeDiagramRow implements AutoCloseable {
		private final ValueOverTime segment;
		private final Closer closer;
		
		public TimeDiagramRow(ValueOverTime segment, Subscription... subscriptions) {
			this.segment = segment;
			closer = new Closer(subscriptions);
		}
		
		public ValueOverTime getSegment() { return segment; }
		
		@Override
		public void close() {
			closer.close();
		}
	}
}
