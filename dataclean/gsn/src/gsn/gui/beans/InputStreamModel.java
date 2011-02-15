package gsn.gui.beans;

import gsn.beans.InputStream;
import gsn.beans.StreamSource;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.list.ArrayListModel;

public class InputStreamModel extends Model {
	public static final String PROPERTY_INPUT_STREAM_NAME = "inputStreamName";

	public static final String PROPERTY_COUNT = "count";

	public static final String PROPERTY_RATE = "rate";

	public static final String PROPERTY_QUERY = "query";

	private String inputStreamName;

	private Long count;

	private int rate;

	private String query;

	private ArrayListModel sources;

	public InputStreamModel() {
		sources = new ArrayListModel();
//		count = Long.MAX_VALUE;
	}

	public InputStreamModel(InputStream inputStream) {
		inputStreamName = inputStream.getInputStreamName();
		count = inputStream.getCount();
		
		if(count == Long.MAX_VALUE)
			count = null;
		
		rate = inputStream.getRate();
		query = inputStream.getQuery();
		sources = new ArrayListModel();
		addStreamSourceList(inputStream.getSources());
	}

	private void addStreamSourceList(StreamSource[] streamSources) {
		for (int i = 0; i < streamSources.length; i++) {
			sources.add(new StreamSourceModel(streamSources[i]));
		}
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		Long oldCount = getCount();
		this.count = count;
		firePropertyChange(PROPERTY_COUNT, oldCount, count);
	}

	public String getInputStreamName() {
		return inputStreamName;
	}

	public void setInputStreamName(String inputStreamName) {
		String oldName = getInputStreamName();
		this.inputStreamName = inputStreamName;
		firePropertyChange(PROPERTY_INPUT_STREAM_NAME, oldName, inputStreamName);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		String oldQuery = getQuery();
		this.query = query;
		firePropertyChange(PROPERTY_QUERY, oldQuery, query);
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		int oldRate = getRate();
		this.rate = rate;
		firePropertyChange(PROPERTY_RATE, oldRate, rate);
	}

	public ArrayListModel getSources() {
		return sources;
	}

	public void setSources(ArrayListModel sources) {
		this.sources = sources;
	}

	public void addSource(StreamSourceModel streamSourceModel) {
		sources.add(streamSourceModel);
	}

	public void removeSource(StreamSourceModel streamSourceModel) {
		sources.remove(streamSourceModel);
	}

	public InputStream getInputStream() {
		InputStream inputStream = new InputStream();
		inputStream.setInputStreamName(getInputStreamName());
		inputStream.setCount(getCount());
		inputStream.setRate(getRate());
		inputStream.setQuery(getQuery());

		StreamSource[] streamSources = new StreamSource[sources.size()];
		for (int i = 0; i < sources.size(); i++) {
			streamSources[i] = ((StreamSourceModel)sources.get(i)).getStreamSource();
			streamSources[i].setInputStream(inputStream);
		}
		inputStream.setSources(streamSources);
		return inputStream;
	}

}